package com.messenger30.Messenger30.controllers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

import javax.persistence.Convert;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
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
    public void sendMessage(@Payload ChatMessage chatMessage , @DestinationVariable Integer id,
                            UsernamePasswordAuthenticationToken authenticationToken) {

        User user = (User) authenticationToken.getPrincipal();
        boolean result = chatRepository.findUserInChat(id,user);

        if(result){
            Message newMessage = chatRepository.addMessageToChat(chatMessage.getContent(), user, id);
            String date = newMessage.getLocalDateTime().format(dateTimeFormatterTime);
//                + " | " + newMessage.getLocalDateTime().format(dateTimeFormatterDate);

            chatMessage.setTime(HtmlUtils.htmlEscape(date));

            if (chatMessage.isHaveFile()){
                uploadFile(chatMessage, newMessage.getMessageId());
            }

            List<User> users = chatRepository.findListUserInChat(id);
            for (User value : users) {
                simpMessagingTemplate.convertAndSendToUser(value.getEmail(), "/queue/messages/chat/" + id, chatMessage);
            }
        }

    }

    private void uploadFile (ChatMessage chatMessage, int id){

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
            chatRepository.uploadFileInMessage(nameFile,id);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    public String downloadFile (String key_name){
        System.out.format("Downloading %s from S3 bucket %s...\n", key_name, nameBucket);
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        uploadPath,
                        "ru-moscow")).build();
        byte[] read_buf = new byte[1024];
        try {
            S3Object o = s3.getObject(nameBucket, key_name);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File(key_name));
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        String str = "https://messenger.obs.ru-moscow-1.hc.sbercloud.ru/" + key_name;
        return str;
    }

    private String stringGeneration(){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i =0;i<12;i++){
            int num = random.nextInt(62);
            sb.append(str.charAt(num));
        }
        return sb.toString();
    }

    private ArrayList<PrintMessage> printMessages(List<Message> messages, User user) {

        ArrayList<PrintMessage> printMessages = new ArrayList<>();

        for (Message message : messages) {
            printMessages.add(fillingMessage(message, user));
        }

        return printMessages;
    }

    private PrintMessage fillingMessage(Message message, User user) {

        PrintMessage printMessage;

        String date = message.getLocalDateTime().format(dateTimeFormatterTime);
//                + " | " + message.getLocalDateTime().format(dateTimeFormatterDate);

        if (message.getIdAuthor().equals(user.getId())) {
            printMessage = new PrintMessage(true, message.getText(), date, message.getNameAuthor(),message.getMessageId(),false);
        } else {
            printMessage = new PrintMessage(false, message.getText(), date, message.getNameAuthor(),message.getMessageId(),false);
        }
        if (message.getNameFile() != null){
            printMessage.setFile(true);
            printMessage.setNameFile(downloadFile(message.getNameFile()));
//            printMessage.setNameFile(message.getNameFile());
        }

        return printMessage;
    }
}
