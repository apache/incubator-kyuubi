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
package org.apache.kyuubi.engine.jdbc.dialect
import java.sql.{Connection, Statement}
import java.util

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

import org.apache.commons.lang3.StringUtils

import org.apache.kyuubi.engine.jdbc.doris.{DorisSchemaHelper, DorisTRowSetGenerator}
import org.apache.kyuubi.engine.jdbc.schema.{JdbcTRowSetGenerator, SchemaHelper}
import org.apache.kyuubi.operation.meta.ResultSetSchemaConstant._
import org.apache.kyuubi.session.Session

class DorisDialect extends MySQLDialect {

  override def name(): String = "doris"

  override def getTRowSetGenerator(): JdbcTRowSetGenerator = new DorisTRowSetGenerator

  override def getSchemaHelper(): SchemaHelper = new DorisSchemaHelper
}
