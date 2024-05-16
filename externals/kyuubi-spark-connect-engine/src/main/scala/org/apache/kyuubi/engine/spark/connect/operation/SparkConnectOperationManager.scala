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
package org.apache.kyuubi.engine.spark.connect.operation

import java.util
import io.grpc.stub.StreamObserver
import org.apache.kyuubi.KyuubiSQLException
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.engine.spark.connect.session.SparkConnectSessionImpl
import org.apache.kyuubi.grpc.operation.{GrpcOperation, GrpcOperationManager, OperationKey}
import org.apache.kyuubi.grpc.session.GrpcSession
import org.apache.kyuubi.operation.{Operation, OperationManager}
import org.apache.kyuubi.operation.log.LogDivertAppender
import org.apache.kyuubi.service.AbstractService
import org.apache.kyuubi.session.Session
import org.apache.spark.connect.proto.{ConfigRequest, ConfigResponse}

class SparkConnectOperationManager private (name: String) extends GrpcOperationManager(name) {

  def this() = this(classOf[SparkConnectOperationManager].getSimpleName)

  private def skipOperationLog: Boolean = false
  override def initialize(conf: KyuubiConf): Unit = {
    LogDivertAppender.initialize(skipOperationLog)
  }
  def newConfigOperation(
                          session: SparkConnectSessionImpl,
                          request: ConfigRequest,
                          response: StreamObserver[ConfigResponse]): GrpcOperation = {
    val configOperation = new ConfigOperation(session, request, response)
    addOperation(configOperation)
  }

  override def close(opKey: OperationKey): Unit = {

  }
}
