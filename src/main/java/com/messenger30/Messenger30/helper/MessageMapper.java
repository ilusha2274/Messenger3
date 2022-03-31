package com.messenger30.Messenger30.helper;

import com.messenger30.Messenger30.repository.Message;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class MessageMapper implements RowMapper<Message> {
    @Override
    public Message mapRow(ResultSet resultSet, int i) throws SQLException {

        LocalDateTime localDateTime = resultSet.getTimestamp("date_message").toLocalDateTime();

        return new Message(resultSet.getInt("user_id"), resultSet.getString("text_message"),
                localDateTime, resultSet.getString("user_name"),resultSet.getInt("message_id"),
                resultSet.getString("name_file"));

    }
}
