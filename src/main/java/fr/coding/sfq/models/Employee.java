package fr.coding.sfq.models;

public class Employee {
    private int ID;
    private String name;
    private double workHours;
    private int age;

    public Employee(String name, double workHours, int age) {
        this.name = name;
        this.workHours = workHours;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public double getWorkHours() {
        return workHours;
    }

    public int getAge() {
        return age;
    }
}
