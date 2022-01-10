package com.messenger30.Messenger30.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LogInController {

    @GetMapping("/login")
    public String printLogin(String error, Model model) {
        if (error != null) {
            model.addAttribute("exception", "Неверное имя пользователя или пароль");
        }
        return "login";
    }

}
