package ru.etherlands.vk_pug_bot.commands;

import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import ru.etherlands.vk_pug_bot.Utils;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ssosedkin on 10.11.2016.
 */
public class DogCommand extends AbstractCommand{
    private Logger logger = Logger.getLogger(DogCommand.class);
    private final String DOMAIN_NAME = "http://random.dog";

    @Override
    public List<String> getCommandWords() {
        return Arrays.asList(new String[] {"dog", "puppy", "песик", "песики", "псэ", "собака", "пёсик", "пёсики",});
    }

    @Override
    public String getDescription() {
        return "Случайный собакен с random.dog";
    }

    @Override
    public List<PugMessage> executeCommand(PugMessage message) {
        List<PugMessage> messages = new ArrayList<PugMessage>();
        PugMessage outgoing = new PugMessage(null);
        processMessage(outgoing);

        messages.add(outgoing);
        return messages;
    };

    public void processMessage(PugMessage message) {
        String fileName = null;
        try {
            DOMParser parser = new DOMParser();
            parser.parse(DOMAIN_NAME);
            fileName = getDogFile(parser.getDocument(), DOMAIN_NAME);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (fileName != null && !fileName.isEmpty()) {
            message.getImageFileNames().add(fileName);
        } else {
            message.setBody("Собакен не получен! :(");
        }
    }

    public String getDogFile(Node node, String domain) {
        try {
            if ("img".equalsIgnoreCase(node.getNodeName()) && node.getAttributes() != null) {
                logger.info(node.getClass().getName() + " : " + node.getNodeName());
                if (node.hasAttributes()) {
                        String url = domain + "/" + node.getAttributes().getNamedItem("src").getNodeValue();
                        logger.info("Dog url : " + url);
                        String filePath = Utils.saveURLToFile(url);
                        logger.info("Dog file : " + filePath);
                        return filePath;
                }
            }

            Node child = node.getFirstChild();
            while (child != null) {
                String dogFile = getDogFile(child, domain);
                if (dogFile != null) {
                    return dogFile;
                }
                child = child.getNextSibling();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

}
