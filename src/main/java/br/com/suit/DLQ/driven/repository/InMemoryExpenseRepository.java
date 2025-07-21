package br.com.suit.DLQ.driven.repository;

import br.com.suit.DLQ.application.domain.Expense;
import br.com.suit.DLQ.application.ports.outbound.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryExpenseRepository implements ExpenseRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(InMemoryExpenseRepository.class);
    
    private final Map<String, Expense> expenses = new ConcurrentHashMap<>();

    @Override
    public void save(Expense expense) {
        logger.info("Saving expense: {}", expense.getId());
        expenses.put(expense.getId(), expense);
        logger.debug("Expense saved successfully. Total expenses: {}", expenses.size());
    }

    public Expense findById(String id) {
        return expenses.get(id);
    }

    public Map<String, Expense> findAll() {
        return Map.copyOf(expenses);
    }

    public void clear() {
        expenses.clear();
    }
}