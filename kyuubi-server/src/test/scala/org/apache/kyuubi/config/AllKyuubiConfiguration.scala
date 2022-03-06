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

package org.apache.kyuubi.config

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

import org.apache.kyuubi.{KyuubiFunSuite, TestUtils}
import org.apache.kyuubi.ha.HighAvailabilityConf
import org.apache.kyuubi.metrics.MetricsConf
import org.apache.kyuubi.zookeeper.ZookeeperConf

// scalastyle:off line.size.limit
/**
 * End-to-end test cases for configuration doc file
 * The golden result file is "docs/deployment/settings.md".
 *
 * To run the entire test suite:
 * {{{
 *   build/mvn clean install -Pflink-provided,spark-provided -DwildcardSuites=org.apache.kyuubi.config.AllKyuubiConfiguration
 * }}}
 *
 * To re-generate golden files for entire suite, run:
 * {{{
 *   KYUUBI_UPDATE=1 build/mvn clean install -Pflink-provided,spark-provided -DwildcardSuites=org.apache.kyuubi.config.AllKyuubiConfiguration
 * }}}
 */
// scalastyle:on line.size.limit
class AllKyuubiConfiguration extends KyuubiFunSuite {
  private val kyuubiHome: String =
    getClass.getProtectionDomain.getCodeSource.getLocation.getPath.split("kyuubi-server")(0)
  private val markdown = Paths.get(kyuubiHome, "docs", "deployment", "settings.md")
    .toAbsolutePath

  def rewriteToConf(path: Path, buffer: ArrayBuffer[String]): Unit = {
    val env =
      Files.newBufferedReader(path, StandardCharsets.UTF_8)

    try {
      buffer += "```bash"
      var line = env.readLine()
      while (line != null) {
        buffer += line
        line = env.readLine()
      }
      buffer += "```"
    } finally {
      env.close()
    }
  }

