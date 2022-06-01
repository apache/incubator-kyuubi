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

package org.apache.kyuubi.operation

import java.io.IOException
import java.nio.ByteBuffer
import java.util.{ArrayList => JArrayList, Locale}
import java.util.concurrent.TimeUnit

import scala.collection.JavaConverters._

import com.codahale.metrics.MetricRegistry
import org.apache.hive.service.rpc.thrift._

import org.apache.kyuubi.{KyuubiException, KyuubiSQLException}
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.engine.{ApplicationOperation, KillResponse, ProcBuilder}
import org.apache.kyuubi.engine.spark.SparkBatchProcessBuilder
import org.apache.kyuubi.metrics.MetricsConstants.OPERATION_OPEN
import org.apache.kyuubi.metrics.MetricsSystem
import org.apache.kyuubi.operation.FetchOrientation.FetchOrientation
import org.apache.kyuubi.operation.OperationState.OperationState
import org.apache.kyuubi.operation.log.OperationLog
import org.apache.kyuubi.session.KyuubiBatchSessionImpl
import org.apache.kyuubi.util.ThriftUtils

class BatchJobSubmission(
    session: KyuubiBatchSessionImpl,
    val batchType: String,
    val batchName: String,
    resource: String,
    className: String,
    batchConf: Map[String, String],
    batchArgs: Seq[String])
  extends KyuubiOperation(OperationType.UNKNOWN_OPERATION, session) {

  override def statement: String = "BATCH_JOB_SUBMISSION"

  override def shouldRunAsync: Boolean = true

  private val _operationLog = OperationLog.createOperationLog(session, getHandle)

  private val applicationManager = session.sessionManager.applicationManager

  private[kyuubi] val batchId: String = session.handle.identifier.toString

  private var applicationStatus: Option[Map[String, String]] = None

  private var killMessage: KillResponse = (false, "UNKNOWN")
  def getKillMessage: KillResponse = killMessage

  private val builder: ProcBuilder = {
    Option(batchType).map(_.toUpperCase(Locale.ROOT)) match {
      case Some("SPARK") =>
        new SparkBatchProcessBuilder(
          session.user,
          session.sessionConf,
          batchId,
          batchName,
          Option(resource),
          className,
          batchConf,
          batchArgs,
          getOperationLog)

      case _ =>
        throw new UnsupportedOperationException(s"Batch type $batchType unsupported")
    }
  }

  private[kyuubi] def currentApplicationState: Option[Map[String, String]] = {
    applicationManager.getApplicationInfo(builder.clusterManager(), batchId)
  }

  private[kyuubi] def killBatchApplication(): KillResponse = {
    applicationManager.killApplication(builder.clusterManager(), batchId)
  }

  private val applicationCheckInterval =
    session.sessionConf.get(KyuubiConf.BATCH_APPLICATION_CHECK_INTERVAL)

  private def updateBatchMetadata(): Unit = {
    val endTime =
      if (isTerminalState(getStatus.state)) {
        getStatus.lastModified
      } else {
        0L
      }
    session.sessionManager.updateBatchMetadata(
      batchId,
      getStatus.state,
      applicationStatus.getOrElse(Map.empty),
      endTime)
  }

  override def getOperationLog: Option[OperationLog] = Option(_operationLog)

  // we can not set to other state if it is canceled
  override def setState(newState: OperationState): Unit = state.synchronized {
    super.setState(newState)
  }

  override protected def beforeRun(): Unit = {
    OperationLog.setCurrentOperationLog(_operationLog)
    setHasResultSet(true)
    setState(OperationState.PENDING)
  }

  override protected def afterRun(): Unit = {
    OperationLog.removeCurrentOperationLog()
  }

  override protected def runInternal(): Unit = {
    val asyncOperation: Runnable = () => {
      setState(OperationState.RUNNING)
      try {
        submitBatchJob()
        setState(OperationState.FINISHED)
      } catch {
        onError()
      } finally {
        updateBatchMetadata()
      }
    }
    try {
      val opHandle = session.sessionManager.submitBackgroundOperation(asyncOperation)
      setBackgroundHandle(opHandle)
    } catch onError("submitting batch job submission operation in background, request rejected")
  }

  private def applicationFailed(applicationStatus: Option[Map[String, String]]): Boolean = {
    applicationStatus.map(_.get(ApplicationOperation.APP_STATE_KEY)).exists(s =>
      s.contains("KILLED") || s.contains("FAILED"))
  }

  private def submitBatchJob(): Unit = {
    var appStatusFirstUpdated = false
    try {
      info(s"Submitting $batchType batch job: $builder")
      val process = builder.start
      applicationStatus = currentApplicationState
      while (!applicationFailed(applicationStatus) && process.isAlive) {
        if (!appStatusFirstUpdated && applicationStatus.isDefined) {
          updateBatchMetadata()
          appStatusFirstUpdated = true
        }
        process.waitFor(applicationCheckInterval, TimeUnit.MILLISECONDS)
        applicationStatus = currentApplicationState
      }

      if (applicationFailed(applicationStatus)) {
        process.destroyForcibly()
        throw new RuntimeException("Batch job failed:" + applicationStatus.get.mkString(","))
      } else {
        process.waitFor()
        if (process.exitValue() != 0) {
          throw new KyuubiException(s"Process exit with value ${process.exitValue()}")
        }
      }
    } finally {
      builder.close()
    }
  }

  def getOperationLogRowSet(
      order: FetchOrientation,
      from: Int,
      size: Int): TRowSet = {
    val operationLog = getOperationLog
    operationLog.map(_.read(from, size)).getOrElse {
      throw KyuubiSQLException(s"Batch ID: $batchId, failed to generate operation log")
    }
  }

  override val getResultSetSchema: TTableSchema = {
    val schema = new TTableSchema()
    Seq("key", "value").zipWithIndex.foreach { case (colName, position) =>
      val tColumnDesc = new TColumnDesc()
      tColumnDesc.setColumnName(colName)
      val tTypeDesc = new TTypeDesc()
      tTypeDesc.addToTypes(TTypeEntry.primitiveEntry(new TPrimitiveTypeEntry(TTypeId.STRING_TYPE)))
      tColumnDesc.setTypeDesc(tTypeDesc)
      tColumnDesc.setPosition(position)
      schema.addToColumns(tColumnDesc)
    }
    schema
  }

  override def getNextRowSet(order: FetchOrientation, rowSetSize: Int): TRowSet = {
    currentApplicationState.map { state =>
      val tRow = new TRowSet(0, new JArrayList[TRow](state.size))
      Seq(state.keys, state.values).map(_.toSeq.asJava).foreach { col =>
        val tCol = TColumn.stringVal(new TStringColumn(col, ByteBuffer.allocate(0)))
        tRow.addToColumns(tCol)
      }
      tRow
    }.getOrElse(ThriftUtils.EMPTY_ROW_SET)
  }

  override def close(): Unit = state.synchronized {
    if (!isClosedOrCanceled) {
      try {
        // For launch engine operation, we use OperationLog to pass engine submit log but
        // at that time we do not have remoteOpHandle
        getOperationLog.foreach(_.close())
      } catch {
        case e: IOException => error(e.getMessage, e)
      }

      try {
        killMessage = killBatchApplication()
        builder.close()
      } finally {
        if (killMessage._1 && !isTerminalState(state)) {
          // the batch operation state should never be closed
          setState(OperationState.CANCELED)
          updateBatchMetadata()
        } else {
          warn(s"Failed to kill application for batch: ${batchId}")
        }
      }
      MetricsSystem.tracing(_.decCount(
        MetricRegistry.name(OPERATION_OPEN, statement.toLowerCase(Locale.getDefault))))
    }
  }

  override def cancel(): Unit = {
    throw new IllegalStateException("Use close instead.")
  }
}
