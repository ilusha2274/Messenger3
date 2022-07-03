package com.messenger30.Messenger30.repository;

import com.messenger30.Messenger30.domain.Chat;
import com.messenger30.Messenger30.domain.PrintFriend;
import com.messenger30.Messenger30.domain.PrintMessage;
import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.domain.ChatMessage;

import java.util.List;

public interface IMessengerService {

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
}
