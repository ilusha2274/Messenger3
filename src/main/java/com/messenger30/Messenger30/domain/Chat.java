package com.messenger30.Messenger30.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private int chatId;
    private List<Message> messages = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private Message lastMessage;
    private String nameChat;
    private LocalDateTime localDateTimeLastMessage;
    private String dateLastMessage;
    private String textLastMessage;
    private String chatType;

    public Chat() {
    }

    public Chat(String nameChat, int chatId, LocalDateTime localDateTimeLastMessage, String textLastMessage) {
        this.nameChat = nameChat;
        this.chatId = chatId;
        this.localDateTimeLastMessage = localDateTimeLastMessage;
        this.textLastMessage = textLastMessage;
    }

    public Chat(int chatId, String nameChat, String chatType) {
        this.chatId = chatId;
        this.nameChat = nameChat;
        this.chatType = chatType;
    }


    public LocalDateTime getLocalDateTimeLastMessage() {
        return localDateTimeLastMessage;
    }

    public void setLocalDateTimeLastMessage(LocalDateTime localDateTimeLastMessage) {
        this.localDateTimeLastMessage = localDateTimeLastMessage;
    }

    public String getDateLastMessage() {
        return dateLastMessage;
    }

    public void setDateLastMessage(String dateLastMessage) {
        this.dateLastMessage = dateLastMessage;
    }

    public String getTextLastMessage() {
        return textLastMessage;
    }

    public void setTextLastMessage(String textLastMessage) {
        this.textLastMessage = textLastMessage;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessage(Message message) {
        messages.add(message);
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public Message getMessageByNumber(int i) {
        return messages.get(i);
    }

    public String getNameChat() {
        return nameChat;
    }

    public void setNameChat(String nameChat) {
        this.nameChat = nameChat;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }
}
