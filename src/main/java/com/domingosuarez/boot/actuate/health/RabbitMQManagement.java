/**
 *
 * Copyright (C) 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.domingosuarez.boot.actuate.health;

import com.domingosuarez.boot.actuate.health.config.RabbitMQResilienceHealthProperties;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.NodeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

/**
 * @author Domingo Suarez Torres
 * @since 0.1.11
 */
@Slf4j
public class RabbitMQManagement implements InitializingBean {
  public static final String GUEST = "guest";
  private final RabbitProperties rabbitProperties;
  private final RabbitMQResilienceHealthProperties rabbitMQResilienceHealthProperties;
  private Client client = null;

  public RabbitMQManagement(RabbitProperties rabbitProperties, RabbitMQResilienceHealthProperties rabbitMQResilienceHealthProperties) {
    this.rabbitProperties = rabbitProperties;
    this.rabbitMQResilienceHealthProperties = rabbitMQResilienceHealthProperties;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      StringBuilder sb = new StringBuilder().append(rabbitMQResilienceHealthProperties.getManagementProtocol())
        .append("://")
        .append(rabbitProperties.getHost())
        .append(":")
        .append(rabbitMQResilienceHealthProperties.getManagementPort())
        .append(rabbitMQResilienceHealthProperties.getManagementEndpoint());

      String username = ofNullable(rabbitProperties.getUsername()).orElse(GUEST);
      String password = ofNullable(rabbitProperties.getPassword()).orElse(GUEST);
      String url = sb.toString();

      log.info("About to create a Rabbit HTTP client using url: {}.", url);

      client = new Client(url, username, password);
      log.info("Rabbit HTTP client created.");

    } catch (MalformedURLException | URISyntaxException e) {
      log.error(e.getMessage(), e);
    }
  }

  public Map<String, Object> getManagementInfo() {
    Map<String, Object> result = new HashMap<>();

    ofNullable(client).ifPresent(client -> {
      result.put("cluster_name", getClusterName(client));
      result.put("nodes", getNodes(client));
    });

    return result;
  }

  private List<NodeInfo> getNodes(Client client) {
    List<NodeInfo> result = null;
    try {
      return client.getNodes();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return ofNullable(result).orElse(emptyList());
  }

  private String getClusterName(Client client) {
    String result = null;
    try {
      result = client.getClusterName().getName();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return ofNullable(result).orElse("Unknown");
  }

}
