package ru.etherlands.vk_pug_bot.listeners;

/**
 * Created by ssosedkin on 07.11.2016.
 */

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.etherlands.vk_pug_bot.Constants;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.Random;

@Component
public class CommandsListener {
    Logger logger = Logger.getLogger(CommandsListener.class);
    Random random = new Random();

    @Autowired
    @Qualifier("incomingTemplate")
    RabbitTemplate incomingTemplate;

    @Autowired
    @Qualifier("outgoingTemplate")
    RabbitTemplate outgoingTemplate;

    @RabbitListener(queues = Constants.INCOMING_COMMANDS_QUEUE)
    public void worker1(Message message) {
        logger.info("accepted on worker 1 : " + message);
        PugMessage incomingMessage = (PugMessage) incomingTemplate.getMessageConverter().fromMessage(message);
        logger.info("Received object: " + incomingMessage);
        PugMessage outgoingMessage = new PugMessage(null, null, incomingMessage.getUserId(), null, null, "Gav: " + incomingMessage.getBody(), incomingMessage.getChatId(), null);

        outgoingTemplate.convertAndSend(outgoingMessage);
    }


}
