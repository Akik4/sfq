package fr.coding.sfq.models;

public class Dishes {
    private int ID;
    private String name;
    private String description;
    private int price;
    private String image;

    public Dishes(String name, String description, int price, String image) {
        this.ID = 1;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
}
