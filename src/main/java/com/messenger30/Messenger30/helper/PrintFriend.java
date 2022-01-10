package com.messenger30.Messenger30.helper;

public class PrintFriend {

    private Integer chatId;
    private String nameFriend;
    private Integer idUser2;

    public PrintFriend(Integer chatId, String nameFriend, Integer idUser2) {
        this.chatId = chatId;
        this.nameFriend = nameFriend;
        this.idUser2 = idUser2;
    }

    public Integer getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(Integer idUser2) {
        this.idUser2 = idUser2;
    }

    public Integer getChatId() {
        return chatId;
    }

    public void setChatId(Integer chatId) {
        this.chatId = chatId;
    }

    public String getNameFriend() {
        return nameFriend;
    }

    public void setNameFriend(String nameFriend) {
        this.nameFriend = nameFriend;
    }
}
