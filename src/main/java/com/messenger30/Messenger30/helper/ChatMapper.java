package com.messenger30.Messenger30.helper;

import com.messenger30.Messenger30.domain.Chat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatMapper implements RowMapper<Chat> {

    @Override
    public Chat mapRow(ResultSet resultSet, int i) throws SQLException {
        Chat chat = new Chat(resultSet.getString("chatname"),
                resultSet.getInt("chat_id"), "", "");

        return chat;
    }
}
