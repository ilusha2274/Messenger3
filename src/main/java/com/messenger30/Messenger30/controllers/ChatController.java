package com.messenger30.Messenger30.controllers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.messenger30.Messenger30.helper.PrintMessage;
import com.messenger30.Messenger30.repository.Chat;
import com.messenger30.Messenger30.repository.ChatRepository;
import com.messenger30.Messenger30.repository.Message;
import com.messenger30.Messenger30.repository.User;
import com.messenger30.Messenger30.websocket.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class ChatController {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${name.bucket}")
    private String nameBucket;

    private final AWSCredentials credentials;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatterDate = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);

    public ChatController(ChatRepository chatRepository, SimpMessagingTemplate simpMessagingTemplate,AWSCredentials credentials) {
        this.chatRepository = chatRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.credentials = credentials;
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
    public void sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable Integer id,
                            UsernamePasswordAuthenticationToken authenticationToken) {

        String urlFile = chatMessage.getNameFile().replaceAll("data:image/png;base64,", "");
        InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(urlFile));

        ObjectMetadata metadata = null;
        String nameFile = stringGeneration();
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        uploadPath,
                        "ru-moscow")).build();
        try {
            s3.putObject(nameBucket, nameFile, inputStream,metadata);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

//        User user = (User) authenticationToken.getPrincipal();
//        boolean result = chatRepository.findUserInChat(id,user);
//
//        if(result){
//            user.setName(chatMessage.getNameAuthor());
//            Message newMessage = chatRepository.addMessageToChat(chatMessage.getContent(), user, id);
//            String date = newMessage.getLocalDateTime().format(dateTimeFormatterTime);
////                + " | " + newMessage.getLocalDateTime().format(dateTimeFormatterDate);
//
//            ChatMessage chatMessage2 = new ChatMessage();
//            chatMessage2.setContent(HtmlUtils.htmlEscape(chatMessage.getContent()));
//            chatMessage2.setNameAuthor(HtmlUtils.htmlEscape(chatMessage.getNameAuthor()));
//            chatMessage2.setTime(HtmlUtils.htmlEscape(date));
//            chatMessage2.setUserId(chatMessage.getUserId());
//
//            List<User> users = chatRepository.findListUserInChat(id);
//            for (User value : users) {
//                simpMessagingTemplate.convertAndSendToUser(value.getEmail(), "/queue/messages/chat/" + id, chatMessage2);
//            }
//        }

    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public void upload(@RequestPart("file") MultipartFile file,
                       @RequestPart("chatMessage") ChatMessage chatMessage) {

        InputStream inputStream = null;
        ObjectMetadata metadata = null;
        String nameFile = stringGeneration();
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        uploadPath,
                "ru-moscow")).build();
        try {
            s3.putObject(nameBucket, nameFile, inputStream,metadata);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    private String stringGeneration(){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i =0;i<12;i++){
            int num = random.nextInt(62);
            sb.append(str.charAt(num));
        }
        return sb.toString();
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

        String date = message.getLocalDateTime().format(dateTimeFormatterTime);
//                + " | " + message.getLocalDateTime().format(dateTimeFormatterDate);

        if (message.getIdAuthor().equals(user.getId())) {
            printMessage = new PrintMessage(true, message.getText(), date, message.getNameAuthor(),message.getMessageId());
        } else {
            printMessage = new PrintMessage(false, message.getText(), date, message.getNameAuthor(),message.getMessageId());
        }

        return printMessage;
    }
}
