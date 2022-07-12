package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.domain.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class error403Controller {

    @GetMapping("/403")
    public String error403(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);

        return "403";
    }
}
