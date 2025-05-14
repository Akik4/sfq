package fr.coding.sfq.models;

import jakarta.persistence.*;

@Entity
@Table(name = "order_dishes")
public class OrderDishiesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "dish_id", referencedColumnName = "id")
    private DishesEntity dish;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private OrdersEntity order;

    public OrderDishiesEntity(int id, DishesEntity dish, OrdersEntity order) {
        this.id = id;
        this.dish = dish;
        this.order = order;
    }
    public OrderDishiesEntity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DishesEntity getDish() {
        return dish;
    }

    public void setDish(DishesEntity dish) {
        this.dish = dish;
    }

    public OrdersEntity getOrder() {
        return order;
    }

    public void setOrder(OrdersEntity order) {
        this.order = order;
    }
}
