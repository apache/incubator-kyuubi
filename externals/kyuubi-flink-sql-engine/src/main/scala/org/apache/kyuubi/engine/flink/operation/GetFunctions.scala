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

package org.apache.kyuubi.engine.flink.operation

import scala.collection.JavaConverters._

import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.{DataTypes, ResultKind, TableEnvironment}
import org.apache.flink.table.catalog.Column
import org.apache.flink.types.Row

import org.apache.kyuubi.engine.flink.result.ResultSet
import org.apache.kyuubi.engine.flink.util.StringUtils.filterPattern
import org.apache.kyuubi.operation.OperationType
import org.apache.kyuubi.operation.meta.ResultSetSchemaConstant._
import org.apache.kyuubi.session.Session

class GetFunctions(
    session: Session,
    catalogName: String,
    schemaName: String,
    functionName: String)
  extends FlinkOperation(OperationType.GET_FUNCTIONS, session) {

  override protected def runInternal(): Unit = {
    try {
      val schemaPattern = toJavaRegex(schemaName)
      val functionPattern = toJavaRegex(functionName)
      var rows: List[Row] = List()
      val tableEnv: TableEnvironment = sessionContext.getExecutionContext.getTableEnvironment
      filterPattern(
        tableEnv.listFunctions().diff(tableEnv.listUserDefinedFunctions()),
        functionPattern)
        .foreach { f =>
          rows = Row.of(null, null, f) +: rows
        }
      tableEnv.listCatalogs()
        .filter { c => StringUtils.isEmpty(catalogName) || c == catalogName }
        .foreach { c =>
          val catalog = tableEnv.getCatalog(c).get()
          filterPattern(catalog.listDatabases().asScala, schemaPattern)
            .foreach { d =>
              filterPattern(catalog.listFunctions(d).asScala, functionPattern)
                .foreach { f =>
                  rows = Row.of(c, d, f) +: rows
                }
            }
        }
      resultSet = ResultSet.builder.resultKind(ResultKind.SUCCESS_WITH_CONTENT)
        .columns(
          Column.physical(FUNCTION_CAT, DataTypes.STRING()),
          Column.physical(FUNCTION_SCHEM, DataTypes.STRING()),
          Column.physical(FUNCTION_NAME, DataTypes.STRING()))
        .data(rows.asJava.toArray(new Array[Row](0)))
        .build
    } catch {
      onError()
    }
  }
}
