package ru.etherlands.vk_pug_bot.server;

import com.vk.api.sdk.client.ApiRequest;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoUpload;
import com.vk.api.sdk.objects.photos.responses.MessageUploadResponse;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.etherlands.vk_pug_bot.Constants;
import ru.etherlands.vk_pug_bot.QueueConfiguration;
import ru.etherlands.vk_pug_bot.Utils;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.io.File;
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
    @Qualifier("outgoingTemplate")
    RabbitTemplate template;

    @Autowired
    ServiceProvider provider;

    @RabbitListener(queues = Constants.OUTGOING_QUEUE)
    void runServerInteraction(Message message) {
        try {
            provider.doLock();
            sendMessage(message, provider.getApiClient(), provider.getUserActor());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            provider.doUnLock();
        }
    }

    private void sendMessage(Message message, VkApiClient apiClient, UserActor userActor) {
        try {
            PugMessage pugMessage = (PugMessage) template.getMessageConverter().fromMessage(message);
            logger.info("Message send: " + pugMessage);
            MessagesSendQuery messageQuery = apiClient.messages().send(userActor).message(pugMessage.getBody());
            if (pugMessage.getChatId() == null) {
                messageQuery = messageQuery.userId(pugMessage.getUserId());
            } else {
                messageQuery = messageQuery.chatId(pugMessage.getChatId());
            }

            addPhotosToMessagesQuery(apiClient, userActor, messageQuery, pugMessage);
            String result = messageQuery.randomId(random.nextInt()).executeAsString();
            logger.info("send result: " + result);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private MessagesSendQuery addPhotosToMessagesQuery(VkApiClient apiClient, UserActor userActor, MessagesSendQuery messagesSendQuery, PugMessage message) throws ClientException, ApiException {
        if (message.getImageFileNames() == null || message.getImageFileNames().isEmpty()) {
            return messagesSendQuery;
        }
        List<Photo> allPhotos = new ArrayList<>();
        for (String imageFileName : message.getImageFileNames()) {
            List<Photo> photos = getPhotosForImageFileName(apiClient, userActor, imageFileName);
            for (Photo vkPhoto : photos) {
                logger.info("Photo: " + vkPhoto.toString());
            }
            allPhotos.addAll(photos);
        }
        if (allPhotos.isEmpty()) {
            return messagesSendQuery;
        }
        return messagesSendQuery.attachment(getAttachmentsForPhotos(allPhotos));
    }

    private List<Photo> getPhotosForImageFileName(VkApiClient apiClient, UserActor userActor, String imageFileName) throws ApiException, ClientException {
        PhotoUpload upload = apiClient.photos().getMessagesUploadServer(userActor).execute();
        String uploadUrl = upload.getUploadUrl();
        Integer albumId = upload.getAlbumId();
        Integer userId = upload.getUserId();

        MessageUploadResponse response = apiClient.upload().photoMessage(uploadUrl, new File(imageFileName)).execute();
        String photo = response.getPhoto();
        String hash = response.getHash();
        Integer server = response.getServer();

        return apiClient.photos().saveMessagesPhoto(userActor, photo).hash(hash).server(server).execute();
    }

    private List<String> getAttachmentsForPhotos(List<Photo> photos) {
        List<String> attachmentStrings = new ArrayList<>();
        for (Photo photo: photos) {
            String attachment = "photo" + photo.getOwnerId() + "_" + photo.getId();
            attachmentStrings.add(attachment);
        }
        return attachmentStrings;
    }
}
