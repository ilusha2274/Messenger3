package com.messenger30.Messenger30.repository;

import com.messenger30.Messenger30.exception.PasswordMismatchException;
import com.messenger30.Messenger30.exception.WrongEmailException;
import com.messenger30.Messenger30.exception.WrongLoginPasswordException;
import com.messenger30.Messenger30.helper.PrintFriend;
import com.messenger30.Messenger30.helper.PrintFriendMapper;
import com.messenger30.Messenger30.helper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

public class DatabaseUserRepository implements UserRepository, UserDetailsService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public DatabaseUserRepository(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = transactionTemplate;
    }

    public List<User> index() {
        return jdbcTemplate.query("SELECT * FROM users", new UserMapper());
    }

    //Перестало выводится сообщение об ошибке из-за блока try catch
    @Override
    public User addUser(User user, String twoPassword) throws PasswordMismatchException, WrongEmailException {

        transactionTemplate.execute(status -> {

            String password = passwordEncoder.encode(user.getPassword());

            int id = jdbcTemplate.queryForObject("INSERT INTO users (user_name,user_email,user_password,enabled) VALUES(?,?,?,?) RETURNING user_id",
                    Integer.class,
                    user.getName(), user.getEmail(), password, user.isEnabled());

            jdbcTemplate.update("INSERT INTO authorities (authority,user_id) VALUES(?,?)", "USER", id);

            user.setId(id);

            return user;
        });
        return user;
    }

    @Override
    public void removeUserByEmail(String email) {
        jdbcTemplate.update("DELETE FROM users WHERE user_email=?", email);
    }

    @Override
    public boolean findEmailUser(String email) throws WrongEmailException {

        if (findUserByEmail(email) != null) {
            throw new WrongEmailException("email занят");
        }
        return false;

    }

    @Override
    public boolean checkPassword(String password, String twoPassword) throws PasswordMismatchException {
        if (password.equals(twoPassword)) {
            return true;
        } else {
            throw new PasswordMismatchException("Пароли не совпадают");
        }
    }

    @Override
    public User logInUser(String email, String password) throws WrongLoginPasswordException {
        User user = findUserByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new WrongLoginPasswordException("Неверное имя пользователя или пароль");
        }
    }

    @Override
    public User findUserByEmail(String email) {

        return jdbcTemplate.query("SELECT * FROM users WHERE user_email=?", new Object[]{email},
                new UserMapper()).stream().findAny().orElse(null);
    }

    @Override
    public User findUserById(int id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE user_id=?", new Object[]{id},
                new UserMapper()).stream().findAny().orElse(null);
    }

    @Override
    public User loadUserByUsername(String s) throws UsernameNotFoundException {
        return findUserByEmail(s);
    }

    @Override
    public void addNewFriends(User user1, User user2) {

        jdbcTemplate.update("INSERT INTO users_users (user1_id, user2_id) VALUES(?,?)",
                user1.getId(), user2.getId());
    }

    @Override
    public List<PrintFriend> findListFriendsByUser(User user) {
        return jdbcTemplate.query(" select u.user_name, uc.chat_id, u.user_id " +
                        " from users u  " +
                        " join users_users uu  " +
                        " on u.user_id = uu.user2_id " +
                        " join users_chats uc " +
                        " on uu.user1_id=uc.user_id " +
                        " join chats " +
                        " on uc.chat_id =  chats.chat_id " +
                        " and chats.chat_type= 'private' " +
                        " join users_chats uc2 " +
                        " on uu.user2_id = uc2.user_id and uc.chat_id=uc2.chat_id " +
                        " where uu.user1_id = ? ",
                new PrintFriendMapper(), user.getId());
    }
}
