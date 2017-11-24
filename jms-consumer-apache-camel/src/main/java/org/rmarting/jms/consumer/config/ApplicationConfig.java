package org.rmarting.jms.consumer.config;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.camel.component.ActiveMQConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

@Configuration
@ConfigurationProperties
public class ApplicationConfig {
	
	@Value("${activemq.broker.url}")
	private String amqBrokerUrl;

	@Value("${activemq.broker.username}")
	private String username;

	@Value("${activemq.broker.password}")
	private String password;
	
	@Bean
	public ConnectionFactory connectionFactory() {
		// Create a ConnectionFactory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.amqBrokerUrl);
		
		connectionFactory.setUserName(this.username);
		connectionFactory.setPassword(this.password);
		
		return connectionFactory;
	}

	@Bean
	public JmsListenerContainerFactory<?> amqBrokerFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		// This provides all boot's default to this factory, including the
		// message converter
		configurer.configure(factory, connectionFactory);
		// You could still override some of Boot's default if necessary.
		return factory;
	}

	@Bean
	public ActiveMQConfiguration jmsConfig(ConnectionFactory connectionFactory) {
		ActiveMQConfiguration activeMQConfiguration = new ActiveMQConfiguration();
		activeMQConfiguration.setConnectionFactory(connectionFactory);

		return activeMQConfiguration;
	}

	@Bean
	public ActiveMQComponent amq(ActiveMQConfiguration jmsConfig) {
		ActiveMQComponent activeMQComponent = new ActiveMQComponent();
		
		activeMQComponent.setConfiguration(jmsConfig);

		return activeMQComponent;
	}
	
}
