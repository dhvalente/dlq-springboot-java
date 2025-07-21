package br.com.suit.DLQ.driven.repository;

import br.com.suit.DLQ.application.domain.Income;
import br.com.suit.DLQ.application.ports.outbound.IncomeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryIncomeRepository implements IncomeRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(InMemoryIncomeRepository.class);
    
    private final Map<String, Income> incomes = new ConcurrentHashMap<>();

    @Override
    public void save(Income income) {
        logger.info("Saving income: {}", income.getId());
        incomes.put(income.getId(), income);
        logger.debug("Income saved successfully. Total incomes: {}", incomes.size());
    }

    public Income findById(String id) {
        return incomes.get(id);
    }

    public Map<String, Income> findAll() {
        return Map.copyOf(incomes);
    }

    public void clear() {
        incomes.clear();
    }
}