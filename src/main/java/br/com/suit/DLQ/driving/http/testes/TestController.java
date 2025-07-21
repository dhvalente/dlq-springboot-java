package br.com.suit.DLQ.driving.http.testes;

import br.com.suit.DLQ.application.commands.RecordExpense;
import br.com.suit.DLQ.application.commands.RecordIncome;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbitmq.queues.expense.exchange}")
    private String expenseExchangeName;

    @Value("${rabbitmq.queues.expense.routing-key}")
    private String expenseRoutingKey;

    @Value("${rabbitmq.queues.income.exchange}")
    private String incomeExchangeName;

    @Value("${rabbitmq.queues.income.routing-key}")
    private String incomeRoutingKey;

    public TestController(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/expense/success")
    public ResponseEntity<String> testExpenseSuccess() {
        try {
            String expenseId = UUID.randomUUID().toString();
            RecordExpense recordExpense = new RecordExpense(
                    expenseId,
                    "Teste de despesa bem-sucedida",
                    new BigDecimal("100.00"),
                    "Teste",
                    LocalDateTime.now()
            );

            String message = objectMapper.writeValueAsString(recordExpense);
            rabbitTemplate.convertAndSend(expenseExchangeName, expenseRoutingKey, message);

            return ResponseEntity.ok("Expense test message sent: " + expenseId);
        } catch (Exception e) {
            logger.error("Error sending test expense message", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/expense/error")
    public ResponseEntity<String> testExpenseError() {
        try {
            String expenseId = UUID.randomUUID().toString();
            RecordExpense recordExpense = new RecordExpense(
                    expenseId,
                    "Teste de despesa com erro para DLQ",
                    new BigDecimal("50.00"),
                    "TestError",
                    LocalDateTime.now()
            );

            String message = objectMapper.writeValueAsString(recordExpense);
            rabbitTemplate.convertAndSend(expenseExchangeName, expenseRoutingKey, message);

            return ResponseEntity.ok("Expense error test message sent: " + expenseId);
        } catch (Exception e) {
            logger.error("Error sending test expense error message", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/income/success")
    public ResponseEntity<String> testIncomeSuccess() {
        try {
            String incomeId = UUID.randomUUID().toString();
            RecordIncome recordIncome = new RecordIncome(
                    incomeId,
                    "Teste de receita bem-sucedida",
                    new BigDecimal("200.00"),
                    "TestSource",
                    LocalDateTime.now()
            );

            String message = objectMapper.writeValueAsString(recordIncome);
            rabbitTemplate.convertAndSend(incomeExchangeName, incomeRoutingKey, message);

            return ResponseEntity.ok("Income test message sent: " + incomeId);
        } catch (Exception e) {
            logger.error("Error sending test income message", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/income/error")
    public ResponseEntity<String> testIncomeError() {
        try {
            String incomeId = UUID.randomUUID().toString();
            RecordIncome recordIncome = new RecordIncome(
                    incomeId,
                    "Teste de receita com erro para DLQ",
                    new BigDecimal("75.00"),
                    "ErrorSource",
                    LocalDateTime.now()
            );

            String message = objectMapper.writeValueAsString(recordIncome);
            rabbitTemplate.convertAndSend(incomeExchangeName, incomeRoutingKey, message);

            return ResponseEntity.ok("Income error test message sent: " + incomeId);
        } catch (Exception e) {
            logger.error("Error sending test income error message", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "description", "DLQ Test Application is running",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}