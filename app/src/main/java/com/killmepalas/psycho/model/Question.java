package com.killmepalas.psycho.model;

public class Question {
    private String id;
    private String name;
    private String testId;

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

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

    public Question(String id, String name, String testId) {
        this.id = id;
        this.name = name;
        this.testId = testId;
    }

    public Question() {
    }

    public Question(String name, String testId) {
        this.name = name;
        this.testId = testId;
    }
}
