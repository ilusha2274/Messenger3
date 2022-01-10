package com.messenger30.Messenger30.websocket;

public class ChatMessage {
    private String content;
    private String time;
    private String nameAuthor;
    private Integer idChat;
    private Integer userId;

    public ChatMessage() {
    }

    public ChatMessage(String content, String time,String nameAuthor) {
        this.content = content;
        this.time = time;
        this.nameAuthor = nameAuthor;
    }

    public ChatMessage(String nameAuthor,Integer userId, String content,Integer idChat) {
        this.content = content;
        this.nameAuthor = nameAuthor;
        this.userId = userId;
        this.idChat = idChat;
    }

    public Integer getIdChat() {
        return idChat;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setIdChat(Integer idChat) {
        this.idChat = idChat;
    }

    public ChatMessage(String content) {
        this.content = content;
    }

    public String getNameAuthor() {
        return nameAuthor;
    }

    public void setNameAuthor(String nameAuthor) {
        this.nameAuthor = nameAuthor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}