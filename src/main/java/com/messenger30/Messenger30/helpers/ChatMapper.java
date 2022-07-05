package com.messenger30.Messenger30.helpers;

import com.messenger30.Messenger30.domain.Chat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatMapper implements RowMapper<Chat> {

    @Override
    public Chat mapRow(ResultSet resultSet, int i) throws SQLException {

        return new Chat(resultSet.getInt("chat_id"), resultSet.getString("chatname"), resultSet.getString("chat_type"));
    }
}
