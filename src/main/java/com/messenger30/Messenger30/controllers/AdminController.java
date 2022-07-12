package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.services.IMessengerService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final IMessengerService messengerService;

    public AdminController(IMessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @GetMapping("/admin")
    public String printAdmin(@AuthenticationPrincipal User user, Model model) {
        List<User> users = messengerService.getAllUsers();
        List<User> admins = users.stream().filter(x -> x.getRole().equals("ROLE_ADMIN") && x.isEnabled()).collect(Collectors.toList());
        List<User> usersList = users.stream().filter(x -> x.getRole().equals("ROLE_USER") && x.isEnabled()).collect(Collectors.toList());
        List<User> banned = users.stream().filter(x -> !x.isEnabled()).collect(Collectors.toList());

        model.addAttribute("listAdmins", admins);
        model.addAttribute("listUsers", usersList);
        model.addAttribute("listBanned", banned);
        model.addAttribute("activePage", "ADMIN");
        model.addAttribute("user", user);

        return "admin";
    }

    @GetMapping("/admin/makeUser/{id}")
    public String makeUser(@PathVariable Integer id) {
        messengerService.makeUser(id);

        return "redirect:/admin";
    }

    @GetMapping("/admin/makeAdmin/{id}")
    public String makeAdmin(@PathVariable Integer id) {
        messengerService.makeAdmin(id);

        return "redirect:/admin";
    }

    @GetMapping("/admin/ban/{id}")
    public String ban(@PathVariable Integer id) {
        messengerService.ban(id);

        return "redirect:/admin";
    }

    @GetMapping("/admin/unBan/{id}")
    public String unBan(@PathVariable Integer id) {
        messengerService.unBan(id);

        return "redirect:/admin";
    }

}
