package br.com.suit.DLQ.driven.messaging.consumer;

import br.com.suit.DLQ.application.commands.RecordIncome;
import br.com.suit.DLQ.application.domain.FinancialProcessingException;
import br.com.suit.DLQ.application.ports.inbound.IncomeProcessingUseCase;
import br.com.suit.DLQ.application.ports.outbound.DeadLetterQueuePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class IncomeConsumer {

    private static final Logger logger = LoggerFactory.getLogger(IncomeConsumer.class);

    private final IncomeProcessingUseCase incomeProcessingUseCase;
    private final DeadLetterQueuePublisher deadLetterQueuePublisher;
    private final ObjectMapper objectMapper;

    public IncomeConsumer(IncomeProcessingUseCase incomeProcessingUseCase, 
                         DeadLetterQueuePublisher deadLetterQueuePublisher,
                         ObjectMapper objectMapper) {
        this.incomeProcessingUseCase = incomeProcessingUseCase;
        this.deadLetterQueuePublisher = deadLetterQueuePublisher;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.queues.income.name}")
    public void processIncomeMessage(String message) {
        logger.info("Received income message: {}", message);
        
        try {
            // Deserializar mensagem para comando
            RecordIncome recordIncome = objectMapper.readValue(message, RecordIncome.class);
            
            // Processar através do caso de uso
            incomeProcessingUseCase.processIncome(recordIncome);
            
            logger.info("Income message processed successfully: {}", recordIncome.getId());
            
        } catch (FinancialProcessingException e) {
            logger.error("Business logic error processing income message", e);
            deadLetterQueuePublisher.publishToIncomeDlq(message, e);
            
        } catch (Exception e) {
            logger.error("Unexpected error processing income message", e);
            deadLetterQueuePublisher.publishToIncomeDlq(message, e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queues.income.dlq}")
    public void processIncomeDlqMessage(String dlqMessage) {
        logger.warn("Received message in income DLQ: {}", dlqMessage);
        
        // Aqui você pode implementar lógica específica para processar mensagens da DLQ
        // Por exemplo: alertas, reprocessamento manual, persistência para análise, etc.
        
        try {
            // Log detalhado para análise
            logger.info("Processing DLQ message for manual review or reprocessing");
            
            // Você pode implementar aqui:
            // 1. Salvar em uma tabela de mensagens falhadas
            // 2. Enviar alerta para o time de operações
            // 3. Implementar lógica de reprocessamento com backoff
            // 4. Análise de padrões de falha
            
        } catch (Exception e) {
            logger.error("Error processing income DLQ message", e);
        }
    }
}