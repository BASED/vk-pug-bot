package ru.etherlands.vk_pug_bot;

import com.vk.api.sdk.objects.messages.Message;
import org.apache.commons.io.FileUtils;
import ru.etherlands.vk_pug_bot.dto.PugMessage;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * Created by ssosedkin on 09.11.2016.
 */
public class Utils {
    public static Properties readProperties() {
        InputStream inputStream = CmdLineInit.class.getClassLoader().getResourceAsStream(Constants.PROPERTIES_FILE);
        try {
            if (inputStream == null) {
                throw new FileNotFoundException("property file '" + Constants.PROPERTIES_FILE + "' not found in the classpath");
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            System.out.println("Incorrect properties file: " + e.getMessage());
        }
        return null;
    }

    public static PugMessage getPugMessageFromMessage(Message message) {
        return new PugMessage(message.getId(), message.getDate(), message.getUserId(), message.getRandomId(), message.getTitle(), message.getBody(), message.getChatId(), message.getAdminId());
    }

    public static byte[] getBytesFromObject(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();

        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static Object getObjectFromBytes(byte [] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static String saveURLToFile(String urlPath) throws IOException {
        URL url = new URL(urlPath);
        String[] urlParts = urlPath.split("/");
        String fileName = makeValidFileName(urlParts[urlParts.length - 1], null);
        if (!fileName.contains(".")) {
            fileName = fileName + ".jpg";
        }
        File file = new File("temp/" + fileName);
        FileUtils.copyURLToFile(url, file);
        return file.getAbsolutePath();
    }

    public static String makeValidFileName(String name, Character replaceChar) {
        if (replaceChar == null) {
            replaceChar = '_';
        }
        return name.replaceAll("[\\\\/:*?\"<>=|\\x00-\\x1F]", replaceChar.toString());
    }

}
