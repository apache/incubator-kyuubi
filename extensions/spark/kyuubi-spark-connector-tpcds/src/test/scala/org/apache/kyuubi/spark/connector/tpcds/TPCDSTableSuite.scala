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

import scala.collection.JavaConverters._

import io.trino.tpcds.Table
import io.trino.tpcds.generator.CallCenterGeneratorColumn
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import org.apache.kyuubi.KyuubiFunSuite
import org.apache.kyuubi.spark.connector.tpcds.LocalSparkSession.withSparkSession

class TPCDSTableSuite extends KyuubiFunSuite {

  test("useAnsiStringType (true, false)") {
    Seq(true, false).foreach(key => {
      val sparkConf = new SparkConf().setMaster("local[*]")
        .set("spark.ui.enabled", "false")
        .set("spark.sql.catalogImplementation", "in-memory")
        .set("spark.sql.catalog.tpcds", classOf[TPCDSCatalog].getName)
        .set("spark.sql.catalog.tpcds.useAnsiStringType", key.toString)
      withSparkSession(SparkSession.builder.config(sparkConf).getOrCreate()) { spark =>
        val rows = spark.sql("desc tpcds.sf1.call_center").collect()
        rows.foreach(row => {
          val dataType = row.getString(1)
          row.getString(0) match {
            case "cc_call_center_id" =>
              if (key) {
                assert(dataType == "char(16)")
              } else {
                assert(dataType == "string")
              }
            case "cc_name" =>
              if (key) {
                assert(dataType == "varchar(50)")
              } else {
                assert(dataType == "string")
              }
            case _ =>
          }
        })
      }
    })
  }

  test("test nullable column") {
    Table.getBaseTables.asScala
      .filterNot(_.getName == "dbgen_version").foreach { tpcdsTable =>
        val tableName = tpcdsTable.getName
        val sparkConf = new SparkConf().setMaster("local[*]")
          .set("spark.ui.enabled", "false")
          .set("spark.sql.catalogImplementation", "in-memory")
          .set("spark.sql.catalog.tpcds", classOf[TPCDSCatalog].getName)
        withSparkSession(SparkSession.builder.config(sparkConf).getOrCreate()) { spark =>
          val sparkTable = spark.table(s"tpcds.sf1.$tableName")
          var notNullBitMap = 0
          sparkTable.schema.fields.zipWithIndex.foreach { case (field, i) =>
            val index = TPCDSTableUtils.reviseColumnIndex(tpcdsTable, i)
            if (!field.nullable) {
              notNullBitMap |= 1 << index
            }
          }
          assert(tpcdsTable.getNotNullBitMap == notNullBitMap)
        }
      }
  }

  test("test reviseColumnIndex") {
    // io.trino.tpcds.row.CallCenterRow.getValues
    val getValuesColumns = Array(
      "CC_CALL_CENTER_SK",
      "CC_CALL_CENTER_ID",
      "CC_REC_START_DATE_ID",
      "CC_REC_END_DATE_ID",
      "CC_CLOSED_DATE_ID",
      "CC_OPEN_DATE_ID",
      "CC_NAME",
      "CC_CLASS",
      "CC_EMPLOYEES",
      "CC_SQ_FT",
      "CC_HOURS",
      "CC_MANAGER",
      "CC_MARKET_ID",
      "CC_MARKET_CLASS",
      "CC_MARKET_DESC",
      "CC_MARKET_MANAGER",
      "CC_DIVISION",
      "CC_DIVISION_NAME",
      "CC_COMPANY",
      "CC_COMPANY_NAME",
      "CC_STREET_NUMBER",
      "CC_STREET_NAME",
      "CC_STREET_TYPE",
      "CC_SUITE_NUMBER",
      "CC_CITY",
      "CC_ADDRESS",
      "CC_STATE",
      "CC_ZIP",
      "CC_COUNTRY",
      "CC_GMT_OFFSET",
      "CC_TAX_PERCENTAGE")
    Table.CALL_CENTER.getColumns.zipWithIndex.map {
      case (_, i) =>
        assert(TPCDSTableUtils.reviseColumnIndex(Table.CALL_CENTER, i) ==
          CallCenterGeneratorColumn.valueOf(getValuesColumns(i)).getGlobalColumnNumber -
          CallCenterGeneratorColumn.CC_CALL_CENTER_SK.getGlobalColumnNumber)
    }
  }
}
