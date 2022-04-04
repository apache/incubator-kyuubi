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
package org.apache.kyuubi.session

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._

class MemorySessionStore extends SessionStore {

  private val handleToSession = new ConcurrentHashMap[SessionHandle, Session]

  override def save(sessionHandle: SessionHandle, session: Session): Unit = {
    handleToSession.put(sessionHandle, session)
  }

  override def get(sessionHandle: SessionHandle): Session = {
    handleToSession.get(sessionHandle)
  }

  override def remove(sessionHandle: SessionHandle): Session = {
    handleToSession.remove(sessionHandle)
  }

  override def sessionCount(): Int = {
    handleToSession.size()
  }

  override def getAllSessions(): Iterable[Session] = {
    handleToSession.values().asScala
  }
}
