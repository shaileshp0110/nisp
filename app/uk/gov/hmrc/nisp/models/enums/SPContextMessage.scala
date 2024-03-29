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

package uk.gov.hmrc.nisp.models.enums

import play.api.libs.json._

object SPContextMessage extends Enumeration {
  type SPContextMessage = Value
  val ScenarioOne = Value
  val ScenarioTwo = Value
  val ScenarioThree = Value
  val ScenarioFour = Value
  val ScenarioFive = Value
  val ScenarioSix = Value
  val ScenarioSeven = Value
  val ScenarioEight = Value

  implicit val formats = new Format[SPContextMessage] {
    def reads(json: JsValue): JsResult[SPContextMessage] = JsSuccess(SPContextMessage.withName(json.as[String]) )
    def writes(spContextMessage: SPContextMessage): JsValue = JsString(spContextMessage.toString)
  }
}
