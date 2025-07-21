package br.com.suit.DLQ.application.ports.outbound;

public interface DeadLetterQueuePublisher {
    void publishToExpenseDlq(String message, Exception exception);
    void publishToIncomeDlq(String message, Exception exception);
}