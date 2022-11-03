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

package org.apache.kyuubi.parser.node.runnable

import org.apache.hive.service.rpc.thrift.TRowSet

import org.apache.kyuubi.parser.node.RunnableNode
import org.apache.kyuubi.session.KyuubiSessionImpl
import org.apache.kyuubi.util.ThriftUtils

case class LaunchEngineNode() extends RunnableNode {
  override protected type R = Unit

  override def execute(session: KyuubiSessionImpl): Unit = {
    session.openEngineSession()
  }

  override def name(): String = "Launch Engine Node"

  override def getNextRowSet: TRowSet = ThriftUtils.EMPTY_ROW_SET

  override def shouldRunAsync: Boolean = false
}
