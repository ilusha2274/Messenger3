package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.services.IMessengerService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AddUserGroupChatController {

    private final IMessengerService messengerService;

    public AddUserGroupChatController(IMessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @GetMapping("chat/{id}/addUser")
    public String printPopupAddUserInChat(@PathVariable Integer id, @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("activePage", "CHAT");
        model.addAttribute("printChats", messengerService.findListChatByUser(user));
        model.addAttribute("active", false);

        if (messengerService.isUserInChat(id, user)) {
            model.addAttribute("printMessages", messengerService.returnFirst20Messages(id, user));
            model.addAttribute("active", true);
            model.addAttribute("chat", messengerService.findChatById(user, id));
            model.addAttribute("listUserInChat", messengerService.findListUserInChat(id));
            model.addAttribute("openPopupAddUser", true);
        }

        return "chat";
    }

    @PostMapping("chat/{id}/addUser")
    public String addUserInChat(@PathVariable Integer id, @AuthenticationPrincipal User user, String newUserEmail, Model model) {
        try {
            messengerService.addUserInGroupChat(newUserEmail, id);
        } catch (RuntimeException e) {
            model.addAttribute("exception", e.getMessage());
        }

        model.addAttribute("user", user);
        model.addAttribute("activePage", "CHAT");
        model.addAttribute("printChats", messengerService.findListChatByUser(user));
        model.addAttribute("active", false);

        if (messengerService.isUserInChat(id, user)) {
            model.addAttribute("printMessages", messengerService.returnFirst20Messages(id, user));
            model.addAttribute("active", true);
            model.addAttribute("chat", messengerService.findChatById(user, id));
            model.addAttribute("listUserInChat", messengerService.findListUserInChat(id));
            model.addAttribute("openPopupAddUser", true);
        }

        return "chat";
    }

    @PostMapping("chat/{id}/delete/{userID}")
    public String deleteUserInGroupChat(@PathVariable Integer id, @PathVariable Integer userID, @AuthenticationPrincipal User user) {
        messengerService.deleteUserInGroupChat(id, userID);

        return "redirect:/chat/" + id + "/addUser";
    }

}
