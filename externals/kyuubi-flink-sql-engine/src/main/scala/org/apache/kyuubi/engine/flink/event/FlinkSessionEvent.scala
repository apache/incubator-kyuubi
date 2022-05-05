/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kyuubi.engine.flink.event

import org.apache.kyuubi.Utils
import org.apache.kyuubi.engine.flink.session.FlinkSessionImpl
import org.apache.kyuubi.events.KyuubiEvent

case class FlinkSessionEvent(
    sessionId: String,
    username: String,
    ip: String,
    startTime: Long,
    var endTime: Long = -1L,
    var totalOperations: Int = 0) extends KyuubiEvent {

  override def partitions: Seq[(String, String)] = {
    ("day", Utils.getDateFromTimestamp(startTime)) :: Nil
  }
}

object FlinkSessionEvent {

  def apply(session: FlinkSessionImpl): FlinkSessionEvent = {
    FlinkSessionEvent(
      session.handle.identifier.toString,
      session.user,
      session.ipAddress,
      session.createTime)
  }
}
