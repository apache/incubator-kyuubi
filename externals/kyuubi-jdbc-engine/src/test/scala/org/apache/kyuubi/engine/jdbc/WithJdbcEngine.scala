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
package org.apache.kyuubi.engine.jdbc

import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.operation.HiveJDBCTestHelper

trait WithJdbcEngine extends WithJdbcServerContainer with HiveJDBCTestHelper {

  protected var engine: JdbcSqlEngine = _
  protected var connectionUrl: String = _

  protected val kyuubiConf: KyuubiConf = KyuubiConf()

  def withKyuubiConf: Map[String, String]

  override def beforeAll(): Unit = {
    startJdbcEngine()
    super.beforeAll()
  }

  def startJdbcEngine(): Unit = {
    withKyuubiConf.foreach { case (k, v) =>
      System.setProperty(k, v)
      kyuubiConf.set(k, v)
    }
    JdbcSqlEngine.startEngine()
    engine = JdbcSqlEngine.currentEngine.get
    connectionUrl = engine.frontendServices.head.connectionUrl
  }

  protected def getJdbcUrl: String = s"jdbc:hive2://$connectionUrl/;"

  override protected def jdbcUrl: String = getJdbcUrl

}
