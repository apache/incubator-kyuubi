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
package org.apache.kyuubi.spark.connector.tpcds

import io.trino.tpcds.Table
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.TableIdentifier

import org.apache.kyuubi.{KyuubiFunSuite, Utils}
import org.apache.kyuubi.spark.connector.common.LocalSparkSession.withSparkSession

class TPCDSGenerateContextSuite extends KyuubiFunSuite {

  test("test TPCDSGenerateContext") {
    val basePath = Utils.createTempDir() + "/" + getClass.getCanonicalName
    val warehousePath = basePath + "/warehouse"
    val sparkConf = new SparkConf().setMaster("local[*]")
      .set("spark.ui.enabled", "false")
      .set("spark.sql.catalogImplementation", "in-memory")
      .set("spark.sql.warehouse.dir", warehousePath)
      .set("spark.sql.catalog.tpcds", classOf[TPCDSCatalog].getName)
    withSparkSession(SparkSession.builder.config(sparkConf).getOrCreate()) { spark =>
      try {
        val generateContext = TPCDSGenerateContext("tpcds.tiny")
        generateContext.persistTable(TPCDSPersistOpts("default"), "catalog_sales")
        val table = spark.sessionState.catalog
          .getTableMetadata(TableIdentifier("catalog_sales"))
        assert(table.partitionColumnNames === Seq("cs_sold_date_sk"))
        val count = spark.sql("select * from catalog_sales").count()
        assert(count == TPCDSStatisticsUtils.numRows(
          Table.getTable("catalog_sales"),
          TPCDSSchemaUtils.TINY_SCALE.toDouble))
      } finally {
        val cleanTables = Seq("catalog_sales")
        cleanTables.foreach(t => spark.sql(s"drop table if exists $t"))
      }
    }
  }

}
