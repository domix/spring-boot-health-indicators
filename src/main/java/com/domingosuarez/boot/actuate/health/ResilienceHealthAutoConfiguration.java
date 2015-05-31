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

import com.domingosuarez.boot.actuate.health.config.ResilienceHealthProperties;
import com.netflix.hystrix.HystrixCommand;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by domix on 31/05/15.
 */
@Configuration
@AutoConfigureBefore(EndpointAutoConfiguration.class)
@AutoConfigureAfter(HealthIndicatorAutoConfiguration.class)
@EnableConfigurationProperties(ResilienceHealthProperties.class)
public class ResilienceHealthAutoConfiguration {

  @Autowired
  private ResilienceHealthProperties configurationProperties = new ResilienceHealthProperties();

  @Configuration
  @ConditionalOnBean(RabbitTemplate.class)
  @ConditionalOnClass(HystrixCommand.class)
  @ConditionalOnProperty(prefix = "resilience.health.rabbit", name = "enabled", matchIfMissing = true)
  public static class RabbitHealthIndicatorConfiguration {

    @Autowired
    private HealthAggregator healthAggregator;

    @Autowired
    private Map<String, RabbitTemplate> rabbitTemplates;

    @Bean
    @ConditionalOnMissingBean(name = "rabbitMQHealthIndicator")
    public HealthIndicator rabbitMQHealthIndicator() {
      if (this.rabbitTemplates.size() == 1) {
        return new RabbitMQHealthIndicator(this.rabbitTemplates.values().iterator().next());
      }

      CompositeHealthIndicator composite = new CompositeHealthIndicator(this.healthAggregator);

      rabbitTemplates.entrySet().stream().
        forEach(entry -> composite.addHealthIndicator(entry.getKey(), new RabbitMQHealthIndicator(entry.getValue())));

      return composite;
    }
  }
}
