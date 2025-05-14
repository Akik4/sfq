package fr.coding.sfq.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tables")
public class TablesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = false, nullable = false)
    private int size;

    @Column(unique = true, nullable = false)
    private String location;

    @Column(unique = false, nullable = false)
    private boolean occupied;
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private OrdersEntity order;

    public TablesEntity() {
    }

    public TablesEntity(int size, String location, boolean occupied) {
        this.size = size;
        this.location = location;
        this.occupied = occupied;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public String getLocation() {
        return location;
    }

    public OrdersEntity getOrder() {
        return order;
    }

    public boolean isOccupied() {
        return occupied;
    }
    public void setOrder(OrdersEntity order) {
        this.order = order;
    }
}
