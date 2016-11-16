package ru.etherlands.vk_pug_bot.commands;

import ru.etherlands.vk_pug_bot.dto.PugMessage;
import ru.etherlands.vk_pug_bot.server.ServiceProvider;

import java.util.List;

/**
 * Created by ssosedkin on 10.11.2016.
 */
public abstract class AbstractCommand {
    public abstract List<String> getCommandWords();
    public abstract List<PugMessage> executeCommand(PugMessage message, ServiceProvider serviceProvider);
    public abstract String getDescription();
}
