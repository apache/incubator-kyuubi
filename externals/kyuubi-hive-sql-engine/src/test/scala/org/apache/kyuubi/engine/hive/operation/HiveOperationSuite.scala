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

package org.apache.kyuubi.engine.hive.operation

import scala.collection.mutable.ArrayBuffer

import org.apache.kyuubi.engine.hive.HiveSQLEngine
import org.apache.kyuubi.operation.HiveJDBCTestHelper
import org.apache.kyuubi.operation.meta.ResultSetSchemaConstant.{TABLE_CAT, TABLE_CATALOG, TABLE_NAME, TABLE_SCHEM, TABLE_TYPE}

class HiveOperationSuite extends HiveJDBCTestHelper {

  override def beforeAll(): Unit = {
    HiveSQLEngine.startEngine()
    super.beforeAll()
  }

  override protected def jdbcUrl: String = {
    "jdbc:hive2://" + HiveSQLEngine.currentEngine.get.frontendServices.head.connectionUrl + "/;"
  }

  test("get catalogs") {
    withJdbcStatement() { statement =>
      val catalogs = statement.getConnection.getMetaData.getCatalogs
      assert(!catalogs.next())
    }
  }

  test("get schemas") {
    withDatabases("test_schema") { statement =>
      statement.execute("CREATE SCHEMA IF NOT EXISTS test_schema")
      val metaData = statement.getConnection.getMetaData
      var resultSet = metaData.getSchemas(null, null)
      val resultSetBuffer = ArrayBuffer[(String, String)]()
      while (resultSet.next()) {
        resultSetBuffer += Tuple2(
          resultSet.getString(TABLE_CATALOG),
          resultSet.getString(TABLE_SCHEM))
      }
      assert(resultSetBuffer.contains(("", "default")))
      assert(resultSetBuffer.contains(("", "test_schema")))

      resultSet = metaData.getSchemas("", "test")
      while (resultSet.next()) {
        assert(resultSet.getString(TABLE_CATALOG) == "")
        assert(resultSet.getString(TABLE_SCHEM) == "test_schema")
      }
    }
  }

  test("get tables") {
    withDatabases("test_schema") { statement =>
      statement.execute("CREATE SCHEMA IF NOT EXISTS test_schema")
      statement.execute("CREATE TABLE IF NOT EXISTS test_schema.test_table(a string)")
      statement.execute(
        "CREATE OR REPLACE VIEW test_schema.test_view AS SELECT  * FROM test_schema.test_table")

      try {
        val meta = statement.getConnection.getMetaData
        var resultSet = meta.getTables(null, null, null, null)
        val resultSetBuffer = ArrayBuffer[(String, String, String, String)]()
        while (resultSet.next()) {
          resultSetBuffer += Tuple4(
            resultSet.getString(TABLE_CAT),
            resultSet.getString(TABLE_SCHEM),
            resultSet.getString(TABLE_NAME),
            resultSet.getString(TABLE_TYPE))
        }
        assert(resultSetBuffer.contains(("", "test_schema", "test_table", "TABLE")))
        assert(resultSetBuffer.contains(("", "test_schema", "test_view", "VIEW")))

        resultSet = meta.getTables("", null, null, null)
        resultSetBuffer.clear()
        while (resultSet.next()) {
          resultSetBuffer += Tuple4(
            resultSet.getString(TABLE_CAT),
            resultSet.getString(TABLE_SCHEM),
            resultSet.getString(TABLE_NAME),
            resultSet.getString(TABLE_TYPE))
        }
        assert(resultSetBuffer.contains(("", "test_schema", "test_table", "TABLE")))
        assert(resultSetBuffer.contains(("", "test_schema", "test_view", "VIEW")))

        resultSet = meta.getTables(null, "test_schema", null, null)
        resultSetBuffer.clear()
        while (resultSet.next()) {
          resultSetBuffer += Tuple4(
            resultSet.getString(TABLE_CAT),
            resultSet.getString(TABLE_SCHEM),
            resultSet.getString(TABLE_NAME),
            resultSet.getString(TABLE_TYPE))
        }
        assert(resultSetBuffer.contains(("", "test_schema", "test_table", "TABLE")))
        assert(resultSetBuffer.contains(("", "test_schema", "test_view", "VIEW")))

        resultSet = meta.getTables(null, null, "test_table", null)
        while (resultSet.next()) {
          assert(resultSet.getString(TABLE_CAT) == "")
          assert(resultSet.getString(TABLE_SCHEM) == "test_schema")
          assert(resultSet.getString(TABLE_NAME) == "test_table")
          assert(resultSet.getString(TABLE_TYPE) == "TABLE")
        }

        resultSet = meta.getTables(null, null, null, Array("VIEW"))
        while (resultSet.next()) {
          assert(resultSet.getString(TABLE_CAT) == "")
          assert(resultSet.getString(TABLE_SCHEM) == "test_schema")
          assert(resultSet.getString(TABLE_NAME) == "test_view")
          assert(resultSet.getString(TABLE_TYPE) == "VIEW")
        }
      } finally {
        statement.execute("DROP VIEW test_schema.test_view")
        statement.execute("DROP TABLE test_schema.test_table")
      }
    }
  }

  test("basic execute statements, create, insert query") {
    withJdbcStatement("hive_engine_test") { statement =>
      statement.execute("CREATE TABLE hive_engine_test(id int, value string) stored as orc")
      statement.execute("INSERT INTO hive_engine_test SELECT 1, '2'")

      val resultSet = statement.executeQuery("SELECT ID, VALUE FROM hive_engine_test")
      assert(resultSet.next())
      assert(resultSet.getInt("ID") === 1)
      assert(resultSet.getString("VALUE") === "2")

      val metaData = resultSet.getMetaData
      assert(metaData.getColumnType(1) === java.sql.Types.INTEGER)
      assert(metaData.getPrecision(1) === 10)
      assert(metaData.getScale(1) === 0)
    }
  }
}
