/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.domingosuarez.boot.actuate.health;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.util.Assert;

import java.util.AbstractMap.SimpleEntry;
import java.util.function.Function;

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

  public RabbitMQHealthIndicator(RabbitTemplate rabbitTemplate) {
    Assert.notNull(rabbitTemplate, "RabbitTemplate must not be null.");
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  protected void doHealthCheck(Health.Builder builder) throws Exception {
    new HealthIndicatorCommand(rabbitTemplate, builder).execute();
  }

  static class HealthIndicatorCommand extends HystrixCommand<Health.Builder> {
    private RabbitTemplate rabbitTemplate;
    private Health.Builder builder;

    public HealthIndicatorCommand(RabbitTemplate rabbitTemplate, Health.Builder builder) {
      super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
      this.builder = builder;
      this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    protected Health.Builder run() throws Exception {
      return builder.up()
        .withDetail("server_properties", rabbitTemplate.execute(channel ->
          channel.getConnection().getServerProperties().entrySet().stream()
            .map(e -> {
              Object value = e.getValue().toString();
              if (e.getValue().getClass().equals(java.util.HashMap.class)) {
                value = e.getValue();
              }
              return new SimpleEntry<>(e.getKey(), value);
            })
            .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
    }

    @Override
    protected Health.Builder getFallback() {
      return builder.status(RABBIT_DOWN)
        .withException((Exception) getFailedExecutionException());
    }
  }
}
