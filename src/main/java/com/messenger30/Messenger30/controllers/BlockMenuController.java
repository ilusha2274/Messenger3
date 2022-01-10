package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.repository.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class BlockMenuController {

    @GetMapping("/home")
    public String printHome(@AuthenticationPrincipal User user, Model model) {

        model.addAttribute("title", user.getName());

        return "home";
    }

    @GetMapping("/profile")
    public String printProfile(@AuthenticationPrincipal User user,Model model) {

        model.addAttribute("activePage", "PROFILE");
        model.addAttribute("title", user.getName());

        return "profile";
    }

    @GetMapping("/settings")
    public String printSettings(@AuthenticationPrincipal User user,Model model) {

        model.addAttribute("activePage", "SETTINGS");
        model.addAttribute("title", user.getName());

        return "settings";
    }

    @GetMapping("/exit")
    public String printExit(HttpSession session) {

        session.invalidate();

        return "redirect:login";
    }
}
