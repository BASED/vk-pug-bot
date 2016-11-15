package ru.etherlands.vk_pug_bot.commands;

import org.apache.log4j.Logger;
import ru.etherlands.vk_pug_bot.Constants;
import ru.etherlands.vk_pug_bot.commands.AbstractCommand;
import ru.etherlands.vk_pug_bot.commands.KittenCommand;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ssosedkin on 10.11.2016.
 */
public class CommandsProcessor {
    private static Logger logger = Logger.getLogger(CommandsProcessor.class);

    public static List<PugMessage> getCommandExecution (PugMessage incomingMessage) {
        List<AbstractCommand> commands = getCommands();
        List<PugMessage> outgoingMessages = null;
        String messageBody = incomingMessage.getBody();
        if (messageBody.startsWith(Constants.COMMAND_PREFIX)) {
            String[] messageWords = messageBody.split(" ");
            String commandWord = messageWords[0].replace(Constants.COMMAND_PREFIX, "").toLowerCase();
            logger.info("CommandWord: " + commandWord);
            for(AbstractCommand command : commands) {
                if (command.getCommandWords().contains(commandWord)) {
                    logger.info("Found command processor: " + command.getClass().getSimpleName());
                    outgoingMessages = command.executeCommand(incomingMessage);
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
        return commands;
    };

}
