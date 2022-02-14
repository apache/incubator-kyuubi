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
import scala.collection.mutable.ArrayBuffer

import org.apache.hive.service.rpc.thrift.TCancelOperationReq
import org.apache.hive.service.rpc.thrift.TCloseOperationReq
import org.apache.hive.service.rpc.thrift.TCloseSessionReq
import org.apache.hive.service.rpc.thrift.TExecuteStatementReq
import org.apache.hive.service.rpc.thrift.TFetchOrientation
import org.apache.hive.service.rpc.thrift.TFetchResultsReq
import org.apache.hive.service.rpc.thrift.TGetOperationStatusReq
import org.apache.hive.service.rpc.thrift.TOpenSessionReq
import org.apache.hive.service.rpc.thrift.TOperationState
import org.apache.hive.service.rpc.thrift.TStatusCode

import org.apache.kyuubi.config.KyuubiConf.ENGINE_TRINO_CONNECTION_CATALOG
import org.apache.kyuubi.engine.trino.WithTrinoEngine
import org.apache.kyuubi.operation.HiveJDBCTestHelper
import org.apache.kyuubi.operation.meta.ResultSetSchemaConstant._

class TrinoOperationSuite extends WithTrinoEngine with HiveJDBCTestHelper {
  override def withKyuubiConf: Map[String, String] = Map(
    ENGINE_TRINO_CONNECTION_CATALOG.key -> "memory")

  // use default schema, do not set to 'default', since withSessionHandle strip suffix '/;'
  override protected val schema = ""

  override protected def jdbcUrl: String = getJdbcUrl

  test("trino - get catalogs") {
    withJdbcStatement() { statement =>
      val meta = statement.getConnection.getMetaData
      val catalogs = meta.getCatalogs
      val resultSetBuffer = ArrayBuffer[String]()
      while (catalogs.next()) {
        resultSetBuffer += catalogs.getString(TABLE_CAT)
      }
      assert(resultSetBuffer.contains("memory"))
      assert(resultSetBuffer.contains("system"))
    }
  }

  test("trino - get table types") {
    withJdbcStatement() { statement =>
      val meta = statement.getConnection.getMetaData
      val types = meta.getTableTypes
      val expected = Set("TABLE", "VIEW").toIterator
      while (types.next()) {
        assert(types.getString(TABLE_TYPE) === expected.next())
      }
      assert(!expected.hasNext)
      assert(!types.next())
    }
  }

