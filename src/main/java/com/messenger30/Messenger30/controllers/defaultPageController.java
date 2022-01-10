package com.messenger30.Messenger30.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class defaultPageController {

    @GetMapping("/")
    public String defaultPage (Model model) {
        return "defaultpage";
    }

}
