package br.com.suit.DLQ.driven.messaging.consumer;

import br.com.suit.DLQ.application.commands.RecordExpense;
import br.com.suit.DLQ.application.domain.FinancialProcessingException;
import br.com.suit.DLQ.application.ports.inbound.ExpenseProcessingUseCase;
import br.com.suit.DLQ.application.ports.outbound.DeadLetterQueuePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ExpenseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseConsumer.class);

    private final ExpenseProcessingUseCase expenseProcessingUseCase;
    private final DeadLetterQueuePublisher deadLetterQueuePublisher;
    private final ObjectMapper objectMapper;

    public ExpenseConsumer(ExpenseProcessingUseCase expenseProcessingUseCase, 
                          DeadLetterQueuePublisher deadLetterQueuePublisher,
                          ObjectMapper objectMapper) {
        this.expenseProcessingUseCase = expenseProcessingUseCase;
        this.deadLetterQueuePublisher = deadLetterQueuePublisher;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${rabbitmq.queues.expense.name}")
    public void processExpenseMessage(String message) {
        logger.info("Received expense message: {}", message);
        
        try {
            // Deserializar mensagem para comando
            RecordExpense recordExpense = objectMapper.readValue(message, RecordExpense.class);
            
            // Processar através do caso de uso
            expenseProcessingUseCase.processExpense(recordExpense);
            
            logger.info("Expense message processed successfully: {}", recordExpense.getId());
            
        } catch (FinancialProcessingException e) {
            logger.error("Business logic error processing expense message", e);
            deadLetterQueuePublisher.publishToExpenseDlq(message, e);
            
        } catch (Exception e) {
            logger.error("Unexpected error processing expense message", e);
            deadLetterQueuePublisher.publishToExpenseDlq(message, e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queues.expense.dlq}")
    public void processExpenseDlqMessage(String dlqMessage) {
        logger.warn("Received message in expense DLQ: {}", dlqMessage);
        
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
            logger.error("Error processing expense DLQ message", e);
        }
    }
}