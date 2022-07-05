package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.exceptions.WrongNameChat;
import com.messenger30.Messenger30.services.IMessengerService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NewGroupChatController {

    private final IMessengerService messengerService;

    public NewGroupChatController(IMessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @GetMapping("/newgroupchat")
    public String printHewMessage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("printFriends", messengerService.findListFriendsByUser(user));
        model.addAttribute("printChats", messengerService.findListChatByUser(user));
        model.addAttribute("activePage", "CHAT");
        model.addAttribute("user", user);
        model.addAttribute("active", false);
        model.addAttribute("openPopup", true);

        return "chat";
    }

    @PostMapping("/newgroupchat")
    public String newMessage(@RequestParam(value = "idFriend", required = false) List<Integer> nameFriends,
                             String nameChat, @AuthenticationPrincipal User user, Model model) {

        try {
            messengerService.addGroupChat(nameFriends, user, nameChat);
        } catch (WrongNameChat e) {
            model.addAttribute("exception", e.getMessage());
            model.addAttribute("printFriends", messengerService.findListFriendsByUser(user));
            model.addAttribute("printChats", messengerService.findListChatByUser(user));
            model.addAttribute("activePage", "CHAT");
            model.addAttribute("user", user);
            model.addAttribute("active", false);
            model.addAttribute("openPopup", true);

            return "chat";
        }

        return "redirect:/chat";
    }

}
