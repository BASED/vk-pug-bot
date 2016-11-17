package ru.etherlands.vk_pug_bot.server;

import com.google.common.collect.Lists;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.base.responses.OkResponse;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetResponse;
import com.vk.api.sdk.queries.messages.MessagesGetQuery;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.etherlands.vk_pug_bot.Constants;
import ru.etherlands.vk_pug_bot.QueueConfiguration;
import ru.etherlands.vk_pug_bot.Utils;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by ssosedkin on 09.11.2016.
 */
@Component
public class MessageReceiver {
    private Logger logger = org.slf4j.LoggerFactory.getLogger(MessageReceiver.class);
    private Integer lastMessageId = 0;
    private Integer maxMessages = Integer.parseInt(Utils.readProperties().getProperty("maxMessages"));

    @Autowired
    @Qualifier("incomingTemplate")
    RabbitTemplate template;

    @Autowired
    ServiceProvider provider;

    @Scheduled(fixedDelay=900)
    void runServerInteraction() {
        try {
            provider.doLock();
            receiveMessages(provider.getApiClient(), provider.getUserActor());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            provider.doUnLock();
        }
    }

    private void receiveMessages(VkApiClient apiClient, UserActor userActor) {
        try {
            MessagesGetQuery query = apiClient.messages().get(userActor).count(maxMessages);
            if (lastMessageId > 0) {
                query = query.lastMessageId(lastMessageId);
            }

            List<PugMessage> receivedMessages = new ArrayList<>();
            List<Integer> readedMessageIds = new ArrayList<Integer>();

            GetResponse response = query.execute();
            for (Message msg : response.getItems()) {

                if (msg.getId() > lastMessageId) {
                    lastMessageId = msg.getId();
                }

                PugMessage pugMessage = Utils.getPugMessageFromMessage(msg);
                logger.info("Message: " + pugMessage);

                if (!msg.isReadState()) {
                    readedMessageIds.add(msg.getId());
                    receivedMessages.add(pugMessage);
                }
            }
            if (!readedMessageIds.isEmpty()) {
                OkResponse okay = apiClient.messages().markAsRead(userActor).messageIds(readedMessageIds).execute();
                logger.info("set readed result: " + okay.getValue());
            }

            receivedMessages = Lists.reverse(receivedMessages);
            for (PugMessage message : receivedMessages) {
                logger.info("Message to queue: " + message);
                template.convertAndSend(message);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
