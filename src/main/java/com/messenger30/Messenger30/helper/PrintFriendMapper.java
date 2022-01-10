package com.messenger30.Messenger30.helper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PrintFriendMapper implements RowMapper<PrintFriend> {

    @Override
    public PrintFriend mapRow(ResultSet resultSet, int i) throws SQLException {

        return new PrintFriend(resultSet.getInt("chat_id"), resultSet.getString("user_name"), resultSet.getInt("user_id"));
    }

}
