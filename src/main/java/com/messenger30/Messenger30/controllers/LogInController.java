package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.services.IMessengerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class LogInController {

    private final IMessengerService messengerService;

    public LogInController(IMessengerService messengerService) {
        this.messengerService = messengerService;
    }

    @GetMapping("/")
    public String redirectLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String printLogin() {
        return "login";
    }

    @PostMapping("/fail_login")
    public String failLogin(String email, Model model) {
        User user = messengerService.findUserByEmail(email);

        if (user == null || user.isEnabled())
            model.addAttribute("exception", "Неверное имя пользователя или пароль!");
        else
            model.addAttribute("exception", "Пользователь заблокирован!");

        model.addAttribute("email", email);

        return "login";
    }
}
