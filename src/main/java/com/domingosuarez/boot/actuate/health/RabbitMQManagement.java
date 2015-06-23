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

import com.rabbitmq.http.client.Client;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

/**
 * @author Domingo Suarez Torres
 * @since 0.1.11
 */
public class RabbitMQManagement implements InitializingBean {
  private final RabbitProperties rabbitProperties;
  private Client client;

  public RabbitMQManagement(RabbitProperties rabbitProperties) {
    this.rabbitProperties = rabbitProperties;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      StringBuilder sb = new StringBuilder().append("http://")
        .append(rabbitProperties.getHost())
        .append(":")
        .append(rabbitProperties.getPort())
        .append("/api/");
      client = new Client(sb.toString(), rabbitProperties.getUsername(), rabbitProperties.getPassword());
    } catch (MalformedURLException | URISyntaxException e) {
      client = null;
    }
  }

  public Map<String, Object> getManagementInfo() {
    Map<String, Object> result = new HashMap<>();

    ofNullable(client).ifPresent(client -> {
      result.put("cluster-name", client.getClusterName().getName());
      result.put("nodes", client.getNodes());
    });

    return result;
  }

}
