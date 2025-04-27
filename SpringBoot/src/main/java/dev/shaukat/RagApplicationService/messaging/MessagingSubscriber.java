package dev.shaukat.RagApplicationService.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import dev.shaukat.RagApplicationService.file.models.FileModel;
import dev.shaukat.RagApplicationService.file.FileService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MessagingSubscriber {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingSubscriber.class);

    private final Channel channel;
    private final FileService fileService;
    private ExecutorService executorService;

    @Value("${messaging.exchange.name}")
    private String exchangeName;

    @Value("${messaging.response.queue}")
    private String responseQueue;

    @Value("${messaging.response.queue.routing.key}")
    private String responseQueueRoutingKey;

    public MessagingSubscriber(Channel channel, FileService fileService) {
        this.channel = channel;
        this.fileService = fileService;
    }

    @PostConstruct
    public void startListener(){
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::startMessagingListener);
    }
    public void startMessagingListener(){

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody());
          LOGGER.info("Received message from Queue: {}.", message);
          List<FileModel> embdeddedFiles = new ObjectMapper().readValue(message, new TypeReference<List<FileModel>>() {});

          fileService.updateFileStatus(embdeddedFiles);

          // Acknowledge message is received.
          long deliveryTag = delivery.getEnvelope().getDeliveryTag();
          System.out.println("\nDelivery tag == > " + deliveryTag);

//          channel.basicAck(deliveryTag,false);
        };

        try {
            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            channel.queueDeclare(responseQueue,true,false, false, null);
            channel.queueBind(responseQueue,exchangeName, responseQueueRoutingKey);

            channel.basicConsume(responseQueue,true, deliverCallback, consumerTag -> {});

            LOGGER.info("Listening for queue messages...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
