package ru.etherlands.vk_pug_bot.listeners;

/**
 * Created by ssosedkin on 07.11.2016.
 */

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.etherlands.vk_pug_bot.Constants;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.Random;

@Component
public class HelloListener {
    Logger logger = Logger.getLogger(HelloListener.class);
    Random random = new Random();
    @Autowired
    RabbitTemplate template;


    @RabbitListener(queues = Constants.INCOMING_HELLO_QUEUE)
    public void worker1(Message message) {
        logger.info("accepted on worker 1 : " + message);
        PugMessage incomingMessage = (PugMessage) template.getMessageConverter().fromMessage(message);
        logger.info("Received object: " + incomingMessage);
        PugMessage outcomingMessage = new PugMessage(null, null, incomingMessage.getUserId(), null, null, "Gav: " + incomingMessage.getBody(), incomingMessage.getChatId(), null);
        template.setExchange(Constants.OUTCOMING_EXCHANGE);
        template.convertAndSend(outcomingMessage);
    }


}
