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

package org.apache.kyuubi.operation

import org.apache.kyuubi.events.KyuubiEvent

case class NoopEvent private (
    handleId: String,
    shouldRunAsync: Boolean,
    state: String,
    eventTime: Long,
    createTime: Long,
    startTime: Long,
    completeTime: Long,
    exception: Option[Throwable],
    sessionId: String,
    sessionUser: String) extends KyuubiEvent {

  override def partitions: Seq[(String, String)] = Nil
}

object NoopEvent {
  def apply(operation: NoopOperation): NoopEvent = {
    val session = operation.getSession
    val status = operation.getStatus
    new NoopEvent(
      operation.getHandle.identifier.toString,
      operation.shouldRunAsync,
      status.state.name(),
      status.lastModified,
      status.create,
      status.start,
      status.completed,
      status.exception,
      session.handle.identifier.toString,
      session.user)
  }
}
