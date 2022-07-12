package com.messenger30.Messenger30.services;

import com.messenger30.Messenger30.domain.*;

import java.util.List;

public interface IMessengerService {

    List<User> getAllUsers();

    User findUserByEmail(String email);

    void addNewFriend(User user, String emailUser2);

    List<PrintFriend> findListFriendsByUser(User user);

    void registerUser(User user, String twoPassword);

    List<Chat> findListChatByUser(User user);

    boolean isUserInChat(int chatId, User user);

    List<PrintMessage> returnFirst20Messages(int chatId, User user);

    List<PrintMessage> returnNext20Messages(int chatID, int messageId, User user);

    void addMessageToChat(ChatMessage chatMessage, User user);

    List<User> findListUserInChat(int chatId);

    void addGroupChat(List<Integer> idFriends, User user, String nameChat);

    Chat findChatById(User user, int id);

    void addUserInGroupChat(String emailUser, int chatId);

    void deleteUserInGroupChat(int chatID, int userId);

    void deleteChat(int chatId);

    void deleteFriend(int idUser1, int idUser2);

    void makeUser(int userId);

    void makeAdmin(int userId);

    void ban(int userId);

    void unBan(int userId);
}
