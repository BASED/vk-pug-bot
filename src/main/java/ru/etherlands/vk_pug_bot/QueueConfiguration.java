package ru.etherlands.vk_pug_bot;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRabbit
@EnableScheduling
@SpringBootApplication
public class QueueConfiguration {
    Logger logger = Logger.getLogger(QueueConfiguration.class);

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory("localhost");
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory());
        return rabbitAdmin;
    }

    @Bean("incomingTemplate")
    public RabbitTemplate incomingTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange(Constants.INCOMING_EXCHANGE);
        return rabbitTemplate;
    }

    @Bean("outgoingTemplate")
    public RabbitTemplate outgoingTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange(Constants.OUTGOING_EXCHANGE);
        return rabbitTemplate;
    }

    @Bean
    public Queue incomingCommandsQueue() {
        return new Queue(Constants.INCOMING_COMMANDS_QUEUE);
    }

    @Bean
    public Queue incomingReactQueue() {
        return new Queue(Constants.INCOMING_REACT_QUEUE);
    }

    @Bean
    public Queue outgoingQueue() {
        return new Queue(Constants.OUTGOING_QUEUE);
    }

    @Bean
    public FanoutExchange incomingExchange(){
        return new FanoutExchange(Constants.INCOMING_EXCHANGE);
    }

    @Bean
    public FanoutExchange outgoingExchange(){
        return new FanoutExchange(Constants.OUTGOING_EXCHANGE);
    }

    @Bean
    public Binding bindingCommands(){ return BindingBuilder.bind(incomingCommandsQueue()).to(incomingExchange()); }

    @Bean
    public Binding bindingReact(){
        return BindingBuilder.bind(incomingReactQueue()).to(incomingExchange());
    }

    @Bean
    public Binding bindingOutgoing() {
        return BindingBuilder.bind(outgoingQueue()).to(outgoingExchange());
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(QueueConfiguration.class, args);

    }
}