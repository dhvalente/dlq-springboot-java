package br.com.suit.DLQ.driven.messaging.publisher;

import br.com.suit.DLQ.application.ports.outbound.DeadLetterQueuePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitDeadLetterQueuePublisher implements DeadLetterQueuePublisher {

    private static final Logger logger = LoggerFactory.getLogger(RabbitDeadLetterQueuePublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.queues.expense.exchange}")
    private String expenseExchangeName;

    @Value("${rabbitmq.queues.expense.dlq-routing-key}")
    private String expenseDlqRoutingKey;

    @Value("${rabbitmq.queues.income.exchange}")
    private String incomeExchangeName;

    @Value("${rabbitmq.queues.income.dlq-routing-key}")
    private String incomeDlqRoutingKey;

    public RabbitDeadLetterQueuePublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishToExpenseDlq(String message, Exception exception) {
        logger.warn("Publishing message to expense DLQ due to processing failure", exception);
        
        Map<String, Object> dlqMessage = createDlqMessage(message, exception, "EXPENSE");
        
        try {
            String jsonMessage = objectMapper.writeValueAsString(dlqMessage);
            rabbitTemplate.convertAndSend(expenseExchangeName, expenseDlqRoutingKey, jsonMessage);
            logger.info("Message successfully published to expense DLQ");
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize DLQ message for expense", e);
        } catch (Exception e) {
            logger.error("Failed to publish message to expense DLQ", e);
        }
    }

    @Override
    public void publishToIncomeDlq(String message, Exception exception) {
        logger.warn("Publishing message to income DLQ due to processing failure", exception);
        
        Map<String, Object> dlqMessage = createDlqMessage(message, exception, "INCOME");
        
        try {
            String jsonMessage = objectMapper.writeValueAsString(dlqMessage);
            rabbitTemplate.convertAndSend(incomeExchangeName, incomeDlqRoutingKey, jsonMessage);
            logger.info("Message successfully published to income DLQ");
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize DLQ message for income", e);
        } catch (Exception e) {
            logger.error("Failed to publish message to income DLQ", e);
        }
    }

    private Map<String, Object> createDlqMessage(String originalMessage, Exception exception, String messageType) {
        Map<String, Object> dlqMessage = new HashMap<>();
        dlqMessage.put("originalMessage", originalMessage);
        dlqMessage.put("errorMessage", exception.getMessage());
        dlqMessage.put("errorType", exception.getClass().getSimpleName());
        dlqMessage.put("messageType", messageType);
        dlqMessage.put("timestamp", LocalDateTime.now().toString());
        dlqMessage.put("retryCount", 0);
        
        return dlqMessage;
    }
}