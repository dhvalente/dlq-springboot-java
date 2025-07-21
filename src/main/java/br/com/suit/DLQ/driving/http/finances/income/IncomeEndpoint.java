package br.com.suit.DLQ.driving.http.finances.income;

import br.com.suit.DLQ.application.commands.RecordIncome;
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
@RequestMapping("/api/incomes")
public class IncomeEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(IncomeEndpoint.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.queues.income.exchange}")
    private String incomeExchangeName;

    @Value("${rabbitmq.queues.income.routing-key}")
    private String incomeRoutingKey;

    public IncomeEndpoint(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> recordIncome(@Valid @RequestBody Request request) {
        logger.info("Received income request: {}", request);

        try {
            // Criar comando com ID único
            String incomeId = UUID.randomUUID().toString();
            RecordIncome recordIncome = new RecordIncome(
                    incomeId,
                    request.getDescription(),
                    request.getAmount(),
                    request.getSource(),
                    LocalDateTime.now()
            );

            // Serializar e enviar para a fila
            String message = objectMapper.writeValueAsString(recordIncome);
            rabbitTemplate.convertAndSend(incomeExchangeName, incomeRoutingKey, message);

            logger.info("Income message sent to queue: {}", incomeId);
            return ResponseEntity.ok("Income recorded with ID: " + incomeId);

        } catch (Exception e) {
            logger.error("Error sending income message to queue", e);
            return ResponseEntity.internalServerError()
                    .body("Error recording income: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Income endpoint is healthy");
    }
}