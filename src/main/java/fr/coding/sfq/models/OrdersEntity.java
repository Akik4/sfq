package fr.coding.sfq.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Column
    private Double priceProduction;

    @ManyToOne
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private TablesEntity table;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDishiesEntity> orderDishes = new ArrayList<>();

    public OrdersEntity() {
    }

    public OrdersEntity(Date date, boolean status, double price,TablesEntity table, double priceProduction) {
        this.date = date;
        this.status = status;
        this.price = price;
        this.table = table;
        this.priceProduction = priceProduction;
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

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setTable(double price) {
        this.table = table;
    }

    public TablesEntity getTable() {
        return table;
    }

    public double getPriceProduction() {
        return priceProduction;
    }
}
