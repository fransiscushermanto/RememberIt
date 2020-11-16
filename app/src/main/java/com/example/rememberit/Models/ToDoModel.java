package com.example.rememberit.Models;

import java.util.Calendar;

public class ToDoModel {
    private int status;
    private String taskTitle, taskBody, id, dueDate, remindMe;

    public ToDoModel() {}

    public ToDoModel(String taskTitle, String taskBody, int status, String id, String dueDate, String remindMe) {
        this.taskTitle = taskTitle;
        this.taskBody = taskBody;
        this.status = status;
        this.id= id;
        this.dueDate = dueDate;
        this.remindMe = remindMe;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getRemindMe() {
        return remindMe;
    }

    public void setRemindMe(String remindMe) {
        this.remindMe = remindMe;
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
