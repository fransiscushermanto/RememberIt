package com.example.rememberit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.example.rememberit.Adapters.ToDoAdapter;
import com.example.rememberit.Models.ToDoModel;
import com.example.rememberit.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    final public static String DATABASE_CHANGED = "com.example.rememberit.DATABASE_CHANGED";

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter toDoAdapter;
    private FloatingActionButton fab;

    private List<ToDoModel> taskList;
    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
        db = new DatabaseHandler(this);
        db.openDatabase();
        taskList = new ArrayList<>();

        fab = findViewById(R.id.fab);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        toDoAdapter = new ToDoAdapter(this, db);
        tasksRecyclerView.setAdapter(toDoAdapter);

        IntentFilter filter = new IntentFilter(DATABASE_CHANGED);;
        registerReceiver(broadcastReceiver, filter);
        LoadTask();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void LoadTask() {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        toDoAdapter.setTasks(taskList);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DATABASE_CHANGED)) {
                LoadTask();
            }
        }
    };

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        LoadTask();
        toDoAdapter.notifyDataSetChanged();
    }
}