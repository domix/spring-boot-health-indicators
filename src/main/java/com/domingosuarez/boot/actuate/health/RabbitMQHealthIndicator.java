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
import com.netflix.hystrix.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.util.Assert;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

/**
 * Simple implementation of a {@link org.springframework.boot.actuate.health.HealthIndicator} returning status information for the
 * RabbitMQ messaging system.
 * <p>
 * The Health check is performed using a Hystrix command.
 *
 * @author Domingo Suarez Torres
 * @since 0.1.0
 */
public class RabbitMQHealthIndicator extends AbstractHealthIndicator {
  private static final Status RABBIT_DOWN = new Status("RABBIT_DOWN");
  private final RabbitTemplate rabbitTemplate;
  private final RabbitMQResilienceHealthProperties properties;

  public RabbitMQHealthIndicator(RabbitTemplate rabbitTemplate, RabbitMQResilienceHealthProperties properties) {
    Assert.notNull(rabbitTemplate, "RabbitTemplate must not be null.");
    this.rabbitTemplate = rabbitTemplate;
    this.properties = properties;
  }

  @Override
  protected void doHealthCheck(Health.Builder builder) throws Exception {
    new HealthIndicatorCommand(builder, rabbitTemplate, properties).execute();
  }

  /**
   * Hystric command to perform the RabbitMQ health check.
   */
  static class HealthIndicatorCommand extends HystrixCommand<Health.Builder> {
    public static final String VERSION = "version";
    private RabbitTemplate rabbitTemplate;
    private final RabbitMQResilienceHealthProperties properties;
    private Health.Builder builder;

    /**
     * Creates the Hystrix command, using all the configuration values from RabbitMQResilienceHealthProperties
     *
     * @param builder
     * @param rabbitTemplate
     * @param properties
     */
    public HealthIndicatorCommand(Health.Builder builder, RabbitTemplate rabbitTemplate, RabbitMQResilienceHealthProperties properties) {
      super(HystrixCommand.Setter
        .withGroupKey(HystrixCommandGroupKey.Factory.asKey(properties.getHystrixCommandGroupKey()))
        .andCommandKey(HystrixCommandKey.Factory.asKey(properties.getHystrixCommandKey()))
        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(properties.getHystrixThreadPoolKey()))
        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(properties.getExecutionIsolationThreadTimeoutInMilliseconds())));
      this.builder = builder;
      this.rabbitTemplate = rabbitTemplate;
      this.properties = properties;
    }

    @Override
    protected Health.Builder run() throws Exception {
      Map<String, Object> serverProperties = rabbitTemplate.execute(channel ->
        channel.getConnection().getServerProperties().entrySet().stream()
          .map(e -> {
            Object value = e.getValue().toString();
            if (e.getValue().getClass().equals(HashMap.class)) {
              value = e.getValue();
            }

            return new SimpleEntry<>(e.getKey(), value);
          })
          .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue)));

      Health.Builder up = builder.up();

      if (properties.getIncludeServerProperties()) {
        return up.withDetail("server_properties", serverProperties);
      } else {
        String version = ofNullable(serverProperties.get(VERSION)).map(Object::toString).orElse("");
        return up.withDetail(VERSION, version);
      }
    }

    @Override
    protected Health.Builder getFallback() {
      Exception exception = getExceptionFromThrowable(getFailedExecutionException());

      if (properties.getUseClassicDown()) {
        return builder.down(exception);
      } else {
        return builder.status(RABBIT_DOWN).withException(exception);
      }
    }
  }
}
