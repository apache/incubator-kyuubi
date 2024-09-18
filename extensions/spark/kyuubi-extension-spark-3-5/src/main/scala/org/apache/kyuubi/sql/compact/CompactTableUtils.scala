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

package org.apache.kyuubi.sql.compact

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{Path => HadoopPath}
import org.apache.spark.sql.catalyst.TableIdentifier
import org.apache.spark.sql.catalyst.catalog.CatalogStorageFormat

import org.apache.kyuubi.sql.KyuubiSQLExtensionException

object CompactTableUtils {

  def toJavaList[A](it: List[A]): java.util.List[A] = {
    val javaList = new java.util.ArrayList[A](it.size)
    it.indices foreach { i =>
      javaList.add(it(i))
    }
    javaList
  }

  def getStagingDir(path: String, jobId: String): HadoopPath = {
    new HadoopPath(getCompactStagingDir(path), s".spark-compact-staging-$jobId")
  }

  def getCompactStagingDir(tableLocation: String): HadoopPath = {
    new HadoopPath(tableLocation, ".compact")
  }

  def getCompactDataDir(tableStorage: CatalogStorageFormat): Seq[String] =
    getCompactDataDir(tableStorage, Seq.empty)

  def getCompactDataDir(
      tableStorage: CatalogStorageFormat,
      partitionStorage: Seq[CatalogStorageFormat]): Seq[String] = {
    (partitionStorage.flatMap(_.locationUri), tableStorage.locationUri) match {
      case (partUri, _) if partUri.nonEmpty => partUri.map(_.toString)
      case (partUri, Some(tableUri)) if partUri.isEmpty => Seq(tableUri.toString)
      case _ => Seq.empty
    }
  }

  def getTableIdentifier(tableIdent: Seq[String]): TableIdentifier = tableIdent match {
    case Seq(tbl) => TableIdentifier(tbl)
    case Seq(db, tbl) => TableIdentifier(tbl, Some(db))
    case _ => throw new KyuubiSQLExtensionException(
        "only support session catalog table, please use db.table instead")
  }

  def getCodecExtFromFilePath(filePath: HadoopPath, hadoopConf: Configuration): Option[String] =
    filePath.getName.split("\\.", 3) match {
      case Array(_, codecExt, "parquet") =>
        Some(codecExt)
      case Array(_, codecExt, "orc") =>
        Some(codecExt)
      case Array(_, "parquet") =>
        None
      case Array(_, "orc") =>
        None
      case Array(_, _, codecExt) =>
        Some(codecExt)
      case Array(_, _) =>
        None
      case _ => None
    }

  def getExtFromFilePath(filePath: String): String =
    filePath.split("\\.", 3).tail.mkString(".")
}
