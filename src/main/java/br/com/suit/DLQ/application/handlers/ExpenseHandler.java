package br.com.suit.DLQ.application.handlers;

import br.com.suit.DLQ.application.commands.RecordExpense;
import br.com.suit.DLQ.application.domain.Expense;
import br.com.suit.DLQ.application.domain.FinancialProcessingException;
import br.com.suit.DLQ.application.ports.inbound.ExpenseProcessingUseCase;
import br.com.suit.DLQ.application.ports.outbound.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExpenseHandler implements ExpenseProcessingUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpenseHandler.class);
    
    private final ExpenseRepository expenseRepository;

    public ExpenseHandler(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public void processExpense(RecordExpense recordExpense) {
        logger.info("Processing expense: {}", recordExpense);
        
        try {
            // Converter comando para entidade de domínio
            Expense expense = new Expense(
                recordExpense.getId(),
                recordExpense.getDescription(),
                recordExpense.getAmount(),
                recordExpense.getCategory(),
                recordExpense.getDateTime()
            );

            // Aplicar regras de negócio
            validateExpenseProcessing(expense);

            // Marcar como processado e salvar
            Expense processedExpense = expense.markAsProcessed();
            expenseRepository.save(processedExpense);
            
            logger.info("Expense processed successfully: {}", expense.getId());
            
        } catch (Exception e) {
            logger.error("Failed to process expense: {}", recordExpense.getId(), e);
            throw new FinancialProcessingException("Failed to process expense: " + recordExpense.getId(), e);
        }
    }

    private void validateExpenseProcessing(Expense expense) {
        // Simular falha se contém palavra "erro"
        if (expense.containsErrorKeyword()) {
            throw new FinancialProcessingException(
                "Expense processing failed due to error keyword in description or category: " + expense.getId()
            );
        }

        // Outras validações de negócio podem ser adicionadas aqui
        logger.debug("Expense validation passed for: {}", expense.getId());
    }
}