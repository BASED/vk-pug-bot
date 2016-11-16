package ru.etherlands.vk_pug_bot.listeners;

/**
 * Created by ssosedkin on 07.11.2016.
 */

import org.apache.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.etherlands.vk_pug_bot.Constants;
import ru.etherlands.vk_pug_bot.commands.AbstractCommand;
import ru.etherlands.vk_pug_bot.commands.CommandsProcessor;
import ru.etherlands.vk_pug_bot.commands.KittenCommand;
import ru.etherlands.vk_pug_bot.dto.PugMessage;
import ru.etherlands.vk_pug_bot.server.ServiceProvider;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    ServiceProvider serviceProvider;

    @RabbitListener(queues = Constants.INCOMING_COMMANDS_QUEUE)
    public void processMessage(Message message) {
        try {
            logger.info("accepted on worker 1 : " + message);
            PugMessage incomingMessage = (PugMessage) incomingTemplate.getMessageConverter().fromMessage(message);
            logger.info("Received object: " + incomingMessage);
            if (incomingMessage.getBody() == null || incomingMessage.getBody().isEmpty()) {
                return;
            }

            List<PugMessage> outgoingMessages = CommandsProcessor.getCommandExecution(incomingMessage, serviceProvider);

            if (outgoingMessages != null) {
                for (PugMessage outgoingMessage : outgoingMessages) {
                    //if not specified by command processor,  send to source
                    if (outgoingMessage.getUserId() == null && outgoingMessage.getChatId() == null) {
                        outgoingMessage.setUserId(incomingMessage.getUserId());
                        outgoingMessage.setChatId(incomingMessage.getChatId());
                    }

                    outgoingTemplate.convertAndSend(outgoingMessage);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


}
