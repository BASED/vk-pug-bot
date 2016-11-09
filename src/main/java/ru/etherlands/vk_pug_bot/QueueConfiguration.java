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

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        return rabbitTemplate;
    }


    @Bean
    public Queue incomingHelloQueue() {
        return new Queue(Constants.INCOMING_HELLO_QUEUE);
    }

    @Bean
    public Queue incomingCatsQueue() {
        return new Queue(Constants.INCOMING_CATS_QUEUE);
    }

    @Bean
    public FanoutExchange incomingExchange(){
        return new FanoutExchange(Constants.INCOMING_EXCHANGE);
    }

    @Bean
    public Binding bindingHello(){
        return BindingBuilder.bind(incomingHelloQueue()).to(incomingExchange());
    }

    @Bean
    public Binding bindingCats(){
        return BindingBuilder.bind(incomingCatsQueue()).to(incomingExchange());
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(QueueConfiguration.class, args);

    }
}