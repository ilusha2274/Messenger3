package com.messenger30.Messenger30.spring;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.messenger30.Messenger30.repository.ChatRepository;
import com.messenger30.Messenger30.repository.DatabaseChatRepository;
import com.messenger30.Messenger30.repository.DatabaseUserRepository;
import com.messenger30.Messenger30.repository.UserRepository;
import com.messenger30.Messenger30.services.IMessengerService;
import com.messenger30.Messenger30.services.MessengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@ComponentScan(value = "controller")
@EnableWebMvc
public class SpringConfig implements WebMvcConfigurer {

    @Value("${access.key.id}")
    private String accessKey;

    @Value("${secret.access.key}")
    private String secretKey;

    private final ApplicationContext applicationContext;

    @Autowired
    public SpringConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Bean
    public UserRepository userRepository(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return new DatabaseUserRepository(jdbcTemplate, passwordEncoder, transactionTemplate());
    }

    @Bean
    public ChatRepository chatRepository(JdbcTemplate jdbcTemplate) {
        return new DatabaseChatRepository(jdbcTemplate, transactionTemplate());
    }

    @Bean
    public UserDetailsService userDetailsService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return new DatabaseUserRepository(jdbcTemplate, passwordEncoder, transactionTemplate());
    }

    @Bean
    public IMessengerService messengerService(ChatRepository chatRepository, UserRepository userRepository, AWSCredentials credentials) {
        return new MessengerService(chatRepository, userRepository, credentials);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl("jdbc:postgresql://localhost:17171/test_db");
//        dataSource.setUsername("ilya_sh");
//        dataSource.setPassword("Ioio14789");

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/first_bd");
        dataSource.setUsername("postgres");
        dataSource.setPassword("14789");

        return dataSource;
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(100000);
        return multipartResolver;
    }

    @Bean
    public AWSCredentials credentials() {
        AWSCredentials credentials = new BasicAWSCredentials(
                accessKey,
                secretKey
        );
        return credentials;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        return new TransactionTemplate(transactionManager());
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JdbcTransactionManager(dataSource());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationManager() {

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return authentication;
            }
        };
    }


}
