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

/**
 * Created by ssosedkin on 10.11.2016.
 */
public class TitsCommand extends AbstractCommand{
    private Logger logger = org.slf4j.LoggerFactory.getLogger(TitsCommand.class);

    private final String DOMAIN_URL = "https://ssl-proxy.my-addr.org";
    private final String SOURCE_URL = DOMAIN_URL + "/myaddrproxy.php/https/tits-guru.com/randomTits";
    @Override
    public List<String> getCommandWords() {
        return Arrays.asList(new String[] {"tits", "сиськи"});
    }

    @Override
    public String getDescription() {
        return "Случайные сиськи!";
    }

    @Override
    public List<PugMessage> executeCommand(PugMessage message, ServiceProvider serviceProvider) {
        if (message.getChatId() == null)
            return null;
        if (message.getChatId() != 1 && message.getChatId() != 2)
            return null;

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
            parser.parse(SOURCE_URL);
            fileName = getTitsFile(parser.getDocument(), DOMAIN_URL);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (fileName != null && !fileName.isEmpty()) {
            message.getImageFileNames().add(fileName);
        } else {
            message.setBody("Ты не бойся посмотреть, все равно там сисек нет! :(");
        }
    }

    public String getTitsFile(Node node, String domain) {
        try {
            if ("a".equalsIgnoreCase(node.getNodeName()) && node.getAttributes() != null) {
                try {
                    logger.info("Data image url: " + node.getAttributes().getNamedItem("data-image-url").getNodeValue());
                    String titsPath = node.getAttributes().getNamedItem("href").getNodeValue();
                    String titsUrl = domain + titsPath;
                    logger.info("Tits url: " + titsUrl);
                    String filePath = Utils.saveURLToFile(titsUrl);
                    logger.info("Tits path: " + filePath);
                    return filePath;
                } catch (NullPointerException ex) {}
            }

            Node child = node.getFirstChild();
            while (child != null) {
                String titsFile = getTitsFile(child, domain);
                if (titsFile != null) {
                    return titsFile;
                }
                child = child.getNextSibling();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

}
