package com.messenger30.Messenger30.repository;

import java.util.List;

public interface ChatRepository {
    List<Chat> findListChatByUser(User user);

    Chat getByNumberChat(int i);

    Chat addChat(List<User> users, String chatType);

    Message addMessageToChat(String text, User user, int chatId);

    List<Message> getListMessageByNumberChat(int i);

    Chat addGroupChat(String nameChat, String chatType, User user);

    Chat findChatByName(String nameChat, User user);

    void addUserToGroupChat(User user, Chat chat);

    boolean findUserInChat(Integer chatID, User user);

    Chat searchChatBetweenUsers(User user1, User user2);

    List<User> findListUserInChat (int chatID);

    List<Message> findFirst20(int chatId);

    List<Message> next20(int chatId, int messageId);

    void uploadFileInMessage (String nameFile, int idMessage);
}
