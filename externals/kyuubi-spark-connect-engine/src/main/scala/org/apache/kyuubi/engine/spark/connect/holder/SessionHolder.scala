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
package org.apache.kyuubi.engine.spark.connect.holder

import com.google.common.cache.{Cache, CacheBuilder}
import org.apache.kyuubi.Logging
import org.apache.kyuubi.engine.spark.connect.grpc.proto.Relation
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan

case class SessionKey(userId: String, sessionId: String)

case class SessionHolder(userId: String, sessionId: String, session: SparkSession)
  extends Logging {

  private lazy val planCache: Option[Cache[Relation, LogicalPlan]] = {
    Some(
      CacheBuilder
        .newBuilder()
        .maximumSize(5)
        .build[Relation, LogicalPlan]())
  }

  private val startTimeMs: Long = System.currentTimeMillis()
  @volatile private var lastAccessTimeMs: Long = System.currentTimeMillis()
  @volatile private var closedTimeMs: Option[Long] = None
  @volatile private var customInactiveTimeoutMs: Option[Long] = None

}
