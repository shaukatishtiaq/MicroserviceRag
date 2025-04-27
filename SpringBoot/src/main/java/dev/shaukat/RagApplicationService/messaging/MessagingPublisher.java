package dev.shaukat.RagApplicationService.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MessagingPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingPublisher.class);

    private final Channel channel;

    public MessagingPublisher(Channel channel){this.channel = channel;}

    public <T> void publishMessage(String queueName, String exchangeName, String routingKey, T message){

        try {
            // Set up Queue and exchange.
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            channel.queueDeclare(queueName, true,false,false,null);
            channel.queueBind(queueName,exchangeName, routingKey);

            // Preprocess Message to transmission.
            String jsonMessage = new ObjectMapper().writeValueAsString(message);

            AMQP.BasicProperties props = new AMQP.BasicProperties()
                    .builder()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .deliveryMode(2) // Persistent.
                    .build();

            channel.basicPublish(exchangeName,routingKey,props,jsonMessage.getBytes());

        } catch (JsonProcessingException e){
            LOGGER.error("Couldn't serialize message: {}.", message, e);
        } catch (IOException e) {
            LOGGER.error("Error creating messaging queue channel: {}", exchangeName, e);
            throw new RuntimeException(e);
        }
    }
}
