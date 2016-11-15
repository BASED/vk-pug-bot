package ru.etherlands.vk_pug_bot.commands;

import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by ssosedkin on 10.11.2016.
 */
public class HelpCommand extends AbstractCommand {
    private Logger logger = Logger.getLogger(HelpCommand.class);

    @Override
    public List<String> getCommandWords() {
        return Arrays.asList(new String[]{"help", "хелп", "помощь", "справка"});
    }

    @Override
    public String getDescription() {
        return "Справка по командам";
    }

    @Override
    public List<PugMessage> executeCommand(PugMessage incoming) {
        List<PugMessage> messages = new ArrayList<PugMessage>();
        PugMessage outgoing = new PugMessage(null);
        processMessage(outgoing);

        messages.add(outgoing);
        return messages;
    }

    public void processMessage(PugMessage outgoing) {
        StringBuilder helpMessage = new StringBuilder();

        for (AbstractCommand command : CommandsProcessor.getCommands()) {
            helpMessage.append(command.getDescription());
            helpMessage.append("\n");
            boolean first = true;
            for (String commandWord : command.getCommandWords()) {
                if (first) {
                    first = false;
                } else {
                    helpMessage.append(", ");
                }
                helpMessage.append("!");
                helpMessage.append(commandWord);

            }
            helpMessage.append("\n");
            helpMessage.append("\n");
        }
        outgoing.setBody(helpMessage.toString());
    }

}
