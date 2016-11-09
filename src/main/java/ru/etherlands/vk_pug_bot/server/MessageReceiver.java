package ru.etherlands.vk_pug_bot.server;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.base.responses.OkResponse;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetResponse;
import org.apache.log4j.Logger;
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

/**
 * Created by ssosedkin on 09.11.2016.
 */
@Component
public class MessageReceiver {
    Logger logger = Logger.getLogger(QueueConfiguration.class);

    @Autowired
    RabbitTemplate template;

    @Scheduled(fixedDelay=1000)
    void initServer() {
        try {


            HttpTransportClient client = new HttpTransportClient();
            VkApiClient apiClient = new VkApiClient(client);

            initVkApi(apiClient, Utils.readProperties());

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void initVkApi(VkApiClient apiClient, Properties properties) {

        int userId = Integer.parseInt(properties.getProperty("userId"));
        String token = properties.getProperty("token");
        //if (groupId == 0 || token == null) throw new RuntimeException("Params are not set");

        UserActor userActor = new UserActor(userId, token);

        try {
            logger.info("Message get");
            System.out.println("Message get");
            GetResponse response = apiClient.messages().get(userActor).count(10).execute();
            for (Message msg: response.getItems()) {

                List<Integer> readedMessageIds = new ArrayList<Integer>();

                if (!msg.isReadState()) {
                    readedMessageIds.add(msg.getId());
                    PugMessage pugMessage = Utils.getPugMessageFromMessage(msg);
                    logger.info("Message: " + pugMessage);

                    template.setExchange(Constants.INCOMING_EXCHANGE);

                    template.convertAndSend(pugMessage);

                }
                if (!readedMessageIds.isEmpty()) {
                    OkResponse okay = apiClient.messages().markAsRead(userActor).messageIds(readedMessageIds).execute();
                    logger.info("set readed result: " + okay.getValue());
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


    }

}
