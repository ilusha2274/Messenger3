package com.messenger30.Messenger30.controllers;

import com.amazonaws.auth.AWSCredentials;
import com.messenger30.Messenger30.domain.ChatMessage;
import com.messenger30.Messenger30.domain.PrintMessage;
import com.messenger30.Messenger30.domain.User;
import com.messenger30.Messenger30.services.IMessengerService;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.List;

@Controller
public class ChatController {

    private final AWSCredentials credentials;
    private final IMessengerService messengerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatController(SimpMessagingTemplate simpMessagingTemplate, AWSCredentials credentials, IMessengerService messengerService) {
        this.messengerService = messengerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.credentials = credentials;
    }

    @GetMapping("/chat")
    public String printChatList(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("printChats", messengerService.findListChatByUser(user));
        model.addAttribute("user", user);
        model.addAttribute("activePage", "CHAT");
        model.addAttribute("active", false);
        model.addAttribute("chatIdActive", -1);

        return "chat";
    }

    @GetMapping("/chat/{id}")
    public String printChatById(Model model, @AuthenticationPrincipal User user, @PathVariable Integer id) {
        model.addAttribute("user", user);
        model.addAttribute("activePage", "CHAT");
        model.addAttribute("printChats", messengerService.findListChatByUser(user));
        model.addAttribute("active", false);

        if (messengerService.isUserInChat(id, user)) {
            model.addAttribute("printMessages", messengerService.returnFirst30Messages(id, user));
            model.addAttribute("active", true);
            model.addAttribute("chat", messengerService.findChatById(user, id));
            model.addAttribute("chatIdActive", id);
        }

        return "chat";
    }

    @GetMapping(value = "/chat/{id}/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<PrintMessage> printNext20messages(@AuthenticationPrincipal User user, @PathVariable Integer id, @PathVariable Integer messageId) {

        return messengerService.returnNext30Messages(id, messageId, user);
    }

    @MessageMapping("/chat/{id}")
    public void sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable Integer id,
                            UsernamePasswordAuthenticationToken authenticationToken) {

        User user = (User) authenticationToken.getPrincipal();

        if (messengerService.isUserInChat(id, user)) {
            messengerService.addMessageToChat(chatMessage, user);

            List<User> users = messengerService.findListUserInChat(id);

            for (User value : users) {
                simpMessagingTemplate.convertAndSendToUser(value.getEmail(), "/queue/messages/chat/" + id, chatMessage);
            }
        }
    }

    @GetMapping("/chat/{id}/delete")
    public String deleteChatByUser(Model model, @AuthenticationPrincipal User user, @PathVariable Integer id) {
        return "redirect:/chat";
    }

    @GetMapping("/chat/{id}/deleteAll")
    public String deleteChatAllUser(Model model, @AuthenticationPrincipal User user, @PathVariable Integer id) {
        messengerService.deleteChat(id);

        return "redirect:/chat";
    }
}
