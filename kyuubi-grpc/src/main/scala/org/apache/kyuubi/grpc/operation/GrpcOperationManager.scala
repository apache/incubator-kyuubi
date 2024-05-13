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
package org.apache.kyuubi.grpc.operation

import java.util.concurrent._

import scala.collection.JavaConverters._

import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.operation.log.LogDivertAppender
import org.apache.kyuubi.service.AbstractService

/**
 * The [[GrpcOperationManager]] manages all the grpc operations during their lifecycle
 */
abstract class GrpcOperationManager(name: String) extends AbstractService(name) {

  private val keyToOperations = new ConcurrentHashMap[OperationKey, GrpcOperation]
  private val operationsLock = new Object

  private var lastExecutionTimeMs: Option[Long] = Some(System.currentTimeMillis())

  def getOperationCount: Int = keyToOperations.size()

  def allOperations(): Iterable[GrpcOperation] = keyToOperations.values().asScala

  override def initialize(conf: KyuubiConf): Unit = {
    LogDivertAppender.initialize()
    super.initialize(conf)
  }

  def close(opKey: OperationKey)

}
