package br.com.suit.DLQ.driving.http.finances.expense;

import br.com.suit.DLQ.application.commands.RecordExpense;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(ExpenseEndpoint.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.queues.expense.exchange}")
    private String expenseExchangeName;

    @Value("${rabbitmq.queues.expense.routing-key}")
    private String expenseRoutingKey;

    public ExpenseEndpoint(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> recordExpense(@Valid @RequestBody Request request) {
        logger.info("Received expense request: {}", request);

        try {
            // Criar comando com ID único
            String expenseId = UUID.randomUUID().toString();
            RecordExpense recordExpense = new RecordExpense(
                    expenseId,
                    request.getDescription(),
                    request.getAmount(),
                    request.getCategory(),
                    LocalDateTime.now()
            );

            // Serializar e enviar para a fila
            String message = objectMapper.writeValueAsString(recordExpense);
            rabbitTemplate.convertAndSend(expenseExchangeName, expenseRoutingKey, message);

            logger.info("Expense message sent to queue: {}", expenseId);
            return ResponseEntity.ok("Expense recorded with ID: " + expenseId);

        } catch (Exception e) {
            logger.error("Error sending expense message to queue", e);
            return ResponseEntity.internalServerError()
                    .body("Error recording expense: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Expense endpoint is healthy");
    }
}