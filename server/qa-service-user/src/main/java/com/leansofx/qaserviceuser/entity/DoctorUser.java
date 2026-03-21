package com.leansofx.qaserviceuser.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "doctor_user")
public class DoctorUser {

    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String title;

    @Column(length = 50)
    private String department;

    @Column(length = 500)
    private String avatar;

    @Column(length = 100)
    private String experience;

    @Column(length = 500)
    private String specialties;

    @Column(name = "is_active")
    private Boolean isActive;

    public DoctorUser() {
    }

    public DoctorUser(String id, String username, String password, String name, String title,
                      String department, String avatar, String experience, String specialties, Boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.title = title;
        this.department = department;
        this.avatar = avatar;
        this.experience = experience;
        this.specialties = specialties;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
