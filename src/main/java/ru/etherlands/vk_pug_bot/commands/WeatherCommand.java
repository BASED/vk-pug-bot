package ru.etherlands.vk_pug_bot.commands;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.Logger;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import ru.etherlands.vk_pug_bot.Utils;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.*;

/**
 * Created by ssosedkin on 10.11.2016.
 */
public class WeatherCommand extends AbstractCommand {
    private Logger logger = Logger.getLogger(WeatherCommand.class);
    private final String DOMAIN_NAME = "http://meteoinfo.ru/rss/forecasts/28722";

    @Override
    public List<String> getCommandWords() {
        return Arrays.asList(new String[]{"weather", "погода"});
    }

    @Override
    public List<PugMessage> executeCommand(PugMessage message) {
        List<PugMessage> messages = new ArrayList<PugMessage>();
        PugMessage outcoming = new PugMessage(null);
        processMessage(outcoming);

        messages.add(outcoming);
        return messages;
    }

    public void processMessage(PugMessage message) {
        List<String> weatherData = null;
        try {
            DOMParser parser = new DOMParser();
           // parser.setProperty("http://cyberneko.org/html/properties/default-encoding", "utf-8");
            parser.parse(DOMAIN_NAME);
            weatherData = getWeatherData(parser.getDocument(), parser.getDocument(), DOMAIN_NAME);
            weatherData = filterWeatherData(weatherData);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (weatherData != null && !weatherData.isEmpty() && !Strings.isNullOrEmpty(weatherData.get(0))) {
            message.setBody(Joiner.on("\n").join(weatherData));
        } else {
            message.setBody("Погода не получена! :(");
        }
    }

    public List<String> getWeatherData(Node node, Node parent, String domain) {
        try {
            List<String> weatherData = new ArrayList<>();
            if ("title".equalsIgnoreCase(node.getNodeName()) && "item".equalsIgnoreCase(parent.getNodeName())) {
                logger.info(node.getClass().getName() + " : " + node.getNodeName() + " -- " + node.getTextContent());
                if (!Strings.isNullOrEmpty(node.getTextContent())) {
                        weatherData.add(node.getTextContent());
                }
            }

            if ("description".equalsIgnoreCase(node.getNodeName()) && "item".equalsIgnoreCase(parent.getNodeName())) {
                logger.info(node.getClass().getName() + " : " + node.getNodeName() + " -- " + node.getTextContent());
                if (!Strings.isNullOrEmpty(node.getTextContent())) {
                    weatherData.add(node.getTextContent());
                    weatherData.add("");
                }
            }

            Node child = node.getFirstChild();
            while (child != null) {
                List<String> childData = getWeatherData(child, node, domain);
                if (childData != null) {
                    weatherData.addAll(childData);
                }
                child = child.getNextSibling();
            }
            return weatherData;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public List<String> filterWeatherData(List<String> weatherData) {
        if (weatherData == null || weatherData.isEmpty() || weatherData.get(0) == null) {
            return weatherData;
        }
        String firstItem = weatherData.get(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String yesterday = " " + calendar.get(Calendar.DAY_OF_MONTH) + " ";
        if (firstItem.contains(yesterday) && weatherData.size() > 3) {
            return weatherData.subList(3, weatherData.size() - 1);
        }
        return weatherData;
    }
}
