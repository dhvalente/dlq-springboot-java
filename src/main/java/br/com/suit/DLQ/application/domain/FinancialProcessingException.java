package br.com.suit.DLQ.application.domain;

public class FinancialProcessingException extends RuntimeException {
    
    public FinancialProcessingException(String message) {
        super(message);
    }
    
    public FinancialProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}