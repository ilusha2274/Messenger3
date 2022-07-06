package com.messenger30.Messenger30.repository;

import com.messenger30.Messenger30.domain.PrintFriend;
import com.messenger30.Messenger30.domain.User;

import java.util.List;


public interface UserRepository {
    User addUser(User user);

    void removeUserByEmail(String email);

    User findUserByEmail(String email);

    User findUserById(int id);

    void addNewFriend(User user1, User user2);

    List<PrintFriend> findListFriendsByUser(User user);

    boolean isAlreadyFriends(int user1, int user2);

    boolean findUserInChat(User user, int chatID);
}
