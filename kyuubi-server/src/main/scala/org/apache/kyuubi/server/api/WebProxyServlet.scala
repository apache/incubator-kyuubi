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

package org.apache.kyuubi.server.api

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.eclipse.jetty.client.api.Response
import org.eclipse.jetty.proxy.ProxyServlet

import org.apache.kyuubi.Logging
import org.apache.kyuubi.config.KyuubiConf

private[api] class WebProxyServlet(conf: KyuubiConf) extends ProxyServlet with Logging {
  var ipAddress: String = _
  var port: Int = _

  override def rewriteTarget(request: HttpServletRequest): String = {
    var targetUrl = "/no-ui-error"
    val requestUrl = request.getRequestURI
    logger.info("requestUrl is {}", requestUrl)
    val url = requestUrl.split("/")
    logger.info("url is {}", url)
    if (url != null && url.length > 0) {
      ipAddress = url(2).split(":")(0)
      port = url(2).split(":")(1).toInt
      val path = requestUrl.substring(("/" + url(1) + "/" + url(2) + "/").length)
      targetUrl = String.format(
        "http://%s:%s/%s/",
        ipAddress,
        port.toString,
        path)
      logger.info("ui -> {}", targetUrl)
    }
    targetUrl
  }

  override def onProxyResponseSuccess(
      clientRequest: HttpServletRequest,
      proxyResponse: HttpServletResponse,
      serverResponse: Response): Unit = {
    if (proxyResponse != null && proxyResponse.getContentType.contains("text/html")) {
      val wrapResponse = new WrapResponse(proxyResponse)
      val data = wrapResponse.getData
      val firstReplacement = "href=\"/proxy/" + s"$ipAddress:$port/"
      val secondReplacement = "src=\"/proxy/" + s"$ipAddress:$port/"
      val thirdReplacement = s"/proxy/$ipAddress:$port/api/v1/"
      val fourthReplacement = s"/proxy/$ipAddress:$port/static/"
      val newData = data.replaceAll("href=\"/", firstReplacement)
        .replaceAll("src=\"/", secondReplacement)
        .replaceAll("/api/v1/", thirdReplacement)
        .replaceAll("/static/", fourthReplacement)
      val out = proxyResponse.getWriter
      out.write(newData)
      out.flush()
      out.close
    }
    super.onProxyResponseSuccess(clientRequest, proxyResponse, serverResponse)
  }
}
