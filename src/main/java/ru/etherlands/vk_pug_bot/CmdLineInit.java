package ru.etherlands.vk_pug_bot;

import org.apache.log4j.Logger;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by ssosedkin on 07.11.2016.
 */

@Component
public class CmdLineInit implements CommandLineRunner {


    @Autowired
    RabbitTemplate template;

    Logger logger = Logger.getLogger(QueueConfiguration.class);

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("ololo");
        template.setExchange(Constants.INCOMING_EXCHANGE);
        template.convertAndSend("Fanout message");
    }
}