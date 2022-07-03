package com.messenger30.Messenger30.repository;

import com.messenger30.Messenger30.domain.Chat;
import com.messenger30.Messenger30.domain.Message;
import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.helper.ChatMapper;
import com.messenger30.Messenger30.helper.MessageMapper;
import com.messenger30.Messenger30.helper.PrintChatMapper;
import com.messenger30.Messenger30.helper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

public class DatabaseChatRepository implements ChatRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public DatabaseChatRepository(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public List<Chat> findListChatByUser(User user) {
        return jdbcTemplate.query("SELECT chats.chat_id, messages.text_message, messages.date_message, users.user_name AS chatname " +
                        " FROM chats " +
                        " LEFT JOIN messages " +
                        " ON chats.chat_last_message = messages.message_id " +
                        " JOIN users_chats uc1 " +
                        " ON chats.chat_id = uc1.chat_id " +
                        " JOIN users_chats uc2 " +
                        " ON chats.chat_id = uc2.chat_id " +
                        " AND uc2.user_id != ? " +
                        " JOIN users " +
                        " ON uc2.user_id = users.user_id " +
                        " WHERE chats.chat_type = 'private' " +
                        " AND uc1.user_id = ? " +
                        " UNION " +
                        " SELECT chats.chat_id, messages.text_message, messages.date_message, chats.name_chat AS chatname " +
                        " FROM chats " +
                        " LEFT JOIN messages " +
                        " ON chats.chat_last_message = messages.message_id " +
                        " JOIN users_chats " +
                        " ON chats.chat_id = users_chats.chat_id " +
                        " WHERE users_chats.user_id = ?" +
                        " AND chats.chat_type = 'group' " +
                        " UNION " +
                        " SELECT chats.chat_id, messages.text_message, messages.date_message, chats.chat_type AS chatname " +
                        " FROM chats " +
                        " LEFT JOIN messages " +
                        " ON chats.chat_last_message = messages.message_id " +
                        " JOIN users_chats " +
                        " ON chats.chat_id = users_chats.chat_id " +
                        " WHERE users_chats.user_id = ?" +
                        " AND chats.chat_type = 'saved' ",
                new PrintChatMapper(), user.getId(), user.getId(), user.getId(), user.getId());
    }

    // Не используется. Надо поменять
    @Override
    public Chat getByNumberChat(int i) {

        return jdbcTemplate.query("SELECT * FROM chats WHERE chat_id=?",
                new Object[]{i},
                new PrintChatMapper()).stream().findAny().orElse(null);
    }

    @Override
    public Chat addChat(List<User> users, String chatType) {
        Chat chat = new Chat();

        transactionTemplate.execute(status -> {
            int id = jdbcTemplate.queryForObject("INSERT INTO chats (chat_type) VALUES(?) RETURNING chat_id",
                    Integer.class, chatType);

            chat.setChatId(id);

            for (User user : users) {
                jdbcTemplate.update("INSERT INTO users_chats (user_id,chat_id) VALUES(?,?)", user.getId(), id);
            }

            return id;
        });

        return chat;
    }

    @Override
    public Message addMessageToChat(String text, User user, int chatId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Message message = new Message(user, text, localDateTime);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                int id = jdbcTemplate.queryForObject("INSERT INTO messages (text_message,chat_id,user_id,date_message) VALUES(?,?,?,?) RETURNING message_id",
                        Integer.class,
                        text, chatId, user.getId(), java.sql.Timestamp.valueOf(localDateTime));

                jdbcTemplate.update("UPDATE chats SET chat_last_message=? WHERE chat_id=?", id, chatId);

                message.setMessageId(id);
            }
        });

        return message;
    }

    @Override
    public List<Message> getListMessageByNumberChat(int i) {
        return jdbcTemplate.query(" SELECT messages.date_message, messages.user_id, messages.text_message, users.user_name, messages.message_id " +
                        " FROM messages " +
                        " JOIN users " +
                        " ON messages.user_id = users.user_id " +
                        " WHERE chat_id=? ",
                new MessageMapper(), i);
    }

    @Override
    public Chat addGroupChat(String nameChat, User user) {
        Chat chat = new Chat();

        transactionTemplate.execute(status -> {
            int id = jdbcTemplate.queryForObject("INSERT INTO chats (chat_type,name_chat) VALUES(?,?) RETURNING chat_id",
                    Integer.class, "group", nameChat);

            chat.setChatId(id);

            jdbcTemplate.update("INSERT INTO users_chats (user_id,chat_id) VALUES(?,?)", user.getId(), id);

            return id;
        });

        return chat;
    }

    @Override
    public Chat findChatByName(String nameChat, User user) {
        return (Chat) jdbcTemplate.query("SELECT chats.chat_id, chats.name_chat AS chatname " +
                        " FROM chats " +
                        " JOIN users_chats " +
                        " ON chats.chat_id = users_chats.chat_id " +
                        " WHERE chats.name_chat = ? " +
                        " AND users_chats.user_id = ? ",
                new ChatMapper(), nameChat, user.getId()).stream().findAny().orElse(null);
    }

    @Override
    public void addUserToGroupChat(int userID, Chat chat) {
        jdbcTemplate.update("INSERT INTO users_chats (user_id,chat_id) VALUES(?,?)", userID, chat.getChatId());
    }

    @Override
    public boolean findUserInChat(Integer chatID, User user) {

        return jdbcTemplate.query("SELECT users_chats.chat_id, user_id AS chatname FROM users_chats WHERE chat_id=? AND user_id= ?",
                new ChatMapper(), chatID, user.getId()).stream().findAny().orElse(null) != null;

    }

    @Override
    public Chat searchChatBetweenUsers(User user1, User user2) {

        return jdbcTemplate.query("select u.user_name AS chatname, uc.chat_id, u.user_id\n" +
                        "                         from users u  \n" +
                        "                         join users_users uu  \n" +
                        "                         on u.user_id = uu.user2_id \n" +
                        "                         join users_chats uc \n" +
                        "                         on uu.user1_id=uc.user_id \n" +
                        "                         join chats \n" +
                        "                         on uc.chat_id =  chats.chat_id \n" +
                        "                         and chats.chat_type= 'private' \n" +
                        "                         join users_chats uc2 \n" +
                        "                         on uu.user2_id = uc2.user_id and uc.chat_id=uc2.chat_id \n" +
                        "                         where uu.user1_id = ? and u.user_id = ? OR uu.user1_id = ? and u.user_id = ?",
                new ChatMapper(), user1.getId(), user2.getId(), user2.getId(), user1.getId()).stream().findAny().orElse(null);
    }

    @Override
    public List<User> findListUserInChat(int chatID) {

        return jdbcTemplate.query(" SELECT users.user_id, users.user_name, users.user_email, users.user_password, users.enabled " +
                " FROM users_chats " +
                " JOIN users " +
                " ON users_chats.user_id = users.user_id " +
                " WHERE users_chats.chat_id = ? ", new UserMapper(), chatID);
    }

    @Override
    public List<Message> findFirst20(int chatId) {

        return jdbcTemplate.query(" SELECT messages.date_message, messages.user_id, messages.text_message, users.user_name, messages.message_id, messages.name_file " +
                " FROM messages " +
                " JOIN users " +
                " ON messages.user_id = users.user_id " +
                " WHERE chat_id=? ORDER BY message_id DESC LIMIT 20 ", new MessageMapper(), chatId);
    }

    @Override
    public List<Message> next20(int chatId, int messageId) {

        return jdbcTemplate.query(" SELECT messages.date_message, messages.user_id, messages.text_message, users.user_name, messages.message_id, messages.name_file " +
                " FROM messages " +
                " JOIN users " +
                " ON messages.user_id = users.user_id " +
                " WHERE chat_id=? AND message_id < ? ORDER BY message_id DESC LIMIT 20 ", new MessageMapper(), chatId, messageId);
    }

    @Override
    public void uploadFileInMessage(String nameFile, int idMessage) {
        jdbcTemplate.update("UPDATE messages SET name_file=? WHERE message_id=?", nameFile, idMessage);
    }
}
