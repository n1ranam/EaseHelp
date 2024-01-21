package com.example.easehelp.model;

public class VolunteerModel {
    String name,address,dob,phone,email,password;
    String idImageUrl;

    public VolunteerModel(String name, String address, String dob, String phone, String email, String password, String idImageUrl) {
        this.name = name;
        this.address = address;
        this.dob = dob;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.idImageUrl = idImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdImageUrl() {
        return idImageUrl;
    }

    public void setIdImageUrl(String idImageUrl) {
        this.idImageUrl = idImageUrl;
    }
}
