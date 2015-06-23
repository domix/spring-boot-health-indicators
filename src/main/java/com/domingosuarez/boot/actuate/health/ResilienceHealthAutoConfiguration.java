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
import com.domingosuarez.boot.actuate.health.config.ResilienceHealthProperties;
import com.netflix.hystrix.Hystrix;
import com.rabbitmq.client.Channel;
import com.rabbitmq.http.client.Client;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author Domingo Suarez Torres
 * @since 0.1.1
 */
@Configuration
@ConditionalOnClass({Hystrix.class, HealthIndicator.class})
@AutoConfigureAfter({HealthIndicatorAutoConfiguration.class})
@EnableConfigurationProperties(ResilienceHealthProperties.class)
public class ResilienceHealthAutoConfiguration {

  @Autowired
  private ResilienceHealthProperties configurationProperties = new ResilienceHealthProperties();

  @Configuration
  @ConditionalOnClass({RabbitTemplate.class, Channel.class})
  @ConditionalOnProperty(prefix = "resilience.health.rabbit", name = "enabled", matchIfMissing = true)
  @EnableConfigurationProperties(RabbitMQResilienceHealthProperties.class)
  public static class RabbitHealthIndicatorConfiguration {
    @Autowired
    private RabbitMQResilienceHealthProperties properties = new RabbitMQResilienceHealthProperties();

    @Autowired
    private HealthAggregator healthAggregator;

    @Autowired
    private Map<String, RabbitTemplate> rabbitTemplates;

    @Autowired
    private RabbitProperties rabbitProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnClass(Client.class)
    @ConditionalOnMissingBean(RabbitMQManagement.class)
    public RabbitMQManagement rabbitMQManagementInformation() {
      return new RabbitMQManagement(rabbitProperties);
    }

    @Bean
    @ConditionalOnMissingBean(RabbitMQHealthIndicator.class)
    public HealthIndicator rabbitMQHealthIndicator() {
      if (this.rabbitTemplates.size() == 1) {
        return new RabbitMQHealthIndicator(applicationContext, this.rabbitTemplates.values().iterator().next(), properties);
      }

      CompositeHealthIndicator composite = new CompositeHealthIndicator(this.healthAggregator);

      rabbitTemplates.entrySet().stream().
        forEach(entry -> composite.addHealthIndicator(entry.getKey(), new RabbitMQHealthIndicator(applicationContext, entry.getValue(), properties)));

      return composite;
    }
  }
}
