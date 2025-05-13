package fr.coding.sfq.models;

public class Employee {
    private int ID;
    private String name;
    private double workHours;
    private String position;

    public Employee(String name, double workHours, String position) {
        this.name = name;
        this.workHours = workHours;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public double getWorkHours() {
        return workHours;
    }

    public void setWorkHours(double workHours) {
        this.workHours = workHours;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
