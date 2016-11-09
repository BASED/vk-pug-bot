package ru.etherlands.vk_pug_bot.server;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ssosedkin on 09.11.2016.
 */
@Component
public class ServiceProvider {

    private ReentrantLock lock = new ReentrantLock();
    private HttpTransportClient client = new HttpTransportClient();
    private VkApiClient apiClient = new VkApiClient(client);



    public ReentrantLock getLock() {
        return lock;
    }

    public HttpTransportClient getClient() {
        return client;
    }

    public VkApiClient getApiClient() {
        return apiClient;
    }
}
