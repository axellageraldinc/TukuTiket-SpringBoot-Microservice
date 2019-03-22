package com.axell.tukutiket.nggomlebu.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@Configuration
public class RabbitConfiguration implements RabbitListenerConfigurer {

    @Value("${queue.payment.verified}")
    private String paymentVerifiedEvent;

    @Value("${queue.qrcode.created}")
    private String qrCodeCreatedEvent;

    @Value("${tukutiket.exchange}")
    private String tukutiketExchange;

    @Bean
    Queue paymentVerifiedEvent() {
        return new Queue(paymentVerifiedEvent, true);
    }

    @Bean
    Queue qrCodeCreatedEvent() {
        return new Queue(qrCodeCreatedEvent, true);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(tukutiketExchange);
    }

    @Bean
    Binding bindingPaymentVerifiedEvent(Queue paymentVerifiedEvent, DirectExchange exchange) {
        return BindingBuilder.bind(paymentVerifiedEvent).to(exchange).with(this.paymentVerifiedEvent);
    }

    @Bean
    Binding bindingQrcodeCreatedEvent(Queue qrCodeCreatedEvent, DirectExchange exchange) {
        return BindingBuilder.bind(qrCodeCreatedEvent).to(exchange).with(this.qrCodeCreatedEvent);
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    MessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());
        return messageHandlerMethodFactory;
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }
}
