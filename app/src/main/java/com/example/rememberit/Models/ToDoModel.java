package com.example.rememberit.Models;

public class ToDoModel {
    private int status;
    private String taskTitle, taskBody, id;

    public ToDoModel() {}

    public ToDoModel(String taskTitle, String taskBody, int status, String id) {
        this.taskTitle = taskTitle;
        this.taskBody = taskBody;
        this.status = status;
        this.id= id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskBody() {
        return taskBody;
    }

    public void setTaskBody(String taskBody) {
        this.taskBody = taskBody;
    }
}
