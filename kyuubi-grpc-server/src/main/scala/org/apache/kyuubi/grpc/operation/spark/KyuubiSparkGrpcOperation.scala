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

package org.apache.kyuubi.grpc.operation.spark

import io.grpc.ManagedChannel

import org.apache.kyuubi.grpc.operation.KyuubiGrpcOperation
import org.apache.kyuubi.grpc.session.KyuubiGrpcSession
import org.apache.kyuubi.shade.org.apache.spark.connect.proto.SparkConnectServiceGrpc
import org.apache.kyuubi.shade.org.apache.spark.connect.proto.SparkConnectServiceGrpc.SparkConnectServiceStub

abstract class KyuubiSparkGrpcOperation(channel: ManagedChannel, session: KyuubiGrpcSession)
  extends KyuubiGrpcOperation(session) {

  def stub: SparkConnectServiceStub = SparkConnectServiceGrpc.newStub(channel)
}
