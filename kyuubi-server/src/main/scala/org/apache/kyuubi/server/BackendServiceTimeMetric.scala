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

package org.apache.kyuubi.server

import org.apache.hive.service.rpc.thrift._

import org.apache.kyuubi.metrics.{MetricsConstants, MetricsSystem}
import org.apache.kyuubi.operation.{OperationHandle, OperationStatus}
import org.apache.kyuubi.operation.FetchOrientation.FetchOrientation
import org.apache.kyuubi.service.BackendService
import org.apache.kyuubi.session.SessionHandle

trait BackendServiceTimeMetric extends BackendService {

  abstract override def openSession(
      protocol: TProtocolVersion,
      user: String,
      password: String,
      ipAddr: String,
      configs: Map[String, String]): SessionHandle = {
    MetricsSystem.timerTracing(MetricsConstants.BS_OPEN_SESSION) {
      super.openSession(protocol, user, password, ipAddr, configs)
    }
  }

  abstract override def closeSession(sessionHandle: SessionHandle): Unit = {
    MetricsSystem.timerTracing(MetricsConstants.BS_CLOSE_SESSION) {
      super.closeSession(sessionHandle)
    }
  }

  abstract override def getInfo(
      sessionHandle: SessionHandle,
      infoType: TGetInfoType): TGetInfoValue = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_INFO) {
      super.getInfo(sessionHandle, infoType)
    }
  }

  abstract override def executeStatement(
      sessionHandle: SessionHandle,
      statement: String,
      confOverlay: Map[String, String],
      runAsync: Boolean,
      queryTimeout: Long): OperationHandle = {
    MetricsSystem.timerTracing(MetricsConstants.BS_EXECUTE_STATEMENT) {
      super.executeStatement(sessionHandle, statement, confOverlay, runAsync, queryTimeout)
    }
  }

  abstract override def getTypeInfo(sessionHandle: SessionHandle): OperationHandle = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_TYPE_INFO) {
      super.getTypeInfo(sessionHandle)
    }
  }

  abstract override def getCatalogs(sessionHandle: SessionHandle): OperationHandle = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_CATALOGS) {
      super.getCatalogs(sessionHandle)
    }
  }

  abstract override def getSchemas(
      sessionHandle: SessionHandle,
      catalogName: String,
      schemaName: String): OperationHandle = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_SCHEMAS) {
      super.getSchemas(sessionHandle, catalogName, schemaName)
    }
  }

  abstract override def getTables(
      sessionHandle: SessionHandle,
      catalogName: String,
      schemaName: String,
      tableName: String,
      tableTypes: java.util.List[String]): OperationHandle = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_TABLES) {
      super.getTables(sessionHandle, catalogName, schemaName, tableName, tableTypes)
    }
  }

  abstract override def getTableTypes(sessionHandle: SessionHandle): OperationHandle = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_TABLE_TYPES) {
      super.getTableTypes(sessionHandle)
    }
  }

  abstract override def getColumns(
      sessionHandle: SessionHandle,
      catalogName: String,
      schemaName: String,
      tableName: String,
      columnName: String): OperationHandle = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_COLUMNS) {
      super.getColumns(sessionHandle, catalogName, schemaName, tableName, columnName)
    }
  }

  abstract override def getFunctions(
      sessionHandle: SessionHandle,
      catalogName: String,
      schemaName: String,
      functionName: String): OperationHandle = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_FUNCTIONS) {
      super.getFunctions(sessionHandle, catalogName, schemaName, functionName)
    }
  }

  abstract override def getOperationStatus(operationHandle: OperationHandle): OperationStatus = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_OPERATION_STATUS) {
      super.getOperationStatus(operationHandle)
    }
  }

  abstract override def cancelOperation(operationHandle: OperationHandle): Unit = {
    MetricsSystem.timerTracing(MetricsConstants.BS_CANCEL_OPERATION) {
      super.cancelOperation(operationHandle)
    }
  }

  abstract override def closeOperation(operationHandle: OperationHandle): Unit = {
    MetricsSystem.timerTracing(MetricsConstants.BS_CLOSE_OPERATION) {
      super.closeOperation(operationHandle)
    }
  }

  abstract override def getResultSetMetadata(operationHandle: OperationHandle): TTableSchema = {
    MetricsSystem.timerTracing(MetricsConstants.BS_GET_RESULT_SET_METADATA) {
      super.getResultSetMetadata(operationHandle)
    }
  }

  abstract override def fetchResults(
      operationHandle: OperationHandle,
      orientation: FetchOrientation,
      maxRows: Int,
      fetchLog: Boolean): TRowSet = {
    MetricsSystem.timerTracing(MetricsConstants.BS_FETCH_RESULTS) {
      super.fetchResults(operationHandle, orientation, maxRows, fetchLog)
    }
  }

}
