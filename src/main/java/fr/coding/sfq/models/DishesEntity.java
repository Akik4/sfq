package fr.coding.sfq.models;

import jakarta.persistence.*;

@Entity
@Table(name = "dishes")
public class DishesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "priceProduction",nullable = true )
    private Double priceProduction;

    @Column(name = "imageURL", columnDefinition = "LONGTEXT")
    private String imageURL;

    public DishesEntity() {}

    public DishesEntity(String name, String description, double price, String imageURL) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.priceProduction = price/4;
        this.imageURL = imageURL;
    }

    public int getId() { return id; }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public double getPrice() {
        return price;
    }
    public double getPriceProduction() {return priceProduction;}
    public String getImageURL() {return imageURL;}

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}