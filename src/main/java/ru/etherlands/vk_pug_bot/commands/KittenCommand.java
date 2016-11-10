package ru.etherlands.vk_pug_bot.commands;

import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ssosedkin on 10.11.2016.
 */
public class KittenCommand  extends AbstractCommand{

    @Override
    public List<String> getCommandWords() {
        return Arrays.asList(new String[] {"котик", "котики", "котэ", "кошак"});
    }

    @Override
    public PugMessage executeCommand(PugMessage message) {
        return new PugMessage("здесь будет котик!");
    };
}
