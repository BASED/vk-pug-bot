package ru.etherlands.vk_pug_bot.commands;

import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.ArrayList;
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
    public List<PugMessage> executeCommand(PugMessage message) {
        List<PugMessage> messages = new ArrayList<PugMessage>();
        PugMessage outcoming = new PugMessage("здесь будет котик!");
        outcoming.getImageFileNames().add("G:/Kitten.png");
        outcoming.getImageFileNames().add("G:/681791902.jpg");

        messages.add(outcoming);
        return messages;
    };
}
