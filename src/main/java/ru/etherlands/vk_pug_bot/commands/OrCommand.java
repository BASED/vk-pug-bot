package ru.etherlands.vk_pug_bot.commands;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import ru.etherlands.vk_pug_bot.dto.PugMessage;
import ru.etherlands.vk_pug_bot.server.ServiceProvider;

import java.util.*;

/**
 * Created by ssosedkin on 10.11.2016.
 */
public class OrCommand extends AbstractCommand {
    private Logger logger = Logger.getLogger(OrCommand.class);
    private Random random = new Random();
    private final int chanceOfSpecialAnswer = 30;

    private List<String> customMessages = Arrays.asList(
        "Думаю %s",
        "Определенно %s",
        "%s. К бабке не ходи",
        "Не нужно быть семи пядей во лбу, чтобы понять, что %s тут единственно верный вариант",
        "Возможно %s",
        "Я выбираю %s",
        "Пусть будет %s"
    );

    @Override
    public List<String> getCommandWords() {
        return Arrays.asList(new String[]{"or", "dice", "выбери"});
    }

    @Override
    public String getDescription() {
        return "Выбор из предложенных вариантов. Например: !выбери рыба колбаса";
    }

    @Override
    public List<PugMessage> executeCommand(PugMessage incoming, ServiceProvider serviceProvider) {
        List<PugMessage> messages = new ArrayList<PugMessage>();
        PugMessage outgoing = new PugMessage(null);
        processMessage(outgoing, incoming);

        messages.add(outgoing);
        return messages;
    }

    public void processMessage(PugMessage outgoing, PugMessage incoming) {
        String answer = "А что, есть выбор?";
        String query = incoming.getBody();
        String[] variants = query.split(" ");

        List<String> filteredVariants = new ArrayList<>();

        boolean first = true;
        for (String variant : variants) {
            if (first) {
                first = false;
                continue;
            }
            if (!Strings.isNullOrEmpty(variant)) {
                variant = variant.trim();
                if (!"или".equalsIgnoreCase(variant)) {
                    filteredVariants.add(variant);
                }
            }
        }

        logger.info("Or variants: " + filteredVariants);
        if (!filteredVariants.isEmpty()) {
            answer = filteredVariants.get(random.nextInt(100) % filteredVariants.size());
        }

        if (random.nextInt(100) < chanceOfSpecialAnswer) {
            answer = String.format(customMessages.get(random.nextInt(customMessages.size())), answer);
        }
        outgoing.setBody(answer);

    }

}
