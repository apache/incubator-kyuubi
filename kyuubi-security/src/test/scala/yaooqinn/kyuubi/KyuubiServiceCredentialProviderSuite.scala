/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yaooqinn.kyuubi

import java.net.InetAddress

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.{Credentials, UserGroupInformation}
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.spark.{SparkConf, SparkFunSuite}
import org.scalatest.mock.MockitoSugar

class KyuubiServiceCredentialProviderSuite extends SparkFunSuite with MockitoSugar {
  test("obtain credentials") {
    val sparkConf = new SparkConf()
    val userName = UserGroupInformation.getCurrentUser.getShortUserName
    val hadoopConf = new Configuration()
      hadoopConf.set(YarnConfiguration.RM_PRINCIPAL,
      userName + "/" + InetAddress.getLocalHost.getHostName + "@" + "KYUUBI.ORG")
    val provider = new KyuubiServiceCredentialProvider
    assert(!provider.credentialsRequired(hadoopConf))
    hadoopConf.set("hadoop.security.authentication", "KERBEROS")
    UserGroupInformation.setConfiguration(hadoopConf)
    assert(provider.credentialsRequired(hadoopConf))
    assert(provider.serviceName === "kyuubi")
    val credential = new Credentials()
    val now = System.currentTimeMillis()
    val renewalTime = provider.obtainCredentials(hadoopConf, sparkConf, credential)
    assert(renewalTime.isDefined)
    assert(renewalTime.get - now >= 2L * 60 * 60 *100 )

  }
}
