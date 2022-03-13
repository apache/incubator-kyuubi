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

import java.util

import scala.collection.mutable.ArrayBuffer

import org.apache.flink.table.api.{DataTypes, ResultKind}
import org.apache.flink.table.catalog.Column
import org.apache.flink.table.client.gateway.Executor
import org.apache.flink.table.operations.command.{AddJarOperation, RemoveJarOperation, ResetOperation, SetOperation}
import org.apache.flink.types.Row

import org.apache.kyuubi.engine.flink.result.ResultSet
import org.apache.kyuubi.engine.flink.result.ResultSetUtil.successResultSet

object OperationUtils {

  /**
   * Runs a SetOperation with executor. Returns when SetOperation is executed successfully.
   *
   * @param setOperation Set operation.
   * @param executor A gateway for communicating with Flink and other external systems.
   * @param sessionId Id of the session.
   * @return A ResultSet of SetOperation execution.
   */
  def runSetOperation(
      setOperation: SetOperation,
      executor: Executor,
      sessionId: String): ResultSet = {
    if (setOperation.getKey.isPresent) {
      val key: String = setOperation.getKey.get.trim

      if (setOperation.getValue.isPresent) {
        val newValue: String = setOperation.getValue.get.trim
        executor.setSessionProperty(sessionId, key, newValue)
      }

      val value = executor.getSessionConfigMap(sessionId).getOrDefault(key, "")
      ResultSet.builder
        .resultKind(ResultKind.SUCCESS_WITH_CONTENT)
        .columns(
          Column.physical("key", DataTypes.STRING()),
          Column.physical("value", DataTypes.STRING()))
        .data(Array(Row.of(key, value)))
        .build
    } else {
      // show all properties if set without key
      val properties: util.Map[String, String] = executor.getSessionConfigMap(sessionId)

      val entries = ArrayBuffer.empty[Row]
      properties.forEach((key, value) => entries.append(Row.of(key, value)))

      if (entries.nonEmpty) {
        val prettyEntries = entries.sortBy(_.getField(0).asInstanceOf[String])
        ResultSet.builder
          .resultKind(ResultKind.SUCCESS_WITH_CONTENT)
          .columns(
            Column.physical("key", DataTypes.STRING()),
            Column.physical("value", DataTypes.STRING()))
          .data(prettyEntries.toArray)
          .build
      } else {
        ResultSet.builder
          .resultKind(ResultKind.SUCCESS_WITH_CONTENT)
          .columns(
            Column.physical("key", DataTypes.STRING()),
            Column.physical("value", DataTypes.STRING()))
          .data(Array[Row]())
          .build
      }
    }
  }

  /**
   * Runs a ResetOperation with executor. Returns when ResetOperation is executed successfully.
   *
   * @param resetOperation Reset operation.
   * @param executor A gateway for communicating with Flink and other external systems.
   * @param sessionId Id of the session.
   * @return A ResultSet of ResetOperation execution.
   */
  def runResetOperation(
      resetOperation: ResetOperation,
      executor: Executor,
      sessionId: String): ResultSet = {
    if (resetOperation.getKey.isPresent) {
      // reset the given property
      executor.resetSessionProperty(sessionId, resetOperation.getKey.get())
    } else {
      // reset all properties
      executor.resetSessionProperties(sessionId)
    }
    successResultSet
  }

  /**
   * Runs a AddJarOperation with executor. Returns when AddJarOperation is executed successfully.
   *
   * @param addJarOperation Add-jar operation.
   * @param executor A gateway for communicating with Flink and other external systems.
   * @param sessionId Id of the session.
   * @return A ResultSet of ResetOperation execution.
   */
  def runAddJarOperation(
      addJarOperation: AddJarOperation,
      executor: Executor,
      sessionId: String): ResultSet = {
    executor.addJar(sessionId, addJarOperation.getPath)
    successResultSet
  }

  /**
   * Runs a RemoveJarOperation with executor. Returns when RemoveJarOperation is executed
   * successfully.
   *
   * @param removeJarOperation Add-jar operation.
   * @param executor A gateway for communicating with Flink and other external systems.
   * @param sessionId Id of the session.
   * @return A ResultSet of ResetOperation execution.
   */
  def runRemoveJarOperation(
      removeJarOperation: RemoveJarOperation,
      executor: Executor,
      sessionId: String): ResultSet = {
    executor.removeJar(sessionId, removeJarOperation.getPath)
    successResultSet
  }
}
