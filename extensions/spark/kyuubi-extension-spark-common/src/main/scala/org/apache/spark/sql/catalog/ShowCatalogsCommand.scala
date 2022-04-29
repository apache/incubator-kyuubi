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

package org.apache.spark.sql.catalog

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.catalyst.expressions.{Attribute, AttributeReference}
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.catalyst.util.StringUtils
import org.apache.spark.sql.connector.catalog.CatalogManager
import org.apache.spark.sql.execution.command.RunnableCommand
import org.apache.spark.sql.types.StringType

/**
 * The command for `SHOW CATALOGS`.
 */
case class ShowCatalogsCommand(pattern: Option[String]) extends RunnableCommand {
  override val output: Seq[Attribute] = Seq(
    AttributeReference("catalog", StringType, nullable = false)())

  // The implementation use eager strategy to list catalog, which is different from SPARK-35973
  override def run(sparkSession: SparkSession): Seq[Row] = {
    val configuredCatalogs = sparkSession.sessionState.conf.getAllConfs.keys
      .filter { _ startsWith "spark.sql.catalog." }
      .map { _ stripPrefix "spark.sql.catalog." }
      .filterNot { _ contains "." }
      .toSet
    val allCatalogs = (configuredCatalogs + CatalogManager.SESSION_CATALOG_NAME).toSeq.sorted
    pattern.map(StringUtils.filterPattern(allCatalogs, _)).getOrElse(allCatalogs).map(Row(_))
  }

  protected def withNewChildrenInternal(newChildren: IndexedSeq[LogicalPlan]): LogicalPlan =
    this.asInstanceOf[LogicalPlan]
}
