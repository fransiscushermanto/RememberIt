package com.example.rememberit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rememberit.Models.DateTimeModel;
import com.example.rememberit.Models.ToDoModel;
import com.example.rememberit.Utils.DatabaseHandler;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class BodyEditor extends AppCompatActivity {

    Toolbar taskBodyEditorToolbar;
    TextView taskBodyUpdateTime;
    EditText taskBodyPlace;
    ScrollView scrollView;
    String task_id;
    DatabaseHandler db;
    ToDoModel toDoModel, taskBodyModel;
    DateTimeModel dateTimeModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_editor);

        db = new DatabaseHandler(this);
        db.openDatabase();

        taskBodyEditorToolbar = findViewById(R.id.task_body_editor_toolbar);
        taskBodyPlace = findViewById(R.id.task_body_place);
        taskBodyUpdateTime = findViewById(R.id.task_body_update_time);
        scrollView = findViewById(R.id.scrollview_taskbody);

        Intent param = getIntent();
        task_id = param.getStringExtra("task_id");
        firstLoad();

        setSupportActionBar(taskBodyEditorToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskBodyPlace.requestFocus();
                Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_LONG).show();
            }
        });

        taskBodyPlace.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 0) {
                    taskBodyEditorToolbar.setElevation(5);
                }else {
                    taskBodyEditorToolbar.setElevation(0);
                }
            }
        });

        taskBodyPlace.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (toDoModel.getTaskBody_updated_at() != null){
                            firstLoad();
                            if (taskBodyPlace.getText().toString().isEmpty()){
                                taskBodyUpdateTime.setVisibility(View.GONE);
                            }else {
                                taskBodyUpdateTime.setVisibility(View.VISIBLE);

                            }
                            taskBodyUpdateTime.setText(formatUpdateStringDate(toDoModel.getTaskBody_updated_at()));
                        }else {
                            if (!taskBodyPlace.getText().toString().isEmpty()){
                                taskBodyUpdateTime.setText(formatUpdateStringDate(Calendar.getInstance().getTime().toString()));
                                taskBodyUpdateTime.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 500; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(
                                new TimerTask() {
                                    @Override
                                    public void run() {
                                        Calendar calendar = Calendar.getInstance();
                                        if (toDoModel.getTaskBody_id() != null) {
                                            if (!taskBodyPlace.getText().toString().isEmpty()) {
                                                if (!toDoModel.getTaskBody().equals(taskBodyPlace.getText().toString())) {
                                                    toDoModel.setTaskBody_updated_at(calendar.getTime().toString());
                                                    toDoModel.setTaskBody(taskBodyPlace.getText().toString());
                                                    db.updateTaskBody(toDoModel);
                                                    firstLoad();
                                                }
                                            }else {
                                                db.deleteTodoBody(toDoModel.getTaskBody_id());
                                                toDoModel.setTaskBody_id("");
                                                db.updateTask(toDoModel);
                                                firstLoad();
                                            }
                                        } else {
                                            if (!taskBodyPlace.getText().toString().isEmpty()) {
                                                toDoModel.setTaskBody_id(String.valueOf(calendar.getTimeInMillis()));
                                                toDoModel.setTaskBody_updated_at(calendar.getTime().toString());
                                                toDoModel.setTaskBody(taskBodyPlace.getText().toString());
                                                db.updateTask(toDoModel);
                                                db.insertTaskBody(toDoModel);
                                                firstLoad();
                                            }
                                        }
                                        // TODO: do what you need here (refresh list)
                                        // you will probably need to use runOnUiThread(Runnable action) for some specific actions (e.g. manipulating views)
                                    }
                                },
                                DELAY
                        );
                    }
                }
        );

        setObject();
    }


    public String formatUpdateStringDate(String date) {
        String result = "";
        dateTimeModel = new DateTimeModel();
        java.util.Date parsed = dateTimeModel.parseStringToDateTime(date, "EEE MMM dd HH:mm:ss Z yyyy");
        dateTimeModel.setCalendarDateTime(parsed);
        Calendar now = Calendar.getInstance();
        if (dateTimeModel.getYear() == now.get(Calendar.YEAR)) {
            if (dateTimeModel.getHour() == now.get(Calendar.HOUR_OF_DAY)) {
                int difference = now.get(Calendar.MINUTE) - dateTimeModel.getMinute();
                if (difference == 0) {
                    result = "Updated a few moments ago";
                }else {
                    if (difference == 1) {
                        int secondDiff = (now.get(Calendar.SECOND) + 60) - dateTimeModel.getSecond();
                        if (secondDiff + dateTimeModel.getSecond() >= 60) {
                            result = String.format("Updated %d minute ago", difference);
                        }else {
                            result = String.format("Updated a few moments ago");
                        }
                    }else {
                        result = String.format("Updated %d minute ago", difference);
                    }

                }
            }else {
                int difference = now.get(Calendar.HOUR_OF_DAY) - dateTimeModel.getHour();
                if (difference == 1) {
                    int minuteDiff = (now.get(Calendar.MINUTE) + 60) - dateTimeModel.getMinute();
                    if (minuteDiff + dateTimeModel.getSecond() >= 60) {
                        result = String.format("Updated %d hour ago", difference);
                    }else {
                        result = String.format("Updated %d minute ago", minuteDiff);
                    }
                }else {
                    result = String.format("Updated %d hour ago", difference);
                }

            }
        }else {
            result = String.format("Last Updated %s", dateTimeModel.getFormattedDateTime("EEE, MMM dd, yyyy", 0));
        }
        return result;
    }

    public void firstLoad () {
        toDoModel = db.getTask(task_id);
        if (toDoModel.getTaskBody_id() != null) {
            ToDoModel temp = getTaskBodyFromDB(toDoModel.getTaskBody_id());
            toDoModel.setTaskBody(temp.getTaskBody());
            toDoModel.setTaskBody_updated_at(temp.getTaskBody_updated_at());

        }
    }

    public void setObject() {
        taskBodyPlace.setText(toDoModel.getTaskBody_id() == null ? "" : toDoModel.getTaskBody());
        taskBodyUpdateTime.setVisibility(toDoModel.getTaskBody() != null ? View.VISIBLE : View.GONE );
    }

    public ToDoModel getTaskBodyFromDB(String taskBody_id) {
        return db.getTaskBody(taskBody_id);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", task_id);
        setResult(Activity.RESULT_OK, returnIntent);
    }
}