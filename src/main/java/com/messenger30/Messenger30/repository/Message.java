package com.messenger30.Messenger30.repository;

import java.time.LocalDateTime;

public class Message {
    private String text;
    private LocalDateTime localDateTime;
    private User author;
    private Integer idAuthor;
    private String nameAuthor;
    private Integer messageId;
    private String nameFile;

    public Message(User user, String text, LocalDateTime localDateTime) {
        this.text = text;
        author = user;
        this.localDateTime = localDateTime;
    }

    public Message(String text, LocalDateTime localDateTime) {
        this.text = text;
        this.localDateTime = localDateTime;
    }

    public Message(Integer idAuthor,String text,LocalDateTime localDateTime, String nameAuthor, Integer messageId, String nameFile){
        this.idAuthor = idAuthor;
        this.text = text;
        this.localDateTime = localDateTime;
        this.nameAuthor = nameAuthor;
        this.messageId = messageId;
        this.nameFile = nameFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getNameAuthor() {
        return nameAuthor;
    }

    public void setNameAuthor(String nameAuthor) {
        this.nameAuthor = nameAuthor;
    }

    public Integer getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(Integer idAuthor) {
        this.idAuthor = idAuthor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
