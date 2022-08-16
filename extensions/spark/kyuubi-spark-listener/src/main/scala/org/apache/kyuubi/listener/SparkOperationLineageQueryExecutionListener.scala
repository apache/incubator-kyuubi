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

package org.apache.kyuubi.listener

import org.apache.spark.sql.execution.QueryExecution
import org.apache.spark.sql.util.QueryExecutionListener

import org.apache.kyuubi.events.{EventBus, LineageEventHandlerRegister, OperationLineageEvent}
import org.apache.kyuubi.helper.SparkSQLLineageParseHelper

class SparkOperationLineageQueryExecutionListener extends QueryExecutionListener {

  override def onSuccess(funcName: String, qe: QueryExecution, durationNs: Long): Unit = {
    LineageEventHandlerRegister.register(qe.sparkSession)
    val lineage =
      SparkSQLLineageParseHelper(qe.sparkSession).transformToLineage(qe.id, qe.optimizedPlan)
    val eventTime = System.currentTimeMillis()
    EventBus.post(OperationLineageEvent(qe.id, eventTime, lineage, None))
  }

  override def onFailure(funcName: String, qe: QueryExecution, exception: Exception): Unit = {
    val eventTime = System.currentTimeMillis()
    EventBus.post(OperationLineageEvent(qe.id, eventTime, None, Some(exception)))
  }
}