  test("Check all kyuubi configs") {
    KyuubiConf
    HighAvailabilityConf
    MetricsConf
    ZookeeperConf

    val newOutput = new ArrayBuffer[String]()
    newOutput += "<!--"
    newOutput += " - Licensed to the Apache Software Foundation (ASF) under one or more"
    newOutput += " - contributor license agreements.  See the NOTICE file distributed with"
    newOutput += " - this work for additional information regarding copyright ownership."
    newOutput += " - The ASF licenses this file to You under the Apache License, Version 2.0"
    newOutput += " - (the \"License\"); you may not use this file except in compliance with"
    newOutput += " - the License.  You may obtain a copy of the License at"
    newOutput += " -"
    newOutput += " -   http://www.apache.org/licenses/LICENSE-2.0"
    newOutput += " -"
    newOutput += " - Unless required by applicable law or agreed to in writing, software"
    newOutput += " - distributed under the License is distributed on an \"AS IS\" BASIS,"
    newOutput += " - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."
    newOutput += " - See the License for the specific language governing permissions and"
    newOutput += " - limitations under the License."
    newOutput += " -->"
    newOutput += ""
    newOutput += "<!-- DO NOT MODIFY THIS FILE DIRECTLY, IT IS AUTO GENERATED BY" +
      " [org.apache.kyuubi.config.AllKyuubiConfiguration] -->"
    newOutput += ""
    newOutput += "<div align=center>"
    newOutput += ""
    newOutput += "![](../imgs/kyuubi_logo.png)"
    newOutput += ""
    newOutput += "</div>"
    newOutput += ""
    newOutput += "# Introduction to the Kyuubi Configurations System"
    newOutput += ""
    newOutput += "Kyuubi provides several ways to configure the system and corresponding engines."
    newOutput += ""
    newOutput += ""
    newOutput += "## Environments"
    newOutput += ""
    newOutput += ""
    newOutput += "You can configure the environment variables in" +
      " `$KYUUBI_HOME/conf/kyuubi-env.sh`, e.g, `JAVA_HOME`, then this java runtime will be used" +
      " both for Kyuubi server instance and the applications it launches. You can also change" +
      " the variable in the subprocess's env configuration file, e.g." +
      "`$SPARK_HOME/conf/spark-env.sh` to use more specific ENV for SQL engine applications."

    rewriteToConf(Paths.get(kyuubiHome, "conf", "kyuubi-env.sh.template"), newOutput)

    newOutput += ""
    newOutput += "For the environment variables that only needed to be transferred into engine" +
      " side, you can set it with a Kyuubi configuration item formatted" +
      " `kyuubi.engineEnv.VAR_NAME`. For example, with `kyuubi.engineEnv.SPARK_DRIVER_MEMORY=4g`," +
      " the environment variable `SPARK_DRIVER_MEMORY` with value `4g` would be transferred into" +
      " engine side. With `kyuubi.engineEnv.SPARK_CONF_DIR=/apache/confs/spark/conf`, the" +
      " value of `SPARK_CONF_DIR` in engine side is set to `/apache/confs/spark/conf`."

    newOutput += ""
    newOutput += "## Kyuubi Configurations"
    newOutput += ""

    newOutput += "You can configure the Kyuubi properties in" +
      " `$KYUUBI_HOME/conf/kyuubi-defaults.conf`. For example:"

    rewriteToConf(Paths.get(kyuubiHome, "conf", "kyuubi-defaults.conf.template"), newOutput)

    KyuubiConf.kyuubiConfEntries.values().asScala
      .toSeq
      .filterNot(_.internal)
      .groupBy(_.key.split("\\.")(1))
      .toSeq.sortBy(_._1).foreach { case (category, entries) =>
        newOutput += ""
        newOutput += s"### ${category.capitalize}"
        newOutput += ""

        newOutput += "Key | Default | Meaning | Type | Since"
        newOutput += "--- | --- | --- | --- | ---"

        entries.sortBy(_.key).foreach { c =>
          val dft = c.defaultValStr.replace("<", "&lt;").replace(">", "&gt;")
          val seq = Seq(
            s"<code>${c.key}</code>",
            s"<div style='width: 65pt;word-wrap: break-word;white-space: normal'>$dft</div>",
            s"<div style='width: 170pt;word-wrap: break-word;white-space: normal'>${c.doc}</div>",
            s"<div style='width: 30pt'>${c.typ}</div>",
            s"<div style='width: 20pt'>${c.version}</div>")
          newOutput += seq.mkString("|")
        }
        newOutput += ""
      }

    newOutput += ("## Spark Configurations")
    newOutput += ""

    newOutput += ("### Via spark-defaults.conf")
    newOutput += ""

    newOutput += ("Setting them in `$SPARK_HOME/conf/spark-defaults.conf`" +
      " supplies with default values for SQL engine application. Available properties can be" +
      " found at Spark official online documentation for" +
      " [Spark Configurations](http://spark.apache.org/docs/latest/configuration.html)")

    newOutput += ""
    newOutput += ("### Via kyuubi-defaults.conf")
    newOutput += ""
    newOutput += ("Setting them in `$KYUUBI_HOME/conf/kyuubi-defaults.conf`" +
      " supplies with default values for SQL engine application too. These properties will" +
      " override all settings in `$SPARK_HOME/conf/spark-defaults.conf`")

    newOutput += ""
    newOutput += ("### Via JDBC Connection URL")
    newOutput += ""
    newOutput += ("Setting them in the JDBC Connection URL" +
      " supplies session-specific for each SQL engine. For example: " +
      "```" +
      "jdbc:hive2://localhost:10009/default;#" +
      "spark.sql.shuffle.partitions=2;spark.executor.memory=5g" +
      "```")
    newOutput += ""
    newOutput += ("- **Runtime SQL Configuration**")
    newOutput += ""
    newOutput += ("  - For [Runtime SQL Configurations](" +
      "http://spark.apache.org/docs/latest/configuration.html#runtime-sql-configuration), they" +
      " will take affect every time")
    newOutput += ""
    newOutput += ("- **Static SQL and Spark Core Configuration**")
    newOutput += ""
    newOutput += ("  - For [Static SQL Configurations](" +
      "http://spark.apache.org/docs/latest/configuration.html#static-sql-configuration) and" +
      " other spark core configs, e.g. `spark.executor.memory`, they will take affect if there" +
      " is no existing SQL engine application. Otherwise, they will just be ignored")
    newOutput += ""
    newOutput += ("### Via SET Syntax")
    newOutput += ""
    newOutput += ("Please refer to the Spark official online documentation for" +
      " [SET Command](http://spark.apache.org/docs/latest/sql-ref-syntax-aux-conf-mgmt-set.html)")

    newOutput += ""
    newOutput += ("## Logging")
    newOutput += ""
    newOutput += ("Kyuubi uses [log4j](https://logging.apache.org/log4j/2.x/) for logging." +
      " You can configure it using `$KYUUBI_HOME/conf/log4j2.properties`.")

    rewriteToConf(Paths.get(kyuubiHome, "conf", "log4j2.properties.template"), newOutput)

    newOutput += ""
    newOutput += ("## Other Configurations")
    newOutput += ""
    newOutput += ("### Hadoop Configurations")
    newOutput += ""
    newOutput += ("Specifying `HADOOP_CONF_DIR` to the directory contains hadoop configuration" +
      " files or treating them as Spark properties with a `spark.hadoop.` prefix." +
      " Please refer to the Spark official online documentation for" +
      " [Inheriting Hadoop Cluster Configuration](http://spark.apache.org/docs/latest/" +
      "configuration.html#inheriting-hadoop-cluster-configuration)." +
      " Also, please refer to the [Apache Hadoop](http://hadoop.apache.org)'s" +
      " online documentation for an overview on how to configure Hadoop.")
    newOutput += ""
    newOutput += ("### Hive Configurations")
    newOutput += ""
    newOutput += ("These configurations are used for SQL engine application to talk to" +
      " Hive MetaStore and could be configured in a `hive-site.xml`." +
      " Placed it in `$SPARK_HOME/conf` directory, or treating them as Spark properties with" +
      " a `spark.hadoop.` prefix.")

    newOutput += ""
    newOutput += ("## User Defaults")
    newOutput += ""
    newOutput += ("In Kyuubi, we can configure user default settings to meet separate needs." +
      " These user defaults override system defaults, but will be overridden by those from" +
      " [JDBC Connection URL](#via-jdbc-connection-url) or [Set Command](#via-set-syntax)" +
      " if could be. They will take effect when creating the SQL engine application ONLY.")
    newOutput += ("User default settings are in the form of `___{username}___.{config key}`." +
      " There are three continuous underscores(`_`) at both sides of the `username` and" +
      " a dot(`.`) that separates the config key and the prefix. For example:")
    newOutput += ("```bash")
    newOutput += ("# For system defaults")
    newOutput += ("spark.master=local")
    newOutput += ("spark.sql.adaptive.enabled=true")
    newOutput += ("# For a user named kent")
    newOutput += ("___kent___.spark.master=yarn")
    newOutput += ("___kent___.spark.sql.adaptive.enabled=false")
    newOutput += ("# For a user named bob")
    newOutput += ("___bob___.spark.master=spark://master:7077")
    newOutput += ("___bob___.spark.executor.memory=8g")
    newOutput += ("```")
    newOutput += ""
    newOutput += "In the above case, if there are related configurations from" +
      " [JDBC Connection URL](#via-jdbc-connection-url), `kent` will run his SQL engine" +
      " application on YARN and prefer the Spark AQE to be off, while `bob` will activate" +
      " his SQL engine application on a Spark standalone cluster with 8g heap memory for each" +
      " executor and obey the Spark AQE behavior of Kyuubi system default. On the other hand," +
      " for those users who do not have custom configurations will use system defaults."

    TestUtils.verifyOutput(markdown, newOutput, getClass.getCanonicalName)
  }
}
