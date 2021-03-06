
0.1.17 / 2015-06-23
==================

  * general cleanup, improved how to get the RabbitMQManagement

0.1.16 / 2015-06-22
==================

  * fix wrong default management protocol

0.1.15 / 2015-06-22
==================

  * adding some logs

0.1.14 / 2015-06-22
==================

  * added configuration settings for management plugin

0.1.13 / 2015-06-22
==================

  * fix missing credentials: added default username & password if they are not set

0.1.12 / 2015-06-22
==================

  * splitting the configuration to fix a startup issue.

0.1.11 / 2015-06-22
==================

  * added RabbitMQ management info
  * upgrade some dependencies
  * just a minor style improvement
  * using Optional instead of ternary operator
  * Documenting the settings for RabbitMQ health indicator and javadocs.

0.1.10 / 2015-06-01
==================

  * fixed a bug when includeServerProperties is off

0.1.9 / 2015-06-01
==================

  * fixed a bug when includeServerProperties is off

0.1.8 / 2015-06-01
==================

  * added the ability to disable the inclusion of RabbitMQ server properties
  * added the ability to enable the classical behavior of the SpringBoot Rabbit health indicator

0.1.7 / 2015-06-01
==================

  * eliminating all the runtime dependencies

0.1.6 / 2015-06-01
==================

  * allow to configure the Hystrix command for RabbitMQ health
  * added some docs

0.1.5 / 2015-05-31
==================

  * added a validation to prevent duplicate bean registration if the user register their own RabbitMQHealthIndicator

0.1.4 / 2015-05-31
==================

  * improved configuration

0.1.3 / 2015-05-31
==================

  * minor tweaks to improve the configuration

0.1.2 / 2015-05-31
==================

  * minor tweaks to improve the configuration

0.1.1 / 2015-05-31
==================

  * added Auto-configuration support
  * added support for multiple RabbitTemplates
  * Added a convenience annotation to Enable the ResilienceHealth stuff

0.1.0 / 2015-05-31
==================

  * initial release
