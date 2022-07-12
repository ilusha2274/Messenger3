package com.messenger30.Messenger30.repository;

import com.messenger30.Messenger30.domain.PrintFriend;
import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.helpers.PrintFriendMapper;
import com.messenger30.Messenger30.helpers.UserMapper;
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

//    public List<User> index() {
//        return jdbcTemplate.query("SELECT * FROM users", new UserMapper());
//    }


    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users " +
                " JOIN role " +
                " ON users.user_id = role.user_id ", new UserMapper());
    }

    @Override
    public User addUser(User user) {

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
    public User findUserByEmail(String email) {

        return jdbcTemplate.query("SELECT * FROM users" +
                        " JOIN role " +
                        " ON role.user_id = users.user_id " +
                        " WHERE user_email=?", new Object[]{email},
                new UserMapper()).stream().findAny().orElse(null);
    }

    @Override
    public User findUserById(int id) {

        return jdbcTemplate.query("SELECT * FROM users " +
                        " JOIN role " +
                        " ON role.user_id = users.user_id " +
                        " WHERE users.user_id=?", new Object[]{id},
                new UserMapper()).stream().findAny().orElse(null);
    }

    @Override
    public User loadUserByUsername(String s) throws UsernameNotFoundException {
        return findUserByEmail(s);
    }

    @Override
    public void addNewFriend(User user1, User user2) {
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

    @Override
    public boolean isAlreadyFriends(int user1, int user2) {
        int res = jdbcTemplate.queryForObject("SELECT count(*) FROM users_users WHERE user1_id = ? AND user2_id = ?", Integer.class, user1, user2);

        return res > 0;
    }

    @Override
    public boolean findUserInChat(User user, int chatID) {

        int res = jdbcTemplate.queryForObject("SELECT count(*) FROM users_chats " +
                " WHERE user_id = ? " +
                " AND chat_id = ? ", Integer.class, user.getId(), chatID);

        return res > 0;
    }

    @Override
    public void deleteFriend(int idUser1, int idUser2) {
        jdbcTemplate.update("DELETE FROM users_users " +
                " WHERE user1_id = ? " +
                " AND user2_id = ?", idUser1, idUser2);
    }

    @Override
    public void makeUser(int userId) {
        jdbcTemplate.update("UPDATE role SET role = 'ROLE_USER' WHERE user_id = ?", userId);
    }

    @Override
    public void makeAdmin(int userId) {
        jdbcTemplate.update("UPDATE role SET role = 'ROLE_ADMIN' WHERE user_id = ?", userId);
    }

    @Override
    public void ban(int userId) {
        jdbcTemplate.update("UPDATE users SET enabled = false WHERE user_id = ?", userId);
    }

    @Override
    public void unBan(int userId) {
        jdbcTemplate.update("UPDATE users SET enabled = true WHERE user_id = ?", userId);
    }
}
