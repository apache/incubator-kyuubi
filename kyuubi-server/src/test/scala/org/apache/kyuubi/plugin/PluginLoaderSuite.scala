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

package org.apache.kyuubi.plugin

import org.apache.kyuubi.{KyuubiException, KyuubiFunSuite}
import org.apache.kyuubi.config.KyuubiConf

class PluginLoaderSuite extends KyuubiFunSuite {

  test("test engine conf advisor wrong class") {
    val conf = new KyuubiConf(false)
    assert(PluginLoader.loadSessionConfAdvisor(conf).isInstanceOf[DefaultSessionConfAdvisor])

    conf.set(KyuubiConf.SESSION_CONF_ADVISOR, classOf[FakeAdvisor].getName)
    val msg1 = intercept[KyuubiException] {
      PluginLoader.loadSessionConfAdvisor(conf)
    }.getMessage
    assert(msg1.contains(s"is not a child of '${classOf[SessionConfAdvisor].getName}'"))

    conf.set(KyuubiConf.SESSION_CONF_ADVISOR, "non.exists")
    val msg2 = intercept[IllegalArgumentException] {
      PluginLoader.loadSessionConfAdvisor(conf)
    }.getMessage
    assert(msg2.startsWith("Error while instantiating 'non.exists'"))
  }
}

class FakeAdvisor {}
