package fr.coding.sfq.models;

import java.util.Date;
import java.util.List;

public class Orders {
    private int ID;
    private Date date;
    private List<Dishes> dishes;

    public Orders(Date date, List<Dishes> dishes) {
        this.date = date;
        this.dishes = dishes;
    }

    public Date getDate() {
        return date;
    }

    public List<Dishes> getDishes() {
        return dishes;
    }

    public void addDish(Dishes dish) {
        this.dishes.add(dish);
    }
}
