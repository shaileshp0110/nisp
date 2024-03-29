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

package uk.gov.hmrc.nisp.helpers

import com.codahale.metrics.Timer
import com.codahale.metrics.Timer.Context
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.nisp.metrics.Metrics
import uk.gov.hmrc.nisp.models.enums.APITypes.APITypes


object MockMetrics extends Metrics with MockitoSugar {

  val fakeTimerContext = mock[Timer.Context]

  override def startTimer(api: APITypes): Context = fakeTimerContext

  override def incrementSuccessCounter(api: APITypes): Unit = {}

  override def incrementFailedCounter(api: APITypes): Unit = {}
}
