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
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK_TITLE = "task_title";
    private static final String STATUS = "status";
    private static final String TASK_BODY = "task_body";
    private static final String DUE_DATE = "due_date";
    private static final String REMIND_ME = "remind_me";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " TEXT PRIMARY KEY, " +
            TASK_TITLE + " TEXT, " + TASK_BODY + " TEXT, " + STATUS + " BOOLEAN, " + DUE_DATE + " DATE, " + REMIND_ME + " DATE )";
    private SQLiteDatabase db;
    private Context mContext;


    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TODO_TABLE);
        Log.d("~ ", CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //Drop the older table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        //Create table again
        onCreate(sqLiteDatabase);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(ID, task.getId());
        cv.put(TASK_TITLE, task.getTaskTitle());
        cv.put(TASK_BODY, task.getTaskBody());
        cv.put(STATUS, false);
        cv.put(DUE_DATE, String.valueOf(task.getDueDate()));
        cv.put(REMIND_ME, String.valueOf(task.getRemindMe()));
        db.insert(TODO_TABLE, null, cv);
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
                        task.setId(cur.getString(cur.getColumnIndex(ID)));
                        task.setTaskTitle(cur.getString(cur.getColumnIndex(TASK_TITLE)));
                        task.setTaskBody(cur.getString(cur.getColumnIndex(TASK_BODY)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        task.setDueDate(cur.getString(cur.getColumnIndex(DUE_DATE)));
                        task.setRemindMe(cur.getString(cur.getColumnIndex(REMIND_ME)));
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

    public void updateStatus(String id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "=?", new String[]{id});
        mContext.sendBroadcast(new Intent(MainActivity.DATABASE_CHANGED));
    }

    public void updateTask(String id, String task_title, String task_body) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, task_title);
        cv.put(TASK_BODY, task_body);
        db.update(TODO_TABLE, cv, ID + "=?", new String[]{id});
    }

    public void deleteTask(String id) {
        db.delete(TODO_TABLE, ID + "=?", new String[]{id});
    }
}
