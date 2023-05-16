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

package org.apache.kyuubi.engine

import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.engine.ApplicationState.ApplicationState

trait ApplicationOperation {

  /**
   * Step for initializing the instance.
   */
  def initialize(conf: KyuubiConf): Unit

  /**
   * Step to clean up the instance
   */
  def stop(): Unit

  /**
   * Called before other method to do a quick skip
   *
   * @param appMgrInfo the application manager information
   */
  def isSupported(appMgrInfo: ApplicationManagerInfo): Boolean

  /**
   * Kill the app/engine by the unique application tag
   *
   * @param appMgrInfo the application manager information
   * @param tag the unique application tag for engine instance.
   *            For example,
   *            if the Hadoop Yarn is used, for spark applications,
   *            the tag will be preset via spark.yarn.tags
   * @return a message contains response describing how the kill process.
   *
   * @note For implementations, please suppress exceptions and always return KillResponse
   */
  def killApplicationByTag(appMgrInfo: ApplicationManagerInfo, tag: String): KillResponse

  /**
   * Get the engine/application status by the unique application tag
   *
   * @param tag the unique application tag for engine instance.
   * @param submitTime engine submit to resourceManager time
   * @return [[ApplicationInfo]]
   */
  def getApplicationInfoByTag(tag: String, submitTime: Option[Long] = None): ApplicationInfo
}

object ApplicationState extends Enumeration {
  type ApplicationState = Value
  val PENDING, RUNNING, FINISHED, KILLED, FAILED, ZOMBIE, NOT_FOUND, UNKNOWN = Value

  def isFailed(state: ApplicationState): Boolean = state match {
    case FAILED => true
    case KILLED => true
    case _ => false
  }

  def isTerminated(state: ApplicationState): Boolean = {
    state match {
      case FAILED => true
      case KILLED => true
      case FINISHED => true
      case NOT_FOUND => true
      case _ => false
    }
  }
}

case class ApplicationInfo(
    id: String,
    name: String,
    state: ApplicationState,
    url: Option[String] = None,
    error: Option[String] = None) {

  def toMap: Map[String, String] = {
    Map(
      "id" -> id,
      "name" -> name,
      "state" -> state.toString,
      "url" -> url.orNull,
      "error" -> error.orNull)
  }
}

object ApplicationInfo {
  val NOT_FOUND: ApplicationInfo = ApplicationInfo(null, null, ApplicationState.NOT_FOUND)
  val UNKNOWN: ApplicationInfo = ApplicationInfo(null, null, ApplicationState.UNKNOWN)
}

object ApplicationOperation {
  val NOT_FOUND = "APPLICATION_NOT_FOUND"
}

case class KubernetesInfo(context: String, namespace: String)

case class ApplicationManagerInfo(
    resourceManager: Option[String],
    kubernetesInfo: Option[KubernetesInfo] = None)

object ApplicationManagerInfo {
  def apply(
      resourceManager: Option[String],
      kubernetesContext: Option[String],
      kubernetesNamespace: Option[String]): ApplicationManagerInfo = {
    val kubernetesInfo = if (kubernetesContext.isDefined && kubernetesNamespace.isDefined) {
      Some(KubernetesInfo(kubernetesContext.get, kubernetesNamespace.get))
    } else {
      None
    }
    new ApplicationManagerInfo(resourceManager, kubernetesInfo)
  }
}
