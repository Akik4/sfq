package fr.coding.sfq.models;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "orders")
public class OrdersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private Date date;

    @Column
    private boolean status;

    @Column
    private double price;

    public OrdersEntity() {
    }

    public OrdersEntity(Date date, boolean status, double price) {
        this.date = date;
        this.status = status;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public boolean getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }
}
