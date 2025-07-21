package br.com.suit.DLQ.application.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Expense {
    private final String id;
    private final String description;
    private final BigDecimal amount;
    private final String category;
    private final LocalDateTime dateTime;
    private final ExpenseStatus status;

    public Expense(String id, String description, BigDecimal amount, String category, LocalDateTime dateTime) {
        this(id, description, amount, category, dateTime, ExpenseStatus.PENDING);
    }

    public Expense(String id, String description, BigDecimal amount, String category, LocalDateTime dateTime, ExpenseStatus status) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.category = Objects.requireNonNull(category, "Category cannot be null");
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
               category.toLowerCase().contains("erro") ||
               category.toLowerCase().contains("error");
    }

    public Expense markAsProcessed() {
        return new Expense(id, description, amount, category, dateTime, ExpenseStatus.PROCESSED);
    }

    public Expense markAsFailed() {
        return new Expense(id, description, amount, category, dateTime, ExpenseStatus.FAILED);
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

    public String getCategory() {
        return category;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public ExpenseStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", dateTime=" + dateTime +
                ", status=" + status +
                '}';
    }
}