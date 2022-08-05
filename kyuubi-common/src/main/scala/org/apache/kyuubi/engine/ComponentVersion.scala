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

/**
 * Encapsulate a component (Kyuubi/Spark/Hive/Flink etc.) version
 * for the convenience of version checks.
 */
case class ComponentVersion(majorVersion: Int, minorVersion: Int) {

  def isVersionAtMost(targetVersionString: String): Boolean = {
    this.compareVersion(targetVersionString, ComponentVersion.atMost)
  }

  def isVersionAtLeast(targetVersionString: String): Boolean = {
    this.compareVersion(targetVersionString, ComponentVersion.atLeast)
  }

  def isVersionEqualTo(targetVersionString: String): Boolean = {
    this.compareVersion(targetVersionString, ComponentVersion.equalTo)
  }

  def compareVersion(
      targetVersionString: String,
      callback: (Int, Int, Int, Int) => Boolean): Boolean = {
    val targetVersion = ComponentVersion(targetVersionString)
    val targetMajor = targetVersion.majorVersion
    val targetMinor = targetVersion.minorVersion
    callback(targetMajor, targetMinor, this.majorVersion, this.minorVersion)
  }
}

object ComponentVersion {

  def apply(versionString: String): ComponentVersion = {
    """^(\d+)\.(\d+)(\..*)?$""".r.findFirstMatchIn(versionString) match {
      case Some(m) =>
        ComponentVersion(m.group(1).toInt, m.group(2).toInt)
      case None =>
        throw new IllegalArgumentException(s"Tried to parse '$versionString' as a project" +
          s" version string, but it could not find the major and minor version numbers.")
    }
  }

  def isVersionAtMost(targetVersionString: String, runtimeVersionString: String): Boolean = {
    compareVersion(
      targetVersionString,
      runtimeVersionString,
      atMost)
  }

  def isVersionAtLeast(targetVersionString: String, runtimeVersionString: String): Boolean = {
    compareVersion(
      targetVersionString,
      runtimeVersionString,
      atLeast)
  }

  def isVersionEqualTo(targetVersionString: String, runtimeVersionString: String): Boolean = {
    compareVersion(
      targetVersionString,
      runtimeVersionString,
      equalTo)
  }

  def compareVersion(
      targetVersionString: String,
      runtimeVersionString: String,
      callback: (Int, Int, Int, Int) => Boolean): Boolean = {
    val runtimeVersion = ComponentVersion(runtimeVersionString)
    val targetVersion = ComponentVersion(targetVersionString)
    val runtimeMajor = runtimeVersion.majorVersion
    val runtimeMinor = runtimeVersion.minorVersion
    val targetMajor = targetVersion.majorVersion
    val targetMinor = targetVersion.minorVersion
    callback(targetMajor, targetMinor, runtimeMajor, runtimeMinor)
  }

  def atMost(targetMajor: Int, targetMinor: Int, runtimeMajor: Int, runtimeMinor: Int): Boolean = {
    (runtimeMajor < targetMajor) || {
      runtimeMajor == targetMajor && runtimeMinor <= targetMinor
    }
  }

  def atLeast(targetMajor: Int, targetMinor: Int, runtimeMajor: Int, runtimeMinor: Int): Boolean = {
    (runtimeMajor > targetMajor) || {
      runtimeMajor == targetMajor && runtimeMinor >= targetMinor
    }
  }

  def equalTo(targetMajor: Int, targetMinor: Int, runtimeMajor: Int, runtimeMinor: Int): Boolean = {
    runtimeMajor == targetMajor && runtimeMinor == targetMinor
  }
}
