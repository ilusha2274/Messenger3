package com.messenger30.Messenger30.controllers;

import com.messenger30.Messenger30.helper.PrintMessage;
import com.messenger30.Messenger30.repository.Chat;
import com.messenger30.Messenger30.repository.ChatRepository;
import com.messenger30.Messenger30.repository.Message;
import com.messenger30.Messenger30.repository.User;
import com.messenger30.Messenger30.websocket.ChatMessage;
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
import org.springframework.web.util.HtmlUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Controller
public class ChatController {

    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatterDate = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);

    public ChatController(ChatRepository chatRepository, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatRepository = chatRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @GetMapping("/chat")
    public String printChatList(@AuthenticationPrincipal User user, Model model) {

        ArrayList<Chat> chats = (ArrayList<Chat>) chatRepository.findListChatByUser(user);

        model.addAttribute("printChats", chats);
        model.addAttribute("title", user.getName());
        model.addAttribute("activePage", "CHAT");
        model.addAttribute("active", false);

        return "chat";
    }

    @GetMapping("/chat/{id}")
    public String printChat(Model model, @AuthenticationPrincipal User user, @PathVariable Integer id) {

        model.addAttribute("title", user.getName());

        ArrayList<Chat> chats = (ArrayList<Chat>) chatRepository.findListChatByUser(user);

        if (chatRepository.findUserInChat(id, user)) {

            ArrayList<PrintMessage> printMessages = printMessages(chatRepository.findFirst20(id), user);

            model.addAttribute("activePage", "CHAT");
            model.addAttribute("printMessages", printMessages);
            model.addAttribute("printChats", chats);
            model.addAttribute("active", true);
            model.addAttribute("chatID", id);
            model.addAttribute("name", user.getName());
            model.addAttribute("userId", user.getId());


        } else {
            model.addAttribute("printChats", chats);
            model.addAttribute("activePage", "CHAT");
            model.addAttribute("active", false);

        }
        return "chat";
    }

    @GetMapping(value = "/chat/{id}/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Collection<PrintMessage> printNext20messages (@AuthenticationPrincipal User user, @PathVariable Integer id, @PathVariable Integer messageId){
        ArrayList<PrintMessage> printMessage = printMessages(chatRepository.next20(id,messageId), user);
        return printMessage;
    }

    @MessageMapping("/chat/{id}")
    //@SendTo("/queue/messages/chat/{id}")
    public void sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable Integer id, UsernamePasswordAuthenticationToken authenticationToken ) {

        //int id = chatMessage.getIdChat();
        User user = (User) authenticationToken.getPrincipal();

        boolean result = chatRepository.findUserInChat(id,user);
        if (result){
            user.setName(chatMessage.getNameAuthor());
            Message newMessage = chatRepository.addMessageToChat(chatMessage.getContent(), user, id);
            String date = newMessage.getLocalDateTime().format(dateTimeFormatterTime) + " | " +
                    newMessage.getLocalDateTime().format(dateTimeFormatterDate);

            ChatMessage chatMessage2 = new ChatMessage();
            chatMessage2.setContent(HtmlUtils.htmlEscape(chatMessage.getContent()));
            chatMessage2.setNameAuthor(HtmlUtils.htmlEscape(chatMessage.getNameAuthor()));
            chatMessage2.setTime(HtmlUtils.htmlEscape(date));
            chatMessage2.setUserId(chatMessage.getUserId());

            List<User> users = chatRepository.findListUserInChat(id);
            for (User value : users) {
                simpMessagingTemplate.convertAndSendToUser(value.getEmail(), "/queue/messages/chat/" + id, chatMessage2);
            }
        }


        //simpMessagingTemplate.convertAndSend("/queue/messages/chat/" + id, chatMessage2);

    }

    private ArrayList<PrintMessage> printMessages(List<Message> messages, User user) {

        ArrayList<PrintMessage> printMessages = new ArrayList<>();

        for (int i = 0; i < messages.size(); i++) {
            printMessages.add(fillingMessage(messages.get(i), user));
        }

        return printMessages;
    }

    private PrintMessage fillingMessage(Message message, User user) {

        PrintMessage printMessage;

        String date = message.getLocalDateTime().format(dateTimeFormatterTime) + " | " +
                message.getLocalDateTime().format(dateTimeFormatterDate);

        if (message.getIdAuthor().equals(user.getId())) {
            printMessage = new PrintMessage(true, message.getText(), date, message.getNameAuthor(),message.getMessageId());
        } else {
            printMessage = new PrintMessage(false, message.getText(), date, message.getNameAuthor(),message.getMessageId());
        }

        return printMessage;
    }
}
