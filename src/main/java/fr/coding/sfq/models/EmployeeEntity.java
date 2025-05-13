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
    private String job;

    @Column
    private double hoursWorked;

    @Column
    private int age;

    public EmployeeEntity() {}
    public EmployeeEntity(String name, String job, double hoursWorked, int age) {
        this.name = name;
        this.job = job;
        this.hoursWorked = hoursWorked;
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJob(String job) {
        this.job = job;
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

    public String getJob() {
        return job;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public int getAge() {
        return age;
    }
}
