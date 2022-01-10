package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.exception.PasswordMismatchException;
import com.messenger30.Messenger30.exception.WrongEmailException;
import com.messenger30.Messenger30.repository.ChatRepository;
import com.messenger30.Messenger30.repository.User;
import com.messenger30.Messenger30.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final ChatRepository chatRepository;

    public RegistrationController(UserRepository userRepository, AuthenticationManager authenticationManager, ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.chatRepository = chatRepository;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(HttpSession session, String email, String name, String password,
                               String twoPassword, Model model) {

        User newUser = new User(name, email, password);

        try {
            if (!(userRepository.findEmailUser(email)) && userRepository.checkPassword(password, twoPassword)) {
                User user = userRepository.addUser(newUser, twoPassword);
                ArrayList<User> users = new ArrayList<>();
                users.add(user);
                chatRepository.addChat(users, "saved");
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (WrongEmailException | PasswordMismatchException | SQLException e) {
            model.addAttribute("exception", e.getMessage());
            return "registration";
        }
        return "redirect:home";
    }

}
