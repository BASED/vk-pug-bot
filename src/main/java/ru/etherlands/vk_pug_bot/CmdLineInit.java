package ru.etherlands.vk_pug_bot;


import com.vk.api.sdk.client.ApiRequest;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.base.responses.OkResponse;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetResponse;
import com.vk.api.sdk.queries.messages.MessagesGetQuery;
import org.apache.log4j.Logger;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by ssosedkin on 07.11.2016.
 */

@Component
public class CmdLineInit implements CommandLineRunner {


    @Autowired
    RabbitTemplate template;

    Logger logger = Logger.getLogger(QueueConfiguration.class);
    private final static String PROPERTIES_FILE = "config.properties";

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("ololo");
        initServer();
        template.setExchange(Constants.INCOMING_EXCHANGE);
        template.convertAndSend("Fanout message");

    }

    void initServer() {
        try {

            Properties properties = readProperties();

            HttpTransportClient client = new HttpTransportClient();
            VkApiClient apiClient = new VkApiClient(client);

            initVkApi(apiClient, readProperties());

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
                    logger.info("Message: " + msg.getBody());
                }
                if (!readedMessageIds.isEmpty()) {
                    OkResponse okay = apiClient.messages().markAsRead(userActor).messageIds(readedMessageIds).execute();
                    logger.info("set readed result: " + okay.getValue());
                }
            }



        } catch (ApiException e) {
            throw new RuntimeException("Api error during init", e);
        } catch (ClientException e) {
            throw new RuntimeException("Client error during init", e);
        }


    }

    private static Properties readProperties() throws FileNotFoundException {
        InputStream inputStream = CmdLineInit.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (inputStream == null)
            throw new FileNotFoundException("property file '" + PROPERTIES_FILE + "' not found in the classpath");

        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Incorrect properties file");
        }
    }
}