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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NewGroupChatController {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public NewGroupChatController(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/newgroupchat")
    public String printHewMessage(@AuthenticationPrincipal User user, Model model) {

//        List<PrintFriend> printFriends = userRepository.findListFriendsByUser(user);
//
//        model.addAttribute("printFriends", printFriends);
//        model.addAttribute("activePage", "NEWGROUPCHAT");
//        model.addAttribute("title", user.getName());
        model.addAttribute("newAdd", true);

        return "redirect:/chat";
    }

    @PostMapping("/newgroupchat")
    public String newMessage(@RequestParam(value = "idChecked", required = false) List<String> nameFriend , String nameChat, @AuthenticationPrincipal User user, Model model) {

        Chat newChat = chatRepository.addGroupChat(nameChat, "group", user);

        if (nameFriend != null){
            for (String s : nameFriend) {
                User newUser = new User(Integer.parseInt(s));
                chatRepository.addUserToGroupChat(newUser, newChat);
            }
        }

        model.addAttribute("activePage", "CHAT");
        model.addAttribute("title", user.getName());
        return "redirect:chat";
    }
}
