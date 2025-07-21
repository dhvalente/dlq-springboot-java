package br.com.suit.DLQ.application.handlers;

import br.com.suit.DLQ.application.commands.RecordIncome;
import br.com.suit.DLQ.application.domain.Income;
import br.com.suit.DLQ.application.domain.FinancialProcessingException;
import br.com.suit.DLQ.application.ports.inbound.IncomeProcessingUseCase;
import br.com.suit.DLQ.application.ports.outbound.IncomeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IncomeHandler implements IncomeProcessingUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(IncomeHandler.class);
    
    private final IncomeRepository incomeRepository;

    public IncomeHandler(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Override
    public void processIncome(RecordIncome recordIncome) {
        logger.info("Processing income: {}", recordIncome);
        
        try {
            // Converter comando para entidade de domínio
            Income income = new Income(
                recordIncome.getId(),
                recordIncome.getDescription(),
                recordIncome.getAmount(),
                recordIncome.getSource(),
                recordIncome.getDateTime()
            );

            // Aplicar regras de negócio
            validateIncomeProcessing(income);

            // Marcar como processado e salvar
            Income processedIncome = income.markAsProcessed();
            incomeRepository.save(processedIncome);
            
            logger.info("Income processed successfully: {}", income.getId());
            
        } catch (Exception e) {
            logger.error("Failed to process income: {}", recordIncome.getId(), e);
            throw new FinancialProcessingException("Failed to process income: " + recordIncome.getId(), e);
        }
    }

    private void validateIncomeProcessing(Income income) {
        // Simular falha se contém palavra "erro"
        if (income.containsErrorKeyword()) {
            throw new FinancialProcessingException(
                "Income processing failed due to error keyword in description or source: " + income.getId()
            );
        }

        // Outras validações de negócio podem ser adicionadas aqui
        logger.debug("Income validation passed for: {}", income.getId());
    }
}