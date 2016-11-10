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
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("incomingTemplate")
    RabbitTemplate template;

    Logger logger = Logger.getLogger(QueueConfiguration.class);


    @Override
    public void run(String... strings) throws Exception {
        /*
        template.setExchange(Constants.INCOMING_EXCHANGE);
        template.convertAndSend("Fanout message");
*/
    }


}