  test("trino - get schemas") {
    case class SchemaWithCatalog(catalog: String, schema: String)

    withJdbcStatement() { statement =>
      statement.execute("CREATE SCHEMA IF NOT EXISTS memory.test_escape_1")
      statement.execute("CREATE SCHEMA IF NOT EXISTS memory.test2escape_1")
      statement.execute("CREATE SCHEMA IF NOT EXISTS memory.test_escape11")

      val meta = statement.getConnection.getMetaData
      val resultSetBuffer = ArrayBuffer[SchemaWithCatalog]()

      val schemas1 = meta.getSchemas(null, null)
      while (schemas1.next()) {
        resultSetBuffer +=
          SchemaWithCatalog(schemas1.getString(TABLE_CATALOG), schemas1.getString(TABLE_SCHEM))
      }
      assert(resultSetBuffer.contains(SchemaWithCatalog("memory", "information_schema")))
      assert(resultSetBuffer.contains(SchemaWithCatalog("system", "information_schema")))

      val schemas2 = meta.getSchemas("memory", null)
      resultSetBuffer.clear()
      while (schemas2.next()) {
        resultSetBuffer +=
          SchemaWithCatalog(schemas2.getString(TABLE_CATALOG), schemas2.getString(TABLE_SCHEM))
      }
      assert(resultSetBuffer.contains(SchemaWithCatalog("memory", "default")))
      assert(resultSetBuffer.contains(SchemaWithCatalog("memory", "information_schema")))
      assert(!resultSetBuffer.exists(f => f.catalog == "system"))

      val schemas3 = meta.getSchemas(null, "sf_")
      resultSetBuffer.clear()
      while (schemas3.next()) {
        resultSetBuffer +=
          SchemaWithCatalog(schemas3.getString(TABLE_CATALOG), schemas3.getString(TABLE_SCHEM))
      }
      assert(resultSetBuffer.contains(SchemaWithCatalog("tpcds", "sf1")))
      assert(!resultSetBuffer.contains(SchemaWithCatalog("tpcds", "sf10")))

      val schemas4 = meta.getSchemas(null, "sf%")
      resultSetBuffer.clear()
      while (schemas4.next()) {
        resultSetBuffer +=
          SchemaWithCatalog(schemas4.getString(TABLE_CATALOG), schemas4.getString(TABLE_SCHEM))
      }
      assert(resultSetBuffer.contains(SchemaWithCatalog("tpcds", "sf1")))
      assert(resultSetBuffer.contains(SchemaWithCatalog("tpcds", "sf10")))
      assert(resultSetBuffer.contains(SchemaWithCatalog("tpcds", "sf100")))
      assert(resultSetBuffer.contains(SchemaWithCatalog("tpcds", "sf1000")))

      // test escape the second '_'
      val schemas5 = meta.getSchemas("memory", "test_escape\\_1")
      resultSetBuffer.clear()
      while (schemas5.next()) {
        resultSetBuffer +=
          SchemaWithCatalog(schemas5.getString(TABLE_CATALOG), schemas5.getString(TABLE_SCHEM))
      }
      assert(resultSetBuffer.contains(SchemaWithCatalog("memory", "test_escape_1")))
      assert(resultSetBuffer.contains(SchemaWithCatalog("memory", "test2escape_1")))
      assert(!resultSetBuffer.contains(SchemaWithCatalog("memory", "test_escape11")))

      statement.execute("DROP SCHEMA memory.test_escape_1")
      statement.execute("DROP SCHEMA memory.test2escape_1")
      statement.execute("DROP SCHEMA memory.test_escape11")
    }
  }

  test("execute statement -  select decimal") {
    withJdbcStatement() { statement =>
      val resultSet = statement.executeQuery("SELECT DECIMAL '1.2' as col1, DECIMAL '1.23' AS col2")
      assert(resultSet.next())
      assert(resultSet.getBigDecimal("col1") === new java.math.BigDecimal("1.2"))
      assert(resultSet.getBigDecimal("col2") === new java.math.BigDecimal("1.23"))
      val metaData = resultSet.getMetaData
      assert(metaData.getColumnType(1) === java.sql.Types.DECIMAL)
      assert(metaData.getColumnType(2) === java.sql.Types.DECIMAL)
      assert(metaData.getPrecision(1) == 2)
      assert(metaData.getPrecision(2) == 3)
      assert(metaData.getScale(1) == 1)
      assert(metaData.getScale(2) == 2)
    }
  }

