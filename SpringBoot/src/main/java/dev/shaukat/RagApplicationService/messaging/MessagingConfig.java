package dev.shaukat.RagApplicationService.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import dev.shaukat.RagApplicationService.utils.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


@Configuration
public class MessagingConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConfig.class);

    @Value("${messaging.host}")
    private String messagingHost;

    @Value("${messaging.port}")
    private String messagingPort;

    @Value("${messaging.username}")
    private String messagingUsername;

    @Value("${messaging.password}")
    private String messagingPassword;


    @Bean
    public Connection messagingConnection(){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(messagingHost);
        factory.setPort(Integer.parseInt(messagingPort));
        factory.setUsername(messagingUsername);
        factory.setPassword(messagingPassword);

        LOGGER.info("Coonnecting to Messaging Queue with config:\nHost: {}\nPort: {}\nUsername: {}\nPassword: {}.", messagingHost, messagingPort, GeneralUtils.maskString(messagingUsername), GeneralUtils.maskString(messagingPassword));

        Connection connection;

        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            LOGGER.error("FAILED TO CONNECT TO MESSAGING QUEUE!!!",e);
            throw new RuntimeException(e);
        }

        LOGGER.info("CONNECTED TO MESSAGING QUEUE!");
        return connection;
    }

    @Bean
    public Channel messagingChannel(Connection connection){
        try {
            return connection.createChannel();
        } catch (IOException e) {
            LOGGER.error("ERROR ESTABLISHING MESSAGING QUEUE CHANNEL.",e);
            throw new RuntimeException(e);
        }
    }
}
