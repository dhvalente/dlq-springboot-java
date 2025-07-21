package br.com.suit.DLQ.application.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Income {
    private final String id;
    private final String description;
    private final BigDecimal amount;
    private final String source;
    private final LocalDateTime dateTime;
    private final IncomeStatus status;

    public Income(String id, String description, BigDecimal amount, String source, LocalDateTime dateTime) {
        this(id, description, amount, source, dateTime, IncomeStatus.PENDING);
    }

    public Income(String id, String description, BigDecimal amount, String source, LocalDateTime dateTime, IncomeStatus status) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.dateTime = Objects.requireNonNull(dateTime, "DateTime cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        
        validateBusinessRules();
    }

    private void validateBusinessRules() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        if (description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
    }

    public boolean containsErrorKeyword() {
        return description.toLowerCase().contains("erro") || 
               description.toLowerCase().contains("error") ||
               source.toLowerCase().contains("erro") ||
               source.toLowerCase().contains("error");
    }

    public Income markAsProcessed() {
        return new Income(id, description, amount, source, dateTime, IncomeStatus.PROCESSED);
    }

    public Income markAsFailed() {
        return new Income(id, description, amount, source, dateTime, IncomeStatus.FAILED);
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getSource() {
        return source;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public IncomeStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Income income = (Income) o;
        return Objects.equals(id, income.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Income{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", source='" + source + '\'' +
                ", dateTime=" + dateTime +
                ", status=" + status +
                '}';
    }
}