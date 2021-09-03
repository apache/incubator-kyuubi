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

package org.apache.kyuubi.util

import org.apache.commons.codec.binary.Base64
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{DataInputBuffer, DataOutputBuffer}
import org.apache.hadoop.security.{Credentials, SecurityUtil}

import org.apache.kyuubi.config.KyuubiConf

object KyuubiHadoopUtils {

  def newHadoopConf(conf: KyuubiConf): Configuration = {
    val hadoopConf = new Configuration()
    conf.getAll.foreach { case (k, v) => hadoopConf.set(k, v) }
    hadoopConf
  }

  def getServerPrincipal(principal: String): String = {
    SecurityUtil.getServerPrincipal(principal, "0.0.0.0")
  }

  def encodeCredentials(creds: Credentials): String = {
    val buf = new DataOutputBuffer
    creds.write(buf)
    val encoder = new Base64(0, null, false)
    val raw = new Array[Byte](buf.getLength)
    System.arraycopy(buf.getData, 0, raw, 0, buf.getLength)
    encoder.encodeToString(raw)
  }

  def decodeCredentials(newValue: String): Credentials = {
    val creds = new Credentials()
    val decoder = new Base64(0, null, false)
    val buf = new DataInputBuffer
    val decoded = decoder.decode(newValue)
    buf.reset(decoded, decoded.length)
    creds.readFields(buf)
    creds
  }

}
