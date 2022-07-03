package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.domain.Chat;
import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.repository.ChatRepository;
import com.messenger30.Messenger30.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AddUserGroupChatController {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public AddUserGroupChatController(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/addUserGroupChat")
    public String printAddUserGroupChat(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("activePage", "ADDUSERGROUPCHAT");
        model.addAttribute("title", user.getName());

        return "addUserGroupChat";
    }

    @PostMapping("/addUserGroupChat")
    public String addUserGroupChat(String nameChat, String emailUser, @AuthenticationPrincipal User user, Model model) {
        User newUser = userRepository.findUserByEmail(emailUser);
        Chat chat = chatRepository.findChatByName(nameChat, user);

        if (chat != null && newUser != null) {
            chatRepository.addUserToGroupChat(newUser.getId(), chat);
            model.addAttribute("activePage", "CHAT");
            model.addAttribute("title", user.getName());

            return "redirect:chat";
        } else {
            model.addAttribute("activePage", "ADDUSERGROUPCHAT");
            model.addAttribute("exception", "чат или пользователь не найден");
            model.addAttribute("title", user.getName());

            return "addUserGroupChat";
        }
    }

}
