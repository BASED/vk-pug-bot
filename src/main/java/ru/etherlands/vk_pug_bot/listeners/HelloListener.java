package ru.etherlands.vk_pug_bot.listeners;

/**
 * Created by ssosedkin on 07.11.2016.
 */
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.etherlands.vk_pug_bot.Constants;

import java.util.Random;

@Component
public class HelloListener {
    Logger logger = Logger.getLogger(HelloListener.class);
    Random random = new Random();

    @RabbitListener(queues = Constants.INCOMING_HELLO_QUEUE)
    public void worker1(String message) {
        logger.info("accepted on worker 1 : " + message);
    }

}
