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

package org.apache.kyuubi.server.statestore

import org.apache.kyuubi.{KyuubiException, Logging}
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.util.ClassUtils

object StateStoreProvider extends Logging {
  def createStateStore(conf: KyuubiConf): StateStore = {
    val classLoader = Thread.currentThread.getContextClassLoader
    val className = conf.get(KyuubiConf.SERVER_STATE_STORE_CLASS)
    if (className.isEmpty) {
      throw new KyuubiException(
        s"${KyuubiConf.SERVER_STATE_STORE_CLASS.key} cannot be empty.")
    }
    val cls = Class.forName(className, true, classLoader)
    cls match {
      case c if classOf[StateStore].isAssignableFrom(cls) =>
        ClassUtils.createInstance[StateStore](c, conf)
      case _ => throw new KyuubiException(
          s"$className must extend of ${classOf[StateStore].getName}")
    }
  }
}
