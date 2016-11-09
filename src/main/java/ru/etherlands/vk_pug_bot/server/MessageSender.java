package ru.etherlands.vk_pug_bot.server;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.base.responses.OkResponse;
import com.vk.api.sdk.objects.messages.responses.GetResponse;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.etherlands.vk_pug_bot.Constants;
import ru.etherlands.vk_pug_bot.QueueConfiguration;
import ru.etherlands.vk_pug_bot.Utils;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Created by ssosedkin on 09.11.2016.
 */
@Component
public class MessageSender {
    Logger logger = Logger.getLogger(QueueConfiguration.class);
    private final Random random = new Random();

    @Autowired
    RabbitTemplate template;

    @Autowired
    ServiceProvider provider;

    @RabbitListener(queues = Constants.OUTCOMING_QUEUE)
    void runServerInteraction(Message message) {
        try {
            provider.getLock().lock();
            sendMessage(message, provider.getApiClient(), Utils.readProperties());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            provider.getLock().unlock();
        }
    }

    private void sendMessage(Message message, VkApiClient apiClient, Properties properties) {
        int userId = Integer.parseInt(properties.getProperty("userId"));
        String token = properties.getProperty("token");
        UserActor userActor = new UserActor(userId, token);

        try {
            PugMessage pugMessage = (PugMessage) template.getMessageConverter().fromMessage(message);
            logger.info("Message send: " + pugMessage);
            MessagesSendQuery messageQuery = apiClient.messages().send(userActor).message(pugMessage.getBody());
            if (pugMessage.getChatId() == null) {
                messageQuery = messageQuery.userId(pugMessage.getUserId());
            } else {
                messageQuery = messageQuery.chatId(pugMessage.getChatId());
            }
            String result = messageQuery.randomId(random.nextInt()).executeAsString();
            logger.info("send result: " + result);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
