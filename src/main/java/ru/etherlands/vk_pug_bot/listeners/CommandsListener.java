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
import ru.etherlands.vk_pug_bot.commands.KittenCommand;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

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

    @RabbitListener(queues = Constants.INCOMING_COMMANDS_QUEUE)
    public void worker1(Message message) {
        logger.info("accepted on worker 1 : " + message);
        PugMessage incomingMessage = (PugMessage) incomingTemplate.getMessageConverter().fromMessage(message);
        logger.info("Received object: " + incomingMessage);
        if (incomingMessage.getBody() == null || incomingMessage.getBody().isEmpty()) {
            return;
        }

//        PugMessage outgoingMessage = new PugMessage(null, null, incomingMessage.getUserId(), null, null, "Gav: " + incomingMessage.getBody(), incomingMessage.getChatId(), null);
        PugMessage outgoingMessage = getCommandExecution(incomingMessage);
        if (outgoingMessage != null) {
            outgoingTemplate.convertAndSend(outgoingMessage);
        }
    }

    public PugMessage getCommandExecution (PugMessage incomingMessage) {
        List<AbstractCommand> commands = getCommands();
        PugMessage outgoingMessage = null;
        String messageBody = incomingMessage.getBody();
        if (messageBody.startsWith("!")) {
            String[] messageWords = messageBody.split(" ");
            String commandWord = messageWords[0].replace("!", "").toLowerCase();
            logger.info("CommandWord: " + commandWord);
            for(AbstractCommand command : commands) {
                if (command.getCommandWords().contains(commandWord)) {
                    outgoingMessage = command.executeCommand(incomingMessage);
                    //if not specified by command parser, send to source
                    if (outgoingMessage.getUserId() == null && outgoingMessage.getChatId() == null) {
                        outgoingMessage.setUserId(incomingMessage.getUserId());
                        outgoingMessage.setChatId(incomingMessage.getChatId());
                    }
                    break;
                }
            }
        }
        return outgoingMessage;
    };

    public List<AbstractCommand> getCommands() {
        List<AbstractCommand> commands = new ArrayList<>();
        commands.add(new KittenCommand());
        return commands;
    };

}
