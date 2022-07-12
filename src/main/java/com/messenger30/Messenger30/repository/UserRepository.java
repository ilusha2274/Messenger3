package com.messenger30.Messenger30.repository;

import com.messenger30.Messenger30.domain.PrintFriend;
import com.messenger30.Messenger30.domain.User;

import java.util.List;


public interface UserRepository {
    List<User> getAllUsers();

    User addUser(User user);

    void removeUserByEmail(String email);

    User findUserByEmail(String email);

    User findUserById(int id);

    void addNewFriend(User user1, User user2);

    List<PrintFriend> findListFriendsByUser(User user);

    boolean isAlreadyFriends(int user1, int user2);

    boolean findUserInChat(User user, int chatID);

    void deleteFriend(int idUser1, int idUser2);

    void makeUser(int userId);

    void makeAdmin(int userId);

    void ban(int userId);

    void unBan(int userId);
}
