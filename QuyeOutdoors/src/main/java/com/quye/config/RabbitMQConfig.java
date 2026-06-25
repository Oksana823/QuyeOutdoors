package com.quye.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public DirectExchange passOrderExchange() {
        return new DirectExchange("quye.pass.order.exchange", true, false);
    }

    @Bean
    public Queue passOrderQueue() {
        return new Queue("quye.pass.order.queue", true);
    }

    @Bean
    public Binding passOrderBinding() {
        return BindingBuilder
                .bind(passOrderQueue())
                .to(passOrderExchange())
                .with("quye.pass.order.create");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
