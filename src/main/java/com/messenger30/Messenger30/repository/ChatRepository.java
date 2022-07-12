package com.messenger30.Messenger30.repository;

import com.messenger30.Messenger30.domain.Chat;
import com.messenger30.Messenger30.domain.Message;
import com.messenger30.Messenger30.domain.User;

import java.util.List;

public interface ChatRepository {
    List<Chat> findListChatByUser(User user);

    Chat findChatById(int id);

    Chat addChat(List<User> users, String chatType);

    Message addMessageToChat(String text, User user, int chatId);

    List<Message> getListMessageByNumberChat(int i);

    Chat addGroupChat(String nameChat, User user);

    Chat findChatByName(String nameChat, User user);

    void addUserToGroupChat(int userId, int chatID);

    boolean findUserInChat(Integer chatID, User user);

    Chat searchChatBetweenUsers(User user1, User user2);

    List<User> findListUserInChat(int chatID);

    List<Message> findFirst30(int chatId);

    List<Message> next30(int chatId, int messageId);

    void uploadFileInMessage(String nameFile, int idMessage);

    void deleteUserInGroupChat(int chatID, int userID);

    void cleanAndDeleteChat(int chatId);

    void cleanChat(int chatId);
}
