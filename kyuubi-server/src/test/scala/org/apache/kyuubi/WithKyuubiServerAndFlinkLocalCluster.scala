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

package org.apache.kyuubi

import scala.sys.process._

import org.apache.kyuubi.engine.flink.FlinkProcessBuilder

trait WithKyuubiServerAndFlinkLocalCluster extends WithKyuubiServer {

  private lazy val FLINK_HOME: String =
    new FlinkProcessBuilder(Utils.currentUser, conf).FLINK_HOME

  override def beforeAll(): Unit = {
    s"$FLINK_HOME/bin/start-cluster.sh".!
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    s"$FLINK_HOME/bin/stop-cluster.sh".!
  }
}
