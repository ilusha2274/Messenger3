package com.messenger30.Messenger30.repository;

import com.messenger30.Messenger30.exception.PasswordMismatchException;
import com.messenger30.Messenger30.exception.WrongEmailException;
import com.messenger30.Messenger30.exception.WrongLoginPasswordException;
import com.messenger30.Messenger30.helper.PrintFriend;

import java.sql.SQLException;
import java.util.List;


public interface UserRepository {
    User addUser(User user, String twoPassword) throws PasswordMismatchException, WrongEmailException, SQLException;

    void removeUserByEmail(String email);

    boolean findEmailUser(String email) throws WrongEmailException;

    boolean checkPassword(String password, String twoPassword) throws PasswordMismatchException;

    User logInUser(String email, String password) throws WrongLoginPasswordException;

    User findUserByEmail(String email);

    User findUserById(int id);

    void addNewFriends(User user1, User user2);

    List<PrintFriend> findListFriendsByUser(User user);
}
