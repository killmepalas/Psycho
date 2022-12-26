package com.killmepalas.psycho.model;

public class Grade {
    private String id;
    private String userId;
    private String testId;
    private Long grade;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }

    public Grade() {
    }

    public Grade(String userId, String testId, Long grade) {
        this.userId = userId;
        this.testId = testId;
        this.grade = grade;
    }
}
