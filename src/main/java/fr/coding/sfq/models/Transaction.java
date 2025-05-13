package fr.coding.sfq.models;

import java.time.LocalDateTime;

public class Transaction {

    public enum Type {
        INCOME, EXPENSE
    }

    private final LocalDateTime timestamp;
    private final Type type;
    private final double amount;
    private final String description;

    public Transaction(LocalDateTime timestamp, Type type, double amount, String description) {
        this.timestamp = timestamp;
        this.type = type;
        this.amount = amount;
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
