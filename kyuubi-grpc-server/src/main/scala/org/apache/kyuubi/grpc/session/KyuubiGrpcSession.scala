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

package org.apache.kyuubi.grpc.session
import scala.util.Random

import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import org.apache.spark.connect.proto._

import org.apache.kyuubi.grpc.event.KyuubiGrpcSessionEventsManager
import org.apache.kyuubi.grpc.events.SessionEventsManager
import org.apache.kyuubi.grpc.operation.{GrpcOperation, OperationKey}
import org.apache.kyuubi.grpc.utils.SystemClock

class KyuubiGrpcSession(
    userId: String,
    sessionManager: KyuubiGrpcSessionManager)
  extends AbstractGrpcSession(userId) {

  def config(
      request: ConfigRequest,
      responseObserver: StreamObserver[ConfigResponse],
      channel: ManagedChannel): Unit = {
    val operation = sessionManager.grpcOperationManager
      .newSparkConfigOperation(channel, this, request, responseObserver)
    runGrpcOperation(operation)
  }

  override def name: Option[String] = Some("KyuubiGrpcSessionImpl")

  override def serverSessionId: String = Random.nextString(10)

  override def sessionManager: GrpcSessionManager[KyuubiGrpcSession] = sessionManager

  override def sessionEventsManager: SessionEventsManager =
    new KyuubiGrpcSessionEventsManager(this, new SystemClock())

  override def getOperation(operationKey: OperationKey): GrpcOperation =
    sessionManager.grpcOperationManager.getOperation(operationKey)
}
