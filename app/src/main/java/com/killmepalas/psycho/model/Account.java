package com.killmepalas.psycho.model;

import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Account {
    private String id;
    private String email;
    private String password;
    private String name;
    private String lastname;
    private String middlename;
    private List<String> role = new ArrayList<>();
    private Long age;
    private String status;
    private boolean assign;

    public boolean isAssign() {
        return assign;
    }

    public void setAssign(boolean assign) {
        this.assign = assign;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public List getRole() {
        return role;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public void setRole(String role) {

        this.role.add(role);
    }


    public Account() {
    }

    public Account(String id, String email, String password, String name, String lastname, String middlename, String role, Long age, String status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.middlename = middlename;
        this.role.add(role);
        this.age = age;
        this.status = status;
    }
}
