package fr.coding.sfq.models;

import jakarta.persistence.*;
import org.checkerframework.checker.units.qual.C;

@Entity
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private double hoursWorked;

    @Column
    private String position;

    @Column
    private Integer age;

    public EmployeeEntity() {}
    public EmployeeEntity(String name, double hoursWorked, String position, int age) {
        this.name = name;
        this.hoursWorked = hoursWorked;
        this.position = position;
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public double getHoursWorked() {
        return hoursWorked;
    }

    public Integer getAge() {
        return age;
    }
}
