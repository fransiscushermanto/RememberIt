package com.example.rememberit.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.rememberit.MainActivity;
import com.example.rememberit.Models.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    private Context mContext;

    //DB VERSION
    private static final int VERSION = 1;

    //DB NAME
    private static final String NAME = "toDoListDatabase";

    //DB TABLES
    private static final String TODO_TABLE = "todo";
    private static final String TODO_BODY_TABLE = "todo_body";

    //DB COLUMNS
    private static final String TASK_ID = "task_id";
    private static final String TODO_BODY_ID = "todo_body_id";
    private static final String TASK_TITLE = "task_title";
    private static final String STATUS = "status";
    private static final String TASK_BODY = "task_body";
    private static final String DUE_DATE = "due_date";
    private static final String REMIND_ME = "remind_me";
    private static final String CREATED_AT = "created_at";
    private static final String FINISHED_AT = "finished_at";
    private static final String TASK_BODY_UPDATE_AT = "updated_at";

    //DB QUERIES
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + TASK_ID + " TEXT PRIMARY KEY, " +
            TASK_TITLE + " TEXT, " + TODO_BODY_ID + " TEXT, " + STATUS + " BOOLEAN, " + DUE_DATE + " DATE, " + REMIND_ME + " DATE, " + CREATED_AT + " DATE, " + FINISHED_AT + " DATE )";
    private static final String CREATE_TODO_BODY_TABLE = "CREATE TABLE " + TODO_BODY_TABLE + "(" + TODO_BODY_ID + " TEXT PRIMARY KEY, " + TASK_BODY + " TEXT, " + TASK_BODY_UPDATE_AT + " DATE )";

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TODO_TABLE);
        sqLiteDatabase.execSQL(CREATE_TODO_BODY_TABLE);
//        Log.d("~ ", CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //Drop the older table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TODO_BODY_TABLE);
        //Create table again
        onCreate(sqLiteDatabase);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_ID, task.getId());
        cv.put(TASK_TITLE, task.getTaskTitle());
        cv.put(TODO_BODY_ID, task.getTaskBody_id());
        cv.put(STATUS, false);
        cv.put(DUE_DATE, task.getDueDate());
        cv.put(REMIND_ME, task.getRemindMe());
        cv.put(CREATED_AT, task.getCreated_at());
        db.insert(TODO_TABLE, null, cv);
    }

    public void insertTaskBody(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TODO_BODY_ID, task.getTaskBody_id());
        cv.put(TASK_BODY, task.getTaskBody());
        cv.put(TASK_BODY_UPDATE_AT, task.getTaskBody_updated_at());
        db.insert(TODO_BODY_TABLE, null, cv);
    }

    public ToDoModel getTaskBody (String taskBody_id) {
        ToDoModel task = new ToDoModel();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = %s", TODO_BODY_TABLE, TODO_BODY_ID, taskBody_id), null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    task.setTaskBody_id(cur.getString(cur.getColumnIndex(TODO_BODY_ID)));
                    task.setTaskBody(cur.getString(cur.getColumnIndex(TASK_BODY)));
                    task.setTaskBody_updated_at(cur.getString(cur.getColumnIndex(TASK_BODY_UPDATE_AT)));
                }
            }
        }catch (Exception e) {
            Log.d("Error select", e.getMessage());
        }finally {
            db.endTransaction();
            cur.close();
        }
        return task;
    }

    public ToDoModel getTask (String task_id) {
        ToDoModel task = new ToDoModel();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.rawQuery(String.format("SELECT * FROM %s WHERE %s = %s", TODO_TABLE, TASK_ID, task_id), null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    task.setId(cur.getString(cur.getColumnIndex(TASK_ID)));
                    task.setTaskTitle(cur.getString(cur.getColumnIndex(TASK_TITLE)));
                    task.setTaskBody_id(cur.getString(cur.getColumnIndex(TODO_BODY_ID)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    task.setDueDate(cur.getString(cur.getColumnIndex(DUE_DATE)));
                    task.setRemindMe(cur.getString(cur.getColumnIndex(REMIND_ME)));
                    task.setCreated_at(cur.getString(cur.getColumnIndex(CREATED_AT)));
                    task.setFinished_at(cur.getString(cur.getColumnIndex(FINISHED_AT)));
                }
            }
        }catch (Exception e) {
            Log.d("Error select", e.getMessage());
        }finally {
            db.endTransaction();
            cur.close();
        }
        return task;
    }

    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getString(cur.getColumnIndex(TASK_ID)));
                        task.setTaskTitle(cur.getString(cur.getColumnIndex(TASK_TITLE)));
                        task.setTaskBody(cur.getString(cur.getColumnIndex(TODO_BODY_ID)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        task.setDueDate(cur.getString(cur.getColumnIndex(DUE_DATE)));
                        task.setRemindMe(cur.getString(cur.getColumnIndex(REMIND_ME)));
                        task.setCreated_at(cur.getString(cur.getColumnIndex(CREATED_AT)));
                        taskList.add(task);
                    } while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, task.getStatus());
        cv.put(FINISHED_AT, task.getFinished_at() == "" ? null : task.getFinished_at());
        db.update(TODO_TABLE, cv, TASK_ID + "=?", new String[]{task.getId()});
    }

    public void updateTask(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, task.getTaskTitle());
        cv.put(DUE_DATE, task.getDueDate());
        cv.put(REMIND_ME, task.getRemindMe());
        cv.put(TODO_BODY_ID, task.getTaskBody_id().isEmpty() ? null : task.getTaskBody_id());
        db.update(TODO_TABLE, cv, TASK_ID + "=?", new String[]{task.getId()});
    }

    public void updateTaskBody(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_BODY, task.getTaskBody());
        cv.put(TASK_BODY_UPDATE_AT, task.getTaskBody_updated_at());
        db.update(TODO_BODY_TABLE, cv, TODO_BODY_ID + "=?", new String[]{task.getTaskBody_id()});
    }

    public void deleteTodoBody(String taskBody_id) {
        db.delete(TODO_BODY_TABLE, TODO_BODY_ID + "=?", new String[]{taskBody_id});
    }

    public void deleteTask(String id) {
        db.delete(TODO_TABLE, TASK_ID + "=?", new String[]{id});
    }
}
