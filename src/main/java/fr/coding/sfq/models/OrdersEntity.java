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
    private String dishId;

    @Column
    private Date date;

    public OrdersEntity() {
    }
    public OrdersEntity(String dishId, Date date) {
        this.dishId = dishId;
        this.date = date;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getDishId() {
        return dishId;
    }

    public Date getDate() {
        return date;
    }
}
