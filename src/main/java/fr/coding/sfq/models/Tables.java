package fr.coding.sfq.models;

import java.util.List;

public class Tables {
    private int ID;
    private int size;
    private String location;
    private boolean occupied;
    private List<Orders> orders;

    public Tables(int size, String location, boolean occupied, List<Orders> orders) {
        this.size = size;
        this.location = location;
        this. occupied = occupied;
        this.orders = orders;
    }

    public int getID() {
        return ID;
    }

    public int getSize() {
        return size;
    }

    public String getLocation() {
        return location;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void addOrder(Orders order) {
        this.orders.add(order);
    }
}
