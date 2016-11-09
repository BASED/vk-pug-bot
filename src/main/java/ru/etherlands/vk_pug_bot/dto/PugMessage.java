package ru.etherlands.vk_pug_bot.dto;

import java.io.Serializable;

/**
 * Created by ssosedkin on 09.11.2016.
 */
public class PugMessage implements Serializable{
    private Integer id;
    private Integer date;
    private Integer userId;
    private Integer randomId;
    private String title;
    private String body;
    private Integer chatId;
    private Integer adminId;

    public PugMessage(Integer id, Integer date, Integer userId, Integer randomId, String title, String body, Integer chatId, Integer adminId) {
        this.id = id;
        this.date = date;
        this.userId = userId;
        this.randomId = randomId;
        this.title = title;
        this.body = body;
        this.chatId = chatId;
        this.adminId = adminId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRandomId() {
        return randomId;
    }

    public void setRandomId(Integer randomId) {
        this.randomId = randomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PugMessage{");
        sb.append("id=").append(this.id);
        sb.append(", date=").append(this.date);
        sb.append(", userId=").append(this.userId);
        sb.append(", randomId=").append(this.randomId);
        sb.append(", title=\'").append(this.title).append("\'");
        sb.append(", body=\'").append(this.body).append("\'");
        sb.append(", chatId=").append(this.chatId);
        sb.append(", adminId=").append(this.adminId);

        sb.append('}');
        return sb.toString();
    }

}
