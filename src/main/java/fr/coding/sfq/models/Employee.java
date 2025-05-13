package fr.coding.sfq.models;

public class Employee {
    private int ID;
    private String name;
    private double workHours;
    private String position;
    private Integer age;

    public Employee(String name, double workHours, String position, int age) {
        this.name = name;
        this.workHours = workHours;
        this.position = position;
        this.age = age;
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

    public Integer getAge() {
        return age;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