  test("test fetch orientation") {
    val sql = "SELECT id FROM (VALUES 0, 1) as t(id)"

    withSessionHandle { (client, handle) =>
      val req = new TExecuteStatementReq()
      req.setSessionHandle(handle)
      req.setStatement(sql)
      val tExecuteStatementResp = client.ExecuteStatement(req)
      val opHandle = tExecuteStatementResp.getOperationHandle
      waitForOperationToComplete(client, opHandle)

      // fetch next from before first row
      val tFetchResultsReq1 = new TFetchResultsReq(opHandle, TFetchOrientation.FETCH_NEXT, 1)
      val tFetchResultsResp1 = client.FetchResults(tFetchResultsReq1)
      assert(tFetchResultsResp1.getStatus.getStatusCode === TStatusCode.SUCCESS_STATUS)
      val idSeq1 = tFetchResultsResp1.getResults.getColumns.get(0).getI32Val.getValues.asScala.toSeq
      assertResult(Seq(0L))(idSeq1)

      // fetch next from first row
      val tFetchResultsReq2 = new TFetchResultsReq(opHandle, TFetchOrientation.FETCH_NEXT, 1)
      val tFetchResultsResp2 = client.FetchResults(tFetchResultsReq2)
      assert(tFetchResultsResp2.getStatus.getStatusCode === TStatusCode.SUCCESS_STATUS)
      val idSeq2 = tFetchResultsResp2.getResults.getColumns.get(0).getI32Val.getValues.asScala.toSeq
      assertResult(Seq(1L))(idSeq2)

      // fetch prior from second row, expected got first row
      val tFetchResultsReq3 = new TFetchResultsReq(opHandle, TFetchOrientation.FETCH_PRIOR, 1)
      val tFetchResultsResp3 = client.FetchResults(tFetchResultsReq3)
      assert(tFetchResultsResp3.getStatus.getStatusCode === TStatusCode.SUCCESS_STATUS)
      val idSeq3 = tFetchResultsResp3.getResults.getColumns.get(0).getI32Val.getValues.asScala.toSeq
      assertResult(Seq(0L))(idSeq3)

      // fetch first
      val tFetchResultsReq4 = new TFetchResultsReq(opHandle, TFetchOrientation.FETCH_FIRST, 3)
      val tFetchResultsResp4 = client.FetchResults(tFetchResultsReq4)
      assert(tFetchResultsResp4.getStatus.getStatusCode === TStatusCode.SUCCESS_STATUS)
      val idSeq4 = tFetchResultsResp4.getResults.getColumns.get(0).getI32Val.getValues.asScala.toSeq
      assertResult(Seq(0L, 1L))(idSeq4)
    }
  }

  test("get operation status") {
    val sql = "select date '2011-11-11' - interval '1' day"

    withSessionHandle { (client, handle) =>
      val req = new TExecuteStatementReq()
      req.setSessionHandle(handle)
      req.setStatement(sql)
      val tExecuteStatementResp = client.ExecuteStatement(req)
      val opHandle = tExecuteStatementResp.getOperationHandle
      val tGetOperationStatusReq = new TGetOperationStatusReq()
      tGetOperationStatusReq.setOperationHandle(opHandle)
      val resp = client.GetOperationStatus(tGetOperationStatusReq)
      val status = resp.getStatus
      assert(status.getStatusCode === TStatusCode.SUCCESS_STATUS)
      assert(resp.getOperationState === TOperationState.FINISHED_STATE)
      assert(resp.isHasResultSet)
    }
  }

  test("basic open | execute | close") {
    withThriftClient { client =>
      val req = new TOpenSessionReq()
      req.setUsername("hongdd")
      req.setPassword("anonymous")
      val tOpenSessionResp = client.OpenSession(req)

      val tExecuteStatementReq = new TExecuteStatementReq()
      tExecuteStatementReq.setSessionHandle(tOpenSessionResp.getSessionHandle)
      tExecuteStatementReq.setRunAsync(true)
      tExecuteStatementReq.setStatement("show session")
      val tExecuteStatementResp = client.ExecuteStatement(tExecuteStatementReq)

      val operationHandle = tExecuteStatementResp.getOperationHandle
      waitForOperationToComplete(client, operationHandle)
      val tFetchResultsReq = new TFetchResultsReq()
      tFetchResultsReq.setOperationHandle(operationHandle)
      tFetchResultsReq.setFetchType(1)
      tFetchResultsReq.setMaxRows(1000)
      val tFetchResultsResp = client.FetchResults(tFetchResultsReq)
      val logs = tFetchResultsResp.getResults.getColumns.get(0).getStringVal.getValues.asScala
      assert(logs.exists(_.contains(classOf[ExecuteStatement].getCanonicalName)))

      tFetchResultsReq.setFetchType(0)
      val tFetchResultsResp1 = client.FetchResults(tFetchResultsReq)
      val rs = tFetchResultsResp1.getResults.getColumns.get(0).getStringVal.getValues.asScala
      assert(rs.contains("aggregation_operator_unspill_memory_limit"))

      val tCloseSessionReq = new TCloseSessionReq()
      tCloseSessionReq.setSessionHandle(tOpenSessionResp.getSessionHandle)
      val tCloseSessionResp = client.CloseSession(tCloseSessionReq)
      assert(tCloseSessionResp.getStatus.getStatusCode === TStatusCode.SUCCESS_STATUS)
    }
  }

