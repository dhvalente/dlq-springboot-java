package br.com.suit.DLQ.application.ports.outbound;

import br.com.suit.DLQ.application.domain.Expense;

public interface ExpenseRepository {
    void save(Expense expense);
}