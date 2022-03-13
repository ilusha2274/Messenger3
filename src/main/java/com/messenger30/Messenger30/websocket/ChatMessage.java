package com.messenger30.Messenger30.websocket;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public class ChatMessage {
    private boolean haveFile;
    private String content;
    private String time;
    private String nameAuthor;
    private Integer idChat;
    private Integer userId;
    private String nameFile;

    public ChatMessage() {
    }

    public ChatMessage(String content, String time,String nameAuthor) {
        this.content = content;
        this.time = time;
        this.nameAuthor = nameAuthor;
    }

    public ChatMessage(String nameAuthor,Integer userId, String content,Integer idChat,boolean haveFile) {
        this.content = content;
        this.nameAuthor = nameAuthor;
        this.userId = userId;
        this.idChat = idChat;
        this.haveFile = haveFile;
    }

    public ChatMessage(String nameAuthor,Integer userId, String content,Integer idChat,boolean haveFile,String nameFile) {
        this.content = content;
        this.nameAuthor = nameAuthor;
        this.userId = userId;
        this.idChat = idChat;
        this.haveFile = haveFile;
        this.nameFile = nameFile;
    }

//    public ChatMessage(String nameAuthor,Integer userId, String content,Integer idChat,
//                       boolean haveFile,File file) {
//        this.content = content;
//        this.nameAuthor = nameAuthor;
//        this.userId = userId;
//        this.idChat = idChat;
//        this.haveFile = haveFile;
//        this.file = file;
//    }

//    public File getFile() {
//        return file;
//    }
//
//    public void setFile(File file) {
//        this.file = file;
//    }


    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public boolean isHaveFile() {
        return haveFile;
    }

    public void setHaveFile(boolean haveFile) {
        this.haveFile = haveFile;
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