package com.killmepalas.psycho.model;

public class Test {
    private String id;
    private String name;
    private String description;
    private String psychologistId;
    private boolean isOpen;

    public Test() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public Test(String name, String description, String psychologistId, boolean isOpen) {
        this.name = name;
        this.description = description;
        this.psychologistId = psychologistId;
        this.isOpen = isOpen;
    }

    public Test(String name, String description, boolean isOpen) {
        this.name = name;
        this.description = description;
        this.isOpen = isOpen;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPsychologistId(String psychologist_id) {
        this.psychologistId = psychologist_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPsychologistId() {
        return psychologistId;
    }
}
