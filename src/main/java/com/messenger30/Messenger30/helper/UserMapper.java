package com.messenger30.Messenger30.helper;

import com.messenger30.Messenger30.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User(resultSet.getInt("user_id"), resultSet.getString("user_name"),
                resultSet.getString("user_email"),
                resultSet.getString("user_password"), resultSet.getBoolean("enabled"));

        user.setRole("USER");

        return user;
    }
}
