package br.com.suit.DLQ.application.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class RecordIncome {
    private final String id;
    private final String description;
    private final BigDecimal amount;
    private final String source;
    private final LocalDateTime dateTime;

    @JsonCreator
    public RecordIncome(@JsonProperty("id") String id, 
                       @JsonProperty("description") String description, 
                       @JsonProperty("amount") BigDecimal amount, 
                       @JsonProperty("source") String source, 
                       @JsonProperty("dateTime") LocalDateTime dateTime) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.source = Objects.requireNonNull(source, "Source cannot be null");
        this.dateTime = Objects.requireNonNull(dateTime, "DateTime cannot be null");
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordIncome that = (RecordIncome) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RecordIncome{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", source='" + source + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}