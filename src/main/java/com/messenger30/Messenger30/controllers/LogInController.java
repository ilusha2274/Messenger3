package com.messenger30.Messenger30.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class LogInController {

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
        model.addAttribute("exception", "Неверное имя пользователя или пароль");
        model.addAttribute("email",email);

        return "login";
    }
}
