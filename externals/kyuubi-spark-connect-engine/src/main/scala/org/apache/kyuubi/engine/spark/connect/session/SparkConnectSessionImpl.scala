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
package org.apache.kyuubi.engine.spark.connect.session

import io.grpc.stub.StreamObserver
import org.apache.spark.sql.SparkSession

import org.apache.kyuubi.Logging
import org.apache.kyuubi.config.KyuubiReservedKeys.KYUUBI_SESSION_HANDLE_KEY
import org.apache.kyuubi.engine.spark.connect.grpc.AbstractGrpcSession
import org.apache.kyuubi.engine.spark.connect.grpc.proto.{ConfigRequest, ConfigResponse}
import org.apache.kyuubi.engine.spark.connect.operation.SparkConnectOperationManager
import org.apache.kyuubi.session.{SessionHandle, SessionManager}

class SparkConnectSessionImpl(
    userId: String,
    sessionId: String,
    sessionManager: SessionManager,
    val spark: SparkSession)
  extends AbstractGrpcSession with Logging {

  private val operationManager: SparkConnectOperationManager =
    sessionManager.operationManager.asInstanceOf[SparkConnectOperationManager]
  override val handle: SessionHandle =
    conf.get(KYUUBI_SESSION_HANDLE_KEY).map(SessionHandle.fromUUID).getOrElse(SessionHandle())

  def config(request: ConfigRequest, response: StreamObserver[ConfigResponse]): Unit = {
    val operation = operationManager.newConfigOperation(this, request, response)
    runOperation(operation)
  }

}
