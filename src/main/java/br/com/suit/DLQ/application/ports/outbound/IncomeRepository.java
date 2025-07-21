package br.com.suit.DLQ.application.ports.outbound;

import br.com.suit.DLQ.application.domain.Income;

public interface IncomeRepository {
    void save(Income income);
}