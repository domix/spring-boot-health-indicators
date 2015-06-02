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
 * @author Domingo Suarez Torres
 * @since 0.1.6
 */
@ConfigurationProperties(prefix = "resilience.health.rabbitmq")
public class RabbitMQResilienceHealthProperties {
  @Setter
  @Getter
  private String hystrixCommandGroupKey = "resilience.health";
  @Setter
  @Getter
  private String hystrixThreadPoolKey = "HealthIndicators";
  @Setter
  @Getter
  private String hystrixCommandKey = "rabbitMQ";
  @Setter
  @Getter
  private Integer executionIsolationThreadTimeoutInMilliseconds = 1000; // default => executionTimeoutInMilliseconds: 1000 = 1 second;
}
