package com.killmepalas.psycho.model;

public class Question {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Question(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Question() {
    }

    public Question(String name) {
        this.name = name;
    }
}
