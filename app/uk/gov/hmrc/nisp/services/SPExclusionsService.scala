/*
 * Copyright 2015 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.nisp.services

import uk.gov.hmrc.nisp.config.ApplicationConfig
import uk.gov.hmrc.nisp.models.SPExclusionsModel
import play.Logger
import uk.gov.hmrc.nisp.models.enums.SPExclusion
import uk.gov.hmrc.nisp.models.enums.SPExclusion.SPExclusion
import uk.gov.hmrc.nisp.models.nps.{NpsSchemeMembership, NpsLiability, NpsDate}
import uk.gov.hmrc.nisp.utils.{FunctionHelper, NISPConstants}

object SPExclusionsService {
  def apply(numberOfQualifyingYears: Int, countryCode: Int, mwrre: Boolean, sex: String, dateOfBirth: NpsDate,
            schemeMemberships: List[NpsSchemeMembership], dateOfDeath: Option[NpsDate],
            nino: String, liabilities: List[NpsLiability], applicationConfig: ApplicationConfig): SPExclusionsService =
    new SPExclusionsService(numberOfQualifyingYears, countryCode, mwrre, sex, dateOfBirth, schemeMemberships, dateOfDeath, nino, liabilities, applicationConfig)
}

class SPExclusionsService(numberOfQualifyingYears: Int, countryCode: Int, mwrre: Boolean, sex: String, dateOfBirth: NpsDate,
                           schemeMemberships: List[NpsSchemeMembership], dateOfDeath: Option[NpsDate],
                          nino: String, liabilities: List[NpsLiability], applicationConfig: ApplicationConfig) {
  def getSPExclusions: Option[SPExclusionsModel] = {

    val allExclusions = FunctionHelper.composeAll(allRules)

    allExclusions(List()) match {
      case List() => None
      case listOfExclusions =>
        val formattedExclusions = listOfExclusions.map(_.toString).mkString(",")
        Logger.info(s"User excluded: $formattedExclusions")
        Some(SPExclusionsModel(listOfExclusions.distinct))
    }
  }

  val checkAbroad = (exclusionsList: List[SPExclusion]) => {
    countryCode match {
      case NISPConstants.countryNotSpecified => exclusionsList
      case NISPConstants.countryGB => exclusionsList
      case NISPConstants.countryNI => exclusionsList
      case NISPConstants.countryEngland => exclusionsList
      case NISPConstants.countryScotland => exclusionsList
      case NISPConstants.countryWales => exclusionsList
      case NISPConstants.countryIsleOfMan => exclusionsList
      case _ => SPExclusion.Abroad :: exclusionsList
    }
  }

  val checkIOMLiabilities = (exclusionList: List[SPExclusion]) => {
    liabilities.find( x => x.liabilityType==NISPConstants.isleOfManLiability) match {
      case Some(_) => SPExclusion.IOM :: exclusionList
      case None => exclusionList
    }
  }

  val checkMWRRE = (exclusionList: List[SPExclusion]) => {
    mwrre match {
      case true => SPExclusion.MWRRE :: exclusionList
      case _ => exclusionList
    }
  }

  val checkDateOfBirth = (exclusionList: List[SPExclusion]) => {
    sex.toLowerCase.trim match {
      case "m" =>
        if(dateOfBirth.localDate.compareTo(NISPConstants.nispMaleCutoffDOB) < 0) {
          List(SPExclusion.CustomerTooOld)
        } else {
          exclusionList
        }
      case _ =>
        if(dateOfBirth.localDate.compareTo(NISPConstants.nispFemaleCutoffDOB) < 0) {
          List(SPExclusion.CustomerTooOld)
        } else {
          exclusionList
        }
    }
  }

  val checkContractedOut = (exclusionList: List[SPExclusion]) => {
    (schemeMemberships.length, applicationConfig.excludeContractedOut) match {
      case (0, _) => exclusionList
      case (_, false) => exclusionList
      case _ => SPExclusion.ContractedOut :: exclusionList
    }
  }

  val checkDead = (exclusionList: List[SPExclusion]) => {
    dateOfDeath match {
      case Some(_) => SPExclusion.Dead :: exclusionList
      case _ => exclusionList
    }
  }

  val allRules = List(checkDateOfBirth, checkAbroad, checkMWRRE, checkContractedOut, checkDead, checkIOMLiabilities)
}
