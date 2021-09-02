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

package org.apache.kyuubi.ha.client

import java.io.{File, IOException}
import java.net.InetAddress
import javax.security.auth.login.Configuration

import scala.collection.JavaConverters._

import org.apache.hadoop.util.StringUtils
import org.apache.zookeeper.ZooDefs
import org.scalatest.time.SpanSugar._

import org.apache.kyuubi.{KerberizedTestHelper, KYUUBI_VERSION}
import org.apache.kyuubi.config.KyuubiConf
import org.apache.kyuubi.ha.HighAvailabilityConf
import org.apache.kyuubi.ha.HighAvailabilityConf._
import org.apache.kyuubi.service.{NoopServer, Serverable, ServiceState}
import org.apache.kyuubi.zookeeper.{EmbeddedZookeeper, ZookeeperConf}

class ServiceDiscoverySuite extends KerberizedTestHelper {
  import ZooKeeperClientProvider._

  val zkServer = new EmbeddedZookeeper()
  val conf: KyuubiConf = KyuubiConf()

  override def beforeAll(): Unit = {
    conf.set(ZookeeperConf.ZK_CLIENT_PORT, 0)
    zkServer.initialize(conf)
    zkServer.start()
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    conf.unset(KyuubiConf.SERVER_KEYTAB)
    conf.unset(KyuubiConf.SERVER_PRINCIPAL)
    conf.unset(HA_ZK_QUORUM)
    zkServer.stop()
    super.afterAll()
  }

  test("publish instance to embedded zookeeper server") {
    val namespace = "kyuubiserver"

    conf
      .unset(KyuubiConf.SERVER_KEYTAB)
      .unset(KyuubiConf.SERVER_PRINCIPAL)
      .set(HA_ZK_QUORUM, zkServer.getConnectString)
      .set(HA_ZK_NAMESPACE, namespace)
      .set(KyuubiConf.FRONTEND_THRIFT_BINARY_BIND_PORT, 0)

    val server: Serverable = new NoopServer()
    server.initialize(conf)
    server.start()

    val znodeRoot = s"/$namespace"
    val serviceDiscovery = new KyuubiServiceDiscovery(server)
    withZkClient(conf) { framework =>
      try {
        serviceDiscovery.initialize(conf)
        serviceDiscovery.start()

        assert(framework.checkExists().forPath("/abc") === null)
        assert(framework.checkExists().forPath(znodeRoot) !== null)
        val children = framework.getChildren.forPath(znodeRoot).asScala
        assert(children.head ===
          s"serviceUri=${server.connectionUrl};version=$KYUUBI_VERSION;sequence=0000000000")

        children.foreach { child =>
          framework.delete().forPath(s"""$znodeRoot/$child""")
        }
        eventually(timeout(5.seconds), interval(1.second)) {
          assert(serviceDiscovery.getServiceState === ServiceState.STOPPED)
          assert(server.getServiceState === ServiceState.STOPPED)
        }
      } finally {
        server.stop()
        serviceDiscovery.stop()
      }
    }
  }

  test("acl for zookeeper") {
    val provider = new ZooKeeperACLProvider(conf)
    val acl = provider.getDefaultAcl
    assert(acl.size() === 1)
    assert(acl === ZooDefs.Ids.OPEN_ACL_UNSAFE)

    val conf1 = conf.clone.set(HA_ZK_ACL_ENABLED, true)
    val acl1 = new ZooKeeperACLProvider(conf1).getDefaultAcl
    assert(acl1.size() === 2)
    val expected = ZooDefs.Ids.READ_ACL_UNSAFE
    expected.addAll(ZooDefs.Ids.CREATOR_ALL_ACL)
    assert(acl1 === expected)
  }

  test("set up zookeeper auth") {
    tryWithSecurityEnabled {
      val keytab = File.createTempFile("kentyao", ".keytab")
      val principal = "kentyao/_HOST@apache.org"

      conf.set(KyuubiConf.SERVER_KEYTAB, keytab.getCanonicalPath)
      conf.set(KyuubiConf.SERVER_PRINCIPAL, principal)
      conf.set(HighAvailabilityConf.HA_ZK_ACL_ENABLED, true)

      ZooKeeperClientProvider.setUpZooKeeperAuth(conf)
      val configuration = Configuration.getConfiguration
      val entries = configuration.getAppConfigurationEntry("KyuubiZooKeeperClient")

      assert(entries.head.getLoginModuleName === "com.sun.security.auth.module.Krb5LoginModule")
      val options = entries.head.getOptions.asScala.toMap

      val hostname = StringUtils.toLowerCase(InetAddress.getLocalHost.getCanonicalHostName)
      assert(options("principal") === s"kentyao/$hostname@apache.org")
      assert(options("useKeyTab").toString.toBoolean)

      conf.set(KyuubiConf.SERVER_KEYTAB, keytab.getName)
      val e = intercept[IOException](ZooKeeperClientProvider.setUpZooKeeperAuth(conf))
      assert(e.getMessage === s"${KyuubiConf.SERVER_KEYTAB.key} does not exists")
    }
  }

  test("KYUUBI-304: Stop engine service gracefully when related zk node is deleted") {
    val logAppender = new LogAppender("test stop engine gracefully")
    withLogAppender(logAppender) {
      val namespace = "kyuubiengine"

      conf
        .unset(KyuubiConf.SERVER_KEYTAB)
        .unset(KyuubiConf.SERVER_PRINCIPAL)
        .set(HA_ZK_QUORUM, zkServer.getConnectString)
        .set(HA_ZK_NAMESPACE, namespace)
        .set(KyuubiConf.FRONTEND_THRIFT_BINARY_BIND_PORT, 0)
        .set(HA_ZK_ACL_ENABLED, false)

      val server: Serverable = new NoopServer()
      server.initialize(conf)
      server.start()

      val znodeRoot = s"/$namespace"
      val serviceDiscovery = new EngineServiceDiscovery(server)
      withZkClient(conf) { framework =>
        try {
          serviceDiscovery.initialize(conf)
          serviceDiscovery.start()

          assert(framework.checkExists().forPath("/abc") === null)
          assert(framework.checkExists().forPath(znodeRoot) !== null)
          val children = framework.getChildren.forPath(znodeRoot).asScala
          assert(children.head ===
            s"serviceUri=${server.connectionUrl};version=$KYUUBI_VERSION;sequence=0000000000")

          children.foreach { child =>
            framework.delete().forPath(s"""$znodeRoot/$child""")
          }
          eventually(timeout(5.seconds), interval(1.second)) {
            assert(serviceDiscovery.getServiceState === ServiceState.STOPPED)
            assert(server.getServiceState === ServiceState.STOPPED)
            val msg = s"This Kyuubi instance ${server.connectionUrl} is now de-registered"
            assert(logAppender.loggingEvents.exists(_.getRenderedMessage.contains(msg)))
          }
        } finally {
          server.stop()
          serviceDiscovery.stop()
        }
      }
    }
  }
}
