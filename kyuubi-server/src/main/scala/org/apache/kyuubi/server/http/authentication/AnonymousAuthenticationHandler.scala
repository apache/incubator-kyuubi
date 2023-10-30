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

package org.apache.kyuubi.server.http.authentication

import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.apache.kyuubi.Logging
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.server.http.authentication.AuthSchemes.AuthScheme

class AnonymousAuthenticationHandler extends AuthenticationHandler with Logging {
  import AuthenticationHandler._

  private var conf: KyuubiConf = _

  // support extracting username from basic authentication header
  override val authScheme: AuthScheme = AuthSchemes.BASIC

  override def init(conf: KyuubiConf): Unit = {
    this.conf = conf
  }

  override def authenticationSupported: Boolean = true

  override def matchAuthScheme(authorization: String): Boolean = {
    authorization == null || authorization.isEmpty || super.matchAuthScheme(authorization)
  }

  override def getAuthorization(request: HttpServletRequest): String = {
    val authHeader = request.getHeader(AUTHORIZATION_HEADER)
    if (authHeader == null || authHeader.isEmpty) {
      ""
    } else {
      super.getAuthorization(request)
    }
  }

  override def authenticate(
      request: HttpServletRequest,
      response: HttpServletResponse): String = {
    val authorization = getAuthorization(request)
    val inputToken = Option(authorization).map(a => Base64.getDecoder.decode(a.getBytes()))
      .getOrElse(Array.empty[Byte])
    val creds = new String(inputToken, StandardCharsets.UTF_8).split(":")
    creds.take(1).headOption.filterNot(_.isEmpty).getOrElse("anonymous")
  }

  override def destroy(): Unit = {}
}
