package ru.etherlands.vk_pug_bot.commands;


import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.w3c.dom.Node;
import ru.etherlands.vk_pug_bot.Utils;
import ru.etherlands.vk_pug_bot.dto.PugMessage;
import ru.etherlands.vk_pug_bot.server.ServiceProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ssosedkin on 10.11.2016.
 */
public class KittenCommand extends AbstractCommand {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(KittenCommand.class);
    private final String DOMAIN_NAME = "http://random.cat";
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public List<String> getCommandWords() {
        return Arrays.asList(new String[]{"cat", "kitten", "котик", "котики", "котэ", "кошак"});
    }

    @Override
    public String getDescription() {
        return "Случайный котэ с http://random.cat";
    }

    @Override
    public List<PugMessage> executeCommand(PugMessage message, ServiceProvider serviceProvider) {
        List<PugMessage> messages = new ArrayList<PugMessage>();
        PugMessage outcoming = new PugMessage(null);
        processMessage(outcoming);

        messages.add(outcoming);
        return messages;
    }

    ;

    public void processMessage(PugMessage message) {
        String fileName = null;
        try {
            lock.lock();
            DOMParser parser = new DOMParser();
            parser.parse(DOMAIN_NAME);
            fileName = getCatFile(parser.getDocument(), DOMAIN_NAME);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            lock.unlock();
        }
        if (fileName != null && !fileName.isEmpty()) {
            message.getImageFileNames().add(fileName);
        } else {
            message.setBody("Котэ не получен! :(");
        }
    }

    public String getCatFile(Node node, String domain) {
        try {
            if ("img".equalsIgnoreCase(node.getNodeName()) && node.getAttributes() != null) {
                if (node.hasAttributes()) {
                    try {
                        if ("cat".equalsIgnoreCase(node.getAttributes().getNamedItem("id").getNodeValue())) {
                            String url = domain + "/" + node.getAttributes().getNamedItem("src").getNodeValue();
                            logger.info("Cat url : " + url);
                            String filePath = Utils.saveURLToFile(url);
                            logger.info("Cat file : " + filePath);
                            return filePath;
                        }
                    } catch (NullPointerException ex) {
                    }
                }
            }

            Node child = node.getFirstChild();
            while (child != null) {
                String catFile = getCatFile(child, domain);
                if (catFile != null) {
                    return catFile;
                }
                child = child.getNextSibling();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

}
