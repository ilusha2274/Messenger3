package com.messenger30.Messenger30.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.messenger30.Messenger30.domain.*;
import com.messenger30.Messenger30.exceptions.*;
import com.messenger30.Messenger30.repository.ChatRepository;
import com.messenger30.Messenger30.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.HtmlUtils;

import java.io.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MessengerService implements IMessengerService {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${name.bucket}")
    private String nameBucket;

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final AWSCredentials credentials;
    private final DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatterDate = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);

    public MessengerService(ChatRepository chatRepository, UserRepository userRepository, AWSCredentials credentials) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.credentials = credentials;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public void addNewFriend(User user, String emailUser2) {
        User user2 = userRepository.findUserByEmail(emailUser2);

        if (user2 == null)
            throw new UserNotFoundException("???????????????????????? ???? ????????????!");

        Chat chat = chatRepository.searchChatBetweenUsers(user2, user);

        if (userRepository.isAlreadyFriends(user.getId(), user2.getId()))
            throw new WrongAlreadyFriends("???????? ???????????????????????? ?????? ?????????????????? ?? ?????????? ???????????? ????????????");

        userRepository.addNewFriend(user, user2);

        if (chat == null)
            addChat(user, user2);
    }

    @Override
    public List<PrintFriend> findListFriendsByUser(User user) {

        return userRepository.findListFriendsByUser(user);
    }

    @Override
    public void registerUser(User user, String twoPassword) {
        if (isDataValidation(user, twoPassword)) {
            userRepository.addUser(user);

            addChat(user);
        }
    }

    @Override
    public List<Chat> findListChatByUser(User user) {
        List<Chat> chats = chatRepository.findListChatByUser(user);

        return sortChatByTime(chats);
    }

    @Override
    public boolean isUserInChat(int chatId, User user) {
        return chatRepository.findUserInChat(chatId, user);
    }

    @Override
    public List<PrintMessage> returnFirst30Messages(int chatId, User user) {
        List<Message> messages = chatRepository.findFirst30(chatId);

        return printMessages(messages, user);
    }

    @Override
    public List<PrintMessage> returnNext30Messages(int chatID, int messageId, User user) {
        List<Message> messages = chatRepository.next30(chatID, messageId);

        return printMessages(messages, user);
    }

    @Override
    public void addMessageToChat(ChatMessage chatMessage, User user) {
        Message newMessage = chatRepository.addMessageToChat(chatMessage.getContent(), user, chatMessage.getIdChat());
        String date = newMessage.getLocalDateTime().format(dateTimeFormatterTime);

        chatMessage.setTime(HtmlUtils.htmlEscape(date));

        if (chatMessage.isHaveFile()) {
            uploadFile(chatMessage, newMessage.getMessageId());
        }
    }

    @Override
    public List<User> findListUserInChat(int chatId) {
        return chatRepository.findListUserInChat(chatId);
    }

    @Override
    public void addGroupChat(List<Integer> idFriends, User user, String nameChat) {
        if (nameChat.equals(""))
            throw new WrongNameChat("?? ???????? ???????????? ???????? ??????!");

        if (idFriends == null)
            throw new WrongNameChat("???????????????? ???????? ???? ???????????? ????????????????????????!");

        Chat newChat = chatRepository.addGroupChat(nameChat, user);

        for (int id : idFriends) {
            chatRepository.addUserToGroupChat(id, newChat.getChatId());
        }
    }

    @Override
    public Chat findChatById(User user, int id) {
        Chat chat = chatRepository.findChatById(id);

        if (chat.getChatType().equals("saved")) {
            chat.setNameChat("saved");
        }

        if (chat.getChatType().equals("private")) {
            List<User> users = chatRepository.findListUserInChat(id);
            if (users.get(0).getId() == user.getId())
                chat.setNameChat(users.get(1).getName());
            else
                chat.setNameChat(users.get(0).getName());
        }

        return chat;
    }

    @Override
    public void addUserInGroupChat(String emailUser, int chatId) {
        User newUser = userRepository.findUserByEmail(emailUser);

        if (newUser == null)
            throw new RuntimeException("???????????????????????? ???? ????????????!");

        if (userRepository.findUserInChat(newUser, chatId))
            throw new RuntimeException("???????????????????????? ?????? ?????????????????? ?? ????????!");

        chatRepository.addUserToGroupChat(newUser.getId(), chatId);
    }

    @Override
    public void deleteUserInGroupChat(int chatID, int userId) {
        chatRepository.deleteUserInGroupChat(chatID, userId);
    }

    @Override
    public void deleteChat(int chatId) {
        Chat chat = chatRepository.findChatById(chatId);

        if (chat.getChatType().equals("group"))
            chatRepository.cleanAndDeleteChat(chatId);
        else
            chatRepository.cleanChat(chatId);
    }

    @Override
    public void deleteFriend(int idUser1, int idUser2) {
        userRepository.deleteFriend(idUser1, idUser2);
    }

    @Override
    public void makeUser(int userId) {
        userRepository.makeUser(userId);
    }

    @Override
    public void makeAdmin(int userId) {
        userRepository.makeAdmin(userId);
    }

    @Override
    public void ban(int userId) {
        userRepository.ban(userId);
    }

    @Override
    public void unBan(int userId) {
        userRepository.unBan(userId);
    }

    private void addChat(User user) {
        ArrayList<User> users = new ArrayList<>();
        users.add(user);

        chatRepository.addChat(users, "saved");
    }

    private void addChat(User user1, User user2) {
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        chatRepository.addChat(users, "private");
    }

    private Chat addChat(int user1, int user2) {
        List<User> users = new ArrayList<>();
        users.add(new User(user1));
        users.add(new User(user2));

        return chatRepository.addChat(users, "private");
    }

    private boolean isDataValidation(User user, String twoPassword) {
        if (user.getEmail().equals(""))
            throw new WrongEmailException("?????????????? email!");

        if (user.getName().equals(""))
            throw new RuntimeException("?????????????? ?????? ????????????????????????!");

        if (user.getPassword().equals(""))
            throw new PasswordMismatchException("???????????? ???? ?????????? ???????? ????????????!");

        if (userRepository.findUserByEmail(user.getEmail()) != null)
            throw new WrongEmailException("email ??????????");

        if (!user.getPassword().equals(twoPassword))
            throw new PasswordMismatchException("???????????? ???? ??????????????????");

        return true;
    }

    private void uploadFile(ChatMessage chatMessage, int id) {
        String urlFile = chatMessage.getNameFile().replaceAll("data:image/png;base64,", "");
        urlFile = urlFile.replaceAll("data:image/jpeg;base64,", "");
        InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(urlFile));
        ObjectMetadata metadata = null;
        String nameFile = stringGeneration();

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        uploadPath,
                        "ru-moscow")).build();

        try {
            s3.putObject(nameBucket, nameFile, inputStream, metadata);
            chatRepository.uploadFileInMessage(nameFile, id);

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

    private String downloadFile(String key_name) {
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

    private String stringGeneration() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 12; i++) {
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

        if (message.getIdAuthor().equals(user.getId())) {
            printMessage = new PrintMessage(true, message.getText(), date, message.getNameAuthor(), message.getMessageId(), false);
        } else {
            printMessage = new PrintMessage(false, message.getText(), date, message.getNameAuthor(), message.getMessageId(), false);
        }

        if (message.getNameFile() != null) {
            printMessage.setFile(true);
            printMessage.setNameFile(downloadFile(message.getNameFile()));
        }

        return printMessage;
    }

    private List<Chat> sortChatByTime(List<Chat> chats) {
        List<Chat> noTime = new ArrayList<>();
        List<Chat> haveTime = new ArrayList<>();

        for (Chat chat : chats) {
            if (chat.getLocalDateTimeLastMessage() == null) {
                chat.setDateLastMessage("");
                noTime.add(chat);
            } else {
                chat.setDateLastMessage(chat.getLocalDateTimeLastMessage().format(dateTimeFormatterDate));
                haveTime.add(chat);
            }
        }

        haveTime = haveTime.stream()
                .sorted((o1, o2) -> (int) (o2.getLocalDateTimeLastMessage().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() -
                        o1.getLocalDateTimeLastMessage().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                .collect(Collectors.toList());

        List<Chat> res = new ArrayList<>();
        res.addAll(haveTime);
        res.addAll(noTime);

        return res;
    }
}
