package ru.etherlands.vk_pug_bot.server;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.stereotype.Component;
import ru.etherlands.vk_pug_bot.Utils;

import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ssosedkin on 09.11.2016.
 */
@Component
public class ServiceProvider {

    private ReentrantLock lock = new ReentrantLock();
    private HttpTransportClient client = new HttpTransportClient();
    private VkApiClient apiClient = new VkApiClient(client);
    private Properties properties = Utils.readProperties();
    private int userId = Integer.parseInt(properties.getProperty("userId"));
    private int lockDelay = Integer.parseInt(properties.getProperty("lockDelay"));
    private String token = properties.getProperty("token");
    private UserActor userActor = new UserActor(userId, token);

    public void doLock() {
        lock.lock();
    }

    public void doUnLock() {
        try {
            Thread.sleep(lockDelay);
        } catch (InterruptedException e) {

        }
        lock.unlock();
    }

    public HttpTransportClient getClient() {
        return client;
    }

    public VkApiClient getApiClient() {
        return apiClient;
    }

    public UserActor getUserActor() { return userActor; };
}
