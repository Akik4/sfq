package fr.coding.sfq.models;

import jakarta.persistence.*;

import java.time.LocalDate;
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
    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }
    public Date getDate() {
        return date;
    }
}
