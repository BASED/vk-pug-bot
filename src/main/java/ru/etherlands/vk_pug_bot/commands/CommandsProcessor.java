package ru.etherlands.vk_pug_bot.commands;


import org.slf4j.Logger;
import ru.etherlands.vk_pug_bot.Constants;
import ru.etherlands.vk_pug_bot.commands.AbstractCommand;
import ru.etherlands.vk_pug_bot.commands.KittenCommand;
import ru.etherlands.vk_pug_bot.dto.PugMessage;
import ru.etherlands.vk_pug_bot.server.ServiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ssosedkin on 10.11.2016.
 */
public class CommandsProcessor {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(CommandsProcessor.class);

    public static List<PugMessage> getCommandExecution (PugMessage incomingMessage, ServiceProvider serviceProvider) {
        List<AbstractCommand> commands = getCommands();
        List<PugMessage> outgoingMessages = null;
        String messageBody = incomingMessage.getBody();
        if (messageBody.startsWith(Constants.COMMAND_PREFIX)) {
            String[] messageWords = messageBody.split(" ");
            String commandWord = messageWords[0].replace(Constants.COMMAND_PREFIX, "").toLowerCase();
            logger.info("Message Id: {} CommandWord: {} ", incomingMessage.getId(), commandWord);
            for(AbstractCommand command : commands) {
                if (command.getCommandWords().contains(commandWord)) {
                    logger.info("Found command processor: " + command.getClass().getSimpleName());
                    outgoingMessages = command.executeCommand(incomingMessage, serviceProvider);
                    break;
                }
            }
        }
        return outgoingMessages;
    };

    public static List<AbstractCommand> getCommands() {
        List<AbstractCommand> commands = new ArrayList<>();
        commands.add(new HelpCommand());
        commands.add(new KittenCommand());
        commands.add(new DogCommand());
        commands.add(new WeatherCommand());
        commands.add(new OrCommand());
        //commands.add(new TitsCommand());
        return commands;
    };

}