  test("not allow to operate closed session or operation") {
    withThriftClient { client =>
      val req = new TOpenSessionReq()
      req.setUsername("hongdd")
      req.setPassword("anonymous")
      val tOpenSessionResp = client.OpenSession(req)

      val tExecuteStatementReq = new TExecuteStatementReq()
      tExecuteStatementReq.setSessionHandle(tOpenSessionResp.getSessionHandle)
      tExecuteStatementReq.setStatement("show session")
      val tExecuteStatementResp = client.ExecuteStatement(tExecuteStatementReq)

      val tCloseOperationReq = new TCloseOperationReq(tExecuteStatementResp.getOperationHandle)
      val tCloseOperationResp = client.CloseOperation(tCloseOperationReq)
      assert(tCloseOperationResp.getStatus.getStatusCode === TStatusCode.SUCCESS_STATUS)

      val tFetchResultsReq = new TFetchResultsReq()
      tFetchResultsReq.setOperationHandle(tExecuteStatementResp.getOperationHandle)
      tFetchResultsReq.setFetchType(0)
      tFetchResultsReq.setMaxRows(1000)
      val tFetchResultsResp = client.FetchResults(tFetchResultsReq)
      assert(tFetchResultsResp.getStatus.getStatusCode === TStatusCode.ERROR_STATUS)
      assert(tFetchResultsResp.getStatus.getErrorMessage startsWith "Invalid OperationHandle" +
        " [type=EXECUTE_STATEMENT, identifier:")

      val tCloseSessionReq = new TCloseSessionReq()
      tCloseSessionReq.setSessionHandle(tOpenSessionResp.getSessionHandle)
      val tCloseSessionResp = client.CloseSession(tCloseSessionReq)
      assert(tCloseSessionResp.getStatus.getStatusCode === TStatusCode.SUCCESS_STATUS)
      val tExecuteStatementResp1 = client.ExecuteStatement(tExecuteStatementReq)

      val status = tExecuteStatementResp1.getStatus
      assert(status.getStatusCode === TStatusCode.ERROR_STATUS)
      assert(status.getErrorMessage startsWith s"Invalid SessionHandle [")
    }
  }

  test("cancel operation") {
    withThriftClient { client =>
      val req = new TOpenSessionReq()
      req.setUsername("hongdd")
      req.setPassword("anonymous")
      val tOpenSessionResp = client.OpenSession(req)

      val tExecuteStatementReq = new TExecuteStatementReq()
      tExecuteStatementReq.setSessionHandle(tOpenSessionResp.getSessionHandle)
      tExecuteStatementReq.setStatement("show session")
      val tExecuteStatementResp = client.ExecuteStatement(tExecuteStatementReq)
      val tCancelOperationReq = new TCancelOperationReq(tExecuteStatementResp.getOperationHandle)
      val tCancelOperationResp = client.CancelOperation(tCancelOperationReq)
      assert(tCancelOperationResp.getStatus.getStatusCode === TStatusCode.SUCCESS_STATUS)
      val tFetchResultsReq = new TFetchResultsReq()
      tFetchResultsReq.setOperationHandle(tExecuteStatementResp.getOperationHandle)
      tFetchResultsReq.setFetchType(0)
      tFetchResultsReq.setMaxRows(1000)
      val tFetchResultsResp = client.FetchResults(tFetchResultsReq)
      assert(tFetchResultsResp.getStatus.getStatusCode === TStatusCode.SUCCESS_STATUS)
    }
  }
}
