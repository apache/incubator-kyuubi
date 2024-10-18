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

package org.apache.kyuubi.engine.trino.operation

import scala.collection.JavaConverters._

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime

import org.apache.kyuubi.config.KyuubiConf.{ENGINE_TRINO_CONNECTION_CATALOG, SESSION_PROGRESS_ENABLE}
import org.apache.kyuubi.shaded.hive.service.rpc.thrift.{TExecuteStatementReq, TGetOperationStatusReq}

class TrinoOperationProgressSuite extends TrinoOperationSuite {
  override def withKyuubiConf: Map[String, String] = Map(
    ENGINE_TRINO_CONNECTION_CATALOG.key -> "memory",
    SESSION_PROGRESS_ENABLE.key -> "true")

  test("get operation progress") {
    val sql = "select date '2011-11-11' - interval '1' day"

    withSessionHandle { (client, handle) =>
      val req = new TExecuteStatementReq()
      req.setStatement(sql)
      req.setRunAsync(true)
      req.setSessionHandle(handle)
      val resp = client.ExecuteStatement(req)
      //scalastyle:off
      eventually(Timeout(25.seconds)) {
        val statusReq = new TGetOperationStatusReq(resp.getOperationHandle)
        val statusResp = client.GetOperationStatus(statusReq)
        val headers = statusResp.getProgressUpdateResponse.getHeaderNames
        val progress = statusResp.getProgressUpdateResponse.getProgressedPercentage
        val rows = statusResp.getProgressUpdateResponse.getRows
        val footerSummary = statusResp.getProgressUpdateResponse.getFooterSummary
        val status = statusResp.getProgressUpdateResponse.getStatus
        println(rows.get(0).asScala)
        println("headers:" + String.join(",", headers))
        println("progress: " + progress)
      }
    }
  }
}
