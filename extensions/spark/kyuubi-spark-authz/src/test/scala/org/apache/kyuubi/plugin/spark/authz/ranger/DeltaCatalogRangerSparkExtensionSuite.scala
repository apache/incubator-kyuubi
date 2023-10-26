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
package org.apache.kyuubi.plugin.spark.authz.ranger

import org.scalatest.Outcome

import org.apache.kyuubi.Utils
import org.apache.kyuubi.plugin.spark.authz.AccessControlException
import org.apache.kyuubi.plugin.spark.authz.RangerTestNamespace._
import org.apache.kyuubi.plugin.spark.authz.RangerTestUsers._
import org.apache.kyuubi.tags.DeltaTest
import org.apache.kyuubi.util.AssertionUtils._

/**
 * Tests for RangerSparkExtensionSuite on Delta Lake
 */
@DeltaTest
class DeltaCatalogRangerSparkExtensionSuite extends RangerSparkExtensionSuite {
  override protected val catalogImpl: String = "hive"

  val namespace1 = deltaNamespace
  val table1 = "table1_delta"

  override def withFixture(test: NoArgTest): Outcome = {
    test()
  }

  override def beforeAll(): Unit = {
    spark.conf.set(
      s"spark.sql.catalog.$sparkCatalog",
      "org.apache.spark.sql.delta.catalog.DeltaCatalog")
    spark.conf.set(
      s"spark.sql.catalog.$sparkCatalog.warehouse",
      Utils.createTempDir("delta-hadoop").toString)
    super.beforeAll()
    doAs(admin, sql(s"CREATE DATABASE IF NOT EXISTS $namespace1"))
  }

  override def afterAll(): Unit = {
    doAs(admin, sql(s"DROP DATABASE IF EXISTS $namespace1"))
    super.afterAll()
    spark.sessionState.catalog.reset()
    spark.sessionState.conf.clear()
  }

  test("CreateDeltaTableCommand") {
    withCleanTmpResources(Seq((s"$namespace1.$table1", "table"))) {
      val createTableSql =
        s"""
           |CREATE TABLE IF NOT EXISTS $namespace1.$table1 (
           |  id INT,
           |  firstName STRING,
           |  middleName STRING,
           |  lastName STRING,
           |  gender STRING,
           |  birthDate TIMESTAMP,
           |  ssn STRING,
           |  salary INT
           |) USING DELTA
           |""".stripMargin
      interceptContains[AccessControlException] {
        doAs(someone, sql(createTableSql))
      }(s"does not have [create] privilege on [$namespace1/$table1]")
      doAs(admin, createTableSql)
    }

    withCleanTmpResources(Seq((s"$namespace1.$table1", "table"))) {
      val createOrReplaceTableSql =
        s"""
           |CREATE OR REPLACE TABLE $namespace1.$table1 (
           |  id INT,
           |  firstName STRING,
           |  middleName STRING,
           |  lastName STRING,
           |  gender STRING,
           |  birthDate TIMESTAMP,
           |  ssn STRING,
           |  salary INT
           |) USING DELTA
           |""".stripMargin
      interceptContains[AccessControlException] {
        doAs(someone, sql(createOrReplaceTableSql))
      }(s"does not have [create] privilege on [$namespace1/$table1]")
      doAs(admin, createOrReplaceTableSql)
    }

    withCleanTmpResources(Seq((s"$namespace1.$table1", "table"))) {
      val createPartitionTableSql =
        s"""
           |CREATE TABLE IF NOT EXISTS $namespace1.$table1 (
           |  id INT,
           |  firstName STRING,
           |  middleName STRING,
           |  lastName STRING,
           |  gender STRING,
           |  birthDate TIMESTAMP,
           |  ssn STRING,
           |  salary INT
           |)
           |USING DELTA
           |PARTITIONED BY (gender)
           |""".stripMargin
      interceptContains[AccessControlException] {
        doAs(someone, sql(createPartitionTableSql))
      }(s"does not have [create] privilege on [$namespace1/$table1]")
      doAs(admin, createPartitionTableSql)
    }
  }
}
