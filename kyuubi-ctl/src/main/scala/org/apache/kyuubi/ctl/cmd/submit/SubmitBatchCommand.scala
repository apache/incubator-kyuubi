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
package org.apache.kyuubi.ctl.cmd.submit

import java.util.{ArrayList, HashMap}

import scala.collection.JavaConverters._

import org.apache.kyuubi.client.{BatchRestApi, KyuubiRestClient}
import org.apache.kyuubi.client.api.v1.dto.{Batch, BatchRequest, OperationLog}
import org.apache.kyuubi.ctl.{CliConfig, RestClientFactory}
import org.apache.kyuubi.ctl.cmd.Command

class SubmitBatchCommand(cliConfig: CliConfig) extends Command(cliConfig) {

  private var map: HashMap[String, Object] = null

  override def validateArguments(): Unit = {
    map = readConfig()
  }

  override def run(): Unit = {
    val kyuubiRestClient: KyuubiRestClient =
      RestClientFactory.getKyuubiRestClient(cliArgs, map, conf)
    val batchRestApi: BatchRestApi = new BatchRestApi(kyuubiRestClient)

    val request = map.get("request").asInstanceOf[HashMap[String, Object]]
    val batchRequest = new BatchRequest(
      map.get("batchType").asInstanceOf[String],
      request.get("resource").asInstanceOf[String],
      request.get("className").asInstanceOf[String],
      request.get("name").asInstanceOf[String],
      request.get("config").asInstanceOf[HashMap[String, String]],
      request.get("args").asInstanceOf[ArrayList[String]])

    var batch: Batch = batchRestApi.createBatch(batchRequest)
    val batchId = batch.getId
    var log: OperationLog = null
    var done = false
    while (!done) {
      log = batchRestApi.getBatchLocalLog(
        batchId,
        -1,
        20)
      log.getLogRowSet.asScala.foreach(x => info(x))

      batch = batchRestApi.getBatchById(batchId)
      if (log.getLogRowSet.size() == 0 && batch.getState() != "RUNNING") {
        done = true
      }
    }

    kyuubiRestClient.close()
  }

}
