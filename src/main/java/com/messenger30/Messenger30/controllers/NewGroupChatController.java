package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.helper.PrintFriend;
import com.messenger30.Messenger30.helper.PrintMessage;
import com.messenger30.Messenger30.repository.Chat;
import com.messenger30.Messenger30.repository.ChatRepository;
import com.messenger30.Messenger30.repository.User;
import com.messenger30.Messenger30.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
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

        List<PrintFriend> printFriends = userRepository.findListFriendsByUser(user);
        ArrayList<Chat> chats = (ArrayList<Chat>) chatRepository.findListChatByUser(user);

        model.addAttribute("printFriends", printFriends);
        model.addAttribute("printChats", chats);
        model.addAttribute("activePage", "CHAT");
        model.addAttribute("title", user.getName());
        model.addAttribute("active", false);
        model.addAttribute("openPopup", true);

        return "chat";
    }

    @PostMapping("/newgroupchat")
    public String newMessage(@RequestParam(value = "idFriend", required = false) List<String> nameFriend ,
                             String nameChat, @AuthenticationPrincipal User user, Model model) {
        //Переделать
        if (nameChat.equals("")){
            return "chat";
        }

        Chat newChat = chatRepository.addGroupChat(nameChat, "group", user);

//        if (nameFriend != null){
            for (String s : nameFriend) {
                User newUser = new User(Integer.parseInt(s));
                chatRepository.addUserToGroupChat(newUser, newChat);
            }
//        }

        model.addAttribute("activePage", "CHAT");
        model.addAttribute("title", user.getName());

        return "redirect:/chat";
    }

//    @GetMapping(value = "/chat", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public Collection<PrintFriend> printListFriend(@AuthenticationPrincipal User user) {
//        List<PrintFriend> printFriends = userRepository.findListFriendsByUser(user);
//        return printFriends;
//    }
}
