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

import java.util.{Date, Set}

import scala.collection.JavaConverters._

import org.apache.hadoop.security.UserGroupInformation
import org.apache.ranger.plugin.policyengine.{RangerAccessRequestImpl, RangerPolicyEngine}

import org.apache.kyuubi.plugin.spark.authz.OperationType.OperationType
import org.apache.kyuubi.plugin.spark.authz.ranger.AccessType._

case class AccessRequest private (accessType: AccessType) extends RangerAccessRequestImpl

object AccessRequest {
  def apply(
      resource: AccessResource,
      user: UserGroupInformation,
      opType: OperationType,
      accessType: AccessType): AccessRequest = {
    val userName = user.getShortUserName
    val groups = {
      try {
        val enableOverrideUserGroupFromUserStore = SparkRangerAdminPlugin.getConfig.getBoolean(
          s"ranger.plugin.${SparkRangerAdminPlugin.getConfig.getServiceType}" +
            s".enable.override.usergroup.from.userstore", false);
        if (!enableOverrideUserGroupFromUserStore) {
          user.getGroupNames.toSet.asJava
        }

        val getUserStoreEnricher = SparkRangerAdminPlugin.getClass.getMethod(
          "getUserStoreEnricher")
        getUserStoreEnricher.setAccessible(true)
        val storeEnricher = getUserStoreEnricher.invoke(SparkRangerAdminPlugin)

        val getRangerUserStore = storeEnricher.getClass.getMethod("getRangerUserStore")
        getRangerUserStore.setAccessible(true)
        val userStore = getRangerUserStore.invoke(storeEnricher)

        val getUserGroupMapping = userStore.getClass.getMethod("getUserGroupMapping")
        getUserGroupMapping.setAccessible(true)

        val userGroupMappingMap: scala.collection.mutable.Map[String, java.util.Set[String]] =
          mapAsScalaMap(getUserGroupMapping.invoke(userStore)
            .asInstanceOf[java.util.LinkedHashMap[String, java.util.Set[String]]])

        val userGroupsFromUserStore = userGroupMappingMap.get(userName).orNull

        if (userGroupsFromUserStore != null) userGroupsFromUserStore
        else user.getGroupNames.toSet.asJava

      } catch {
        case _: NoSuchMethodException =>
          user.getGroupNames.toSet.asJava
      }
    }

    val req = new AccessRequest(accessType)
    req.setResource(resource)
    req.setUser(userName)
    req.setUserGroups(groups)
    req.setAction(opType.toString)
    try {
      val getRoles = SparkRangerAdminPlugin.getClass.getMethod(
        "getRolesFromUserAndGroups",
        classOf[String],
        classOf[java.util.Set[String]])
      getRoles.setAccessible(true)
      val roles = getRoles.invoke(SparkRangerAdminPlugin, userName, groups)
      val setRoles = req.getClass.getMethod("setUserRoles", classOf[java.util.Set[String]])
      setRoles.setAccessible(true)
      setRoles.invoke(req, roles)
    } catch {
      case _: NoSuchMethodException =>
    }
    req.setAccessTime(new Date())
    accessType match {
      case USE => req.setAccessType(RangerPolicyEngine.ANY_ACCESS)
      case _ => req.setAccessType(accessType.toString.toLowerCase)
    }
    try {
      val getClusterName = SparkRangerAdminPlugin.getClass.getMethod("getClusterName")
      getClusterName.setAccessible(true)
      val clusterName = getClusterName.invoke(SparkRangerAdminPlugin)
      val setClusterName = req.getClass.getMethod("setClusterName", classOf[String])
      setClusterName.setAccessible(true)
      setClusterName.invoke(req, clusterName)
    } catch {
      case _: NoSuchMethodException =>
    }
    req
  }
}
