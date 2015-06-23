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
package com.domingosuarez.boot.actuate.health.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Hystrix command & the health indicator.
 *
 * @author Domingo Suarez Torres
 * @since 0.1.6
 */
@ConfigurationProperties(prefix = "resilience.health.rabbitmq")
public class RabbitMQResilienceHealthProperties {

  /**
   * The Hystrix CommandGroupKey.
   * Default to 'resilience.health'.
   */
  @Setter
  @Getter
  private String hystrixCommandGroupKey = "resilience.health";

  /**
   * The Hystrix ThreadPoolKey.
   * Default to 'HealthIndicators'.
   */
  @Setter
  @Getter
  private String hystrixThreadPoolKey = "HealthIndicators";

  /**
   * The Hystrix CommandKey.
   * Default to 'rabbitHealth'.
   */
  @Setter
  @Getter
  private String hystrixCommandKey = "rabbitHealth";

  /**
   * The Hystrix executionTimeoutInMilliseconds.
   * Default to '1000'.
   */
  @Setter
  @Getter
  private Integer executionIsolationThreadTimeoutInMilliseconds = 1000; // default => executionTimeoutInMilliseconds: 1000 = 1 second;

  /**
   * When RabbitMQ is Up and running, the health indicator will include all the server properties by default.
   * Setting this flag to false will only include the server version.
   */
  @Setter
  @Getter
  private Boolean includeServerProperties = Boolean.TRUE;

  /**
   * When RabbitMQ is down, the default response by the indicator will be 'RABBIT_DOWN'
   * and the Application health will not be affected by the failing RabbitMQ.
   * Setting this flag to true will behave like the default one, this means the response
   * will be 'DOWN' and the Application health will be 'DOWN' as well.
   */
  @Setter
  @Getter
  private Boolean useClassicDown = Boolean.FALSE;

  @Setter
  @Getter
  private Integer managementPort = 15672;

  @Setter
  @Getter
  private String managementProtocol = "http";

  @Setter
  @Getter
  private String managementEndpoint = "/api/";
}
