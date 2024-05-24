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

package org.apache.kyuubi.engine.flink.security.token;

import static org.apache.flink.client.deployment.application.ApplicationConfiguration.APPLICATION_ARGS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.ConfigUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.security.token.DelegationTokenProvider;
import org.apache.flink.runtime.security.token.hadoop.HadoopDelegationTokenConverter;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.kyuubi.Utils$;
import org.apache.kyuubi.config.KyuubiConf;
import org.apache.kyuubi.config.KyuubiConf$;
import org.apache.kyuubi.config.KyuubiReservedKeys$;
import org.apache.kyuubi.engine.flink.FlinkEngineUtils$;
import org.apache.kyuubi.util.KyuubiHadoopUtils$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KyuubiDelegationTokenProvider implements DelegationTokenProvider {

  private static final Logger LOG = LoggerFactory.getLogger(KyuubiDelegationTokenProvider.class);

  public static volatile Map<Text, Token<? extends TokenIdentifier>> previousTokens;

  private long renewalInterval;

  @Override
  public void init(Configuration configuration) throws Exception {
    final List<String> programArgsList =
        ConfigUtils.decodeListFromConfig(configuration, APPLICATION_ARGS, String::new);
    KyuubiConf kyuubiConf = KyuubiConf$.MODULE$.apply(false);
    Utils$.MODULE$.fromCommandLineArgs(programArgsList.toArray(new String[0]), kyuubiConf);
    String engineCredentials =
        kyuubiConf
            .getOption(KyuubiReservedKeys$.MODULE$.KYUUBI_ENGINE_CREDENTIALS_KEY())
            .getOrElse(() -> "");
    if (StringUtils.isNotBlank(engineCredentials)) {
      LOG.info("Renew delegation token with engine credentials: {}", engineCredentials);
      FlinkEngineUtils$.MODULE$.renewDelegationToken(engineCredentials);
    }
    Credentials credentials = UserGroupInformation.getCurrentUser().getCredentials();
    previousTokens = new HashMap<>(credentials.getTokenMap());

    renewalInterval =
        Long.valueOf(
            kyuubiConf
                .getOption(KyuubiConf$.MODULE$.CREDENTIALS_RENEWAL_INTERVAL().key())
                .getOrElse(
                    () ->
                        KyuubiConf$.MODULE$
                            .CREDENTIALS_RENEWAL_INTERVAL()
                            .defaultVal()
                            .get()
                            .toString()));
  }

  @Override
  public ObtainedDelegationTokens obtainDelegationTokens() throws Exception {
    // Maybe updated by `FlinkTBinaryFrontendService.RenewDelegationToken`
    Credentials credentials = UserGroupInformation.getCurrentUser().getCredentials();
    Credentials newCredentials = new Credentials();
    for (Map.Entry<Text, Token<? extends TokenIdentifier>> tokenEntry :
        credentials.getTokenMap().entrySet()) {
      Text alias = tokenEntry.getKey();
      Token<? extends TokenIdentifier> token = tokenEntry.getValue();
      Token<? extends TokenIdentifier> previousToken = previousTokens.get(alias);
      if (previousToken == null
          || KyuubiHadoopUtils$.MODULE$.compareIssueDate(token, previousToken) > 0) {
        newCredentials.addToken(alias, token);
      }
    }
    previousTokens = new HashMap<>(credentials.getTokenMap());
    Optional<Long> validUntil = Optional.of(System.currentTimeMillis() + renewalInterval);
    return new ObtainedDelegationTokens(
        HadoopDelegationTokenConverter.serialize(credentials), validUntil);
  }

  @Override
  public boolean delegationTokensRequired() throws Exception {
    return true;
  }

  @Override
  public String serviceName() {
    return "kyuubi";
  }
}
