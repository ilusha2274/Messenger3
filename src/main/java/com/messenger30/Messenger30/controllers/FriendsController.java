package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.exceptions.UserNotFoundException;
import com.messenger30.Messenger30.exceptions.WrongAlreadyFriends;
import com.messenger30.Messenger30.services.IMessengerService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FriendsController {

    private final IMessengerService messengerService;

    public FriendsController(IMessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @GetMapping("/friends")
    public String printFriendsList(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("printFriends", messengerService.findListFriendsByUser(user));
        model.addAttribute("activePage", "FRIENDS");

        return "friends";
    }

    @PostMapping("/friends")
    public String addNewFriend(Model model, @AuthenticationPrincipal User user, String email) {

        try {
            messengerService.addNewFriend(user, email);
        } catch (UserNotFoundException | WrongAlreadyFriends e) {
            model.addAttribute("exception", e.getMessage());
            model.addAttribute("openPopup", true);
        }

        model.addAttribute("activePage", "FRIENDS");
        model.addAttribute("printFriends", messengerService.findListFriendsByUser(user));
        model.addAttribute("user", user);

        return "friends";

    }

    @GetMapping("/deleteFriend/{id}")
    public String deleteFriend(@AuthenticationPrincipal User user, @PathVariable Integer id, Model model) {
        messengerService.deleteFriend(user.getId(), id);

        return "redirect:/friends";
    }
}
