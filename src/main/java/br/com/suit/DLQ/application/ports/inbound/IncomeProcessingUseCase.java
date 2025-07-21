package br.com.suit.DLQ.application.ports.inbound;

import br.com.suit.DLQ.application.commands.RecordIncome;

public interface IncomeProcessingUseCase {
    void processIncome(RecordIncome recordIncome);
}