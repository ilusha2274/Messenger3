package com.messenger30.Messenger30.helper;

public class PrintMessage {
    private boolean author;
    private String message;
    private String date;
    private String nameAuthor;
    private Integer messageId;

    public PrintMessage(boolean author, String message, String date,String nameAuthor, Integer messageId) {
        this.author = author;
        this.message = message;
        this.date = date;
        this.nameAuthor = nameAuthor;
        this.messageId = messageId;
    }

    public PrintMessage() {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAuthor() {
        return author;
    }

    public void setAuthor(boolean author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
