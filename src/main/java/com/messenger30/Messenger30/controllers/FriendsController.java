package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.helper.PrintFriend;
import com.messenger30.Messenger30.repository.Chat;
import com.messenger30.Messenger30.repository.ChatRepository;
import com.messenger30.Messenger30.repository.User;
import com.messenger30.Messenger30.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FriendsController {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public FriendsController(UserRepository userRepository, ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @GetMapping("/friends")
    public String printFriendsList(@AuthenticationPrincipal User user, String email, Model model) {

        List<PrintFriend> printFriends = userRepository.findListFriendsByUser(user);

        model.addAttribute("title", user.getName());
        model.addAttribute("printFriends", printFriends);
        model.addAttribute("activePage", "FRIENDS");

        return "friends";
    }

    @PostMapping("/friends")
    public String addNewFriends2(Model model, @AuthenticationPrincipal User user, String email) {

        User user2 = userRepository.findUserByEmail(email);

        if (user2 == null) {
            model.addAttribute("activePage", "FRIENDS");
            model.addAttribute("exception", "Пользователь не найден");
        } else {
            addFriend(user, user2);
            model.addAttribute("activePage", "FRIENDS");
        }
        List<PrintFriend> printFriends = userRepository.findListFriendsByUser(user);
        model.addAttribute("printFriends", printFriends);
        model.addAttribute("title", user.getName());
        return "friends";

    }

    public void addFriend(User user, User user2) {
        Chat chat = chatRepository.searchChatBetweenUsers(user2, user);
        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        if (chat != null) {
            userRepository.addNewFriends(user, user2);
        } else {
            chatRepository.addChat(users, "private");
            userRepository.addNewFriends(user, user2);
        }
    }
}
