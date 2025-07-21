package br.com.suit.DLQ.application.ports.inbound;

import br.com.suit.DLQ.application.commands.RecordExpense;

public interface ExpenseProcessingUseCase {
    void processExpense(RecordExpense recordExpense);
}