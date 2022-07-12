package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.services.IMessengerService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final AuthenticationManager authenticationManager;
    private final IMessengerService messengerService;

    public RegistrationController(AuthenticationManager authenticationManager, IMessengerService messengerService) {
        this.authenticationManager = authenticationManager;
        this.messengerService = messengerService;
    }

    @GetMapping("/registration")
    public String printRegistration(Model model) {
        model.addAttribute("user", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(String email, String name, String password,
                               String twoPassword, Model model) {

        User newUser = new User(name, email, password);
        model.addAttribute("user", newUser);

        try {
            messengerService.registerUser(newUser, twoPassword);

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            model.addAttribute("exception", e.getMessage());

            return "registration";
        }

        return "redirect:home";
    }

}
