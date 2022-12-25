package com.killmepalas.psycho.model;

public class Answer {
    private String id;
    private String name;
    private boolean isRight;
    private String qId;

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

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }


    public Answer(String name, boolean isRight, String qId) {
        this.name = name;
        this.isRight = isRight;
        this.qId = qId;
    }

    public Answer() {
    }


}
