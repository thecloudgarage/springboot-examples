package com.example.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {
    private int id;
    private String firstName;
    private String lastName;
	private String city;
	private String email;
	private String phone;
	private String monthavailable;

    public User() {
    }

    public User(int id, String firstName, String lastName, String city, String email, String phone, String monthavailable) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
		this.city = city;
		this.email = email;
		this.phone = phone;
		this.monthavailable = monthavailable;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getcity() {
        return city;
    }

    public void setcity(String city) {
        this.city = city;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getphone() {
        return phone;
    }

    public void setphone(String phone) {
        this.phone = phone;
    }
	
    public String getmonthavailable() {
        return monthavailable;
    }

    public void setmonthavailable(String monthavailable) {
        this.monthavailable = monthavailable;
    }
}
