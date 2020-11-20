package com.example.rememberit.Models;

import java.util.Calendar;

public class ToDoModel {
    private int status;
    private String taskTitle, taskBody, id, dueDate, remindMe, created_at, taskBody_id, taskBody_updated_at, finished_at;

    public ToDoModel() {}

    public ToDoModel(String taskTitle, String taskBody_id, int status, String id, String dueDate, String remindMe) {
        this.taskTitle = taskTitle;
        this.taskBody_id = taskBody_id;
        this.status = status;
        this.id= id;
        this.dueDate = dueDate;
        this.remindMe = remindMe;
    }

    public ToDoModel(String taskBody_id) {
        this.taskBody_id = taskBody_id;
    }

    public String getTaskBody_updated_at() {
        return taskBody_updated_at;
    }

    public void setTaskBody_updated_at(String taskBody_updated_at) {
        this.taskBody_updated_at = taskBody_updated_at;
    }

    public String getFinished_at() {
        return finished_at;
    }

    public void setFinished_at(String finished_at) {
        this.finished_at = finished_at;
    }

    public String getTaskBody_id() {
        return taskBody_id;
    }

    public void setTaskBody_id(String taskBody_id) {
        this.taskBody_id = taskBody_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
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
