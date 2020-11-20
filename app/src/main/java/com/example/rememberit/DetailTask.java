package com.example.rememberit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rememberit.Models.DateTimeModel;
import com.example.rememberit.Models.ToDoModel;
import com.example.rememberit.Utils.DatabaseHandler;

import java.lang.reflect.Field;
import java.util.Calendar;

public class DetailTask extends AppCompatActivity {
    final public static String DATABASE_CHANGED = "com.example.rememberit.DATABASE_CHANGED";

    Toolbar tasksToolbar;
    LockEditText editText_task_title;
    TextView task_createdAt, task_body, taskBodyUpdateTime, textView_task_title;
    ToDoModel taskFromDB;
    DateTimeModel dateTimeModel, dueDateModel, remindModel;
    CheckBox task_status;
    Button dueDate_btn, remindMe_btn;
    ImageButton dueDate_close, remindMe_close;
    Calendar calendar, now;
    DatePickerDialog datePickerDialog;
    LinearLayout dueDateLayout, remindMeLayout;
    RelativeLayout taskBodyLayout;
    DatabaseHandler db;
    private boolean warning = false, dueDateClose = false, remindMeClose = false, resetState = false;
    String title, due_date, remind_me, created_at, finished_at;
    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);

        db = new DatabaseHandler(this);
        db.openDatabase();

        now = Calendar.getInstance();

        dateTimeModel = new DateTimeModel();
        dueDateModel = new DateTimeModel();
        remindModel = new DateTimeModel();

        Intent intent = getIntent();
        final String id = intent.getStringExtra("task_id");
        LoadDataFromDB(id);

        tasksToolbar = findViewById(R.id.tasks_toolbar);
        editText_task_title = findViewById(R.id.todoTextTitle_editText);
        textView_task_title = findViewById(R.id.todoTextTitle_textView);
        task_status = findViewById(R.id.todoCheckBox);
        task_createdAt = findViewById(R.id.task_createdAt);
        dueDateLayout = findViewById(R.id.dueDateWrapper);
        dueDate_btn = findViewById(R.id.setDueDateTask_button);
        dueDate_close = findViewById(R.id.dueDateClose_button);
        remindMeLayout = findViewById(R.id.remindMeWrapper);
        remindMe_btn = findViewById(R.id.remindMeTask_button);
        remindMe_close = findViewById(R.id.remindMeClose_button);
        taskBodyLayout = findViewById(R.id.task_body);
        task_body = findViewById(R.id.text_task_body);
        taskBodyUpdateTime = findViewById(R.id.task_body_update_time);


        setSupportActionBar(tasksToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editText_task_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    textView_task_title.setVisibility(View.VISIBLE);
                    editText_task_title.setVisibility(View.GONE);
                }
            }
        });

        task_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        java.util.Date finish = Calendar.getInstance().getTime();
                        taskFromDB.setStatus(1);
                        taskFromDB.setFinished_at(finish.toString());
                        dateTimeModel.setCalendarDateTime(finish);
                        db.updateStatus(taskFromDB);
                        LoadDataFromDB(id);
                    }
                    else {
                        taskFromDB.setStatus(0);
                        taskFromDB.setFinished_at("");
                        db.updateStatus(taskFromDB);
                        LoadDataFromDB(id);
                    }
                if (status == 1) {
                    textView_task_title.setPaintFlags(textView_task_title.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
                }else {
                    textView_task_title.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                }
                task_createdAt.setText(String.format("%s %s", status == 1 ? "Completed" : "Created on", status == 1 ? checkDate(dateTimeModel.getCalendarDateTime(), now.get(Calendar.YEAR) != dateTimeModel.getYear() ? "EEE, MMM dd yyyy" : "EEE, MMM dd") : dateTimeModel.formatDateTime(calendar, "EEE, MMM dd", 0)));
            }
        });

        setDataToObject();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadDataFromDB(taskFromDB.getId());
        setDataToObject();
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

    public void LoadDataFromDB (String id) {
        taskFromDB = db.getTask(id);
        title = taskFromDB.getTaskTitle();
        created_at = taskFromDB.getCreated_at();
        due_date = taskFromDB.getDueDate();
        remind_me = taskFromDB.getRemindMe();
        status = taskFromDB.getStatus();
        finished_at = taskFromDB.getFinished_at();
        if (taskFromDB.getTaskBody_id() != null) {
            ToDoModel temp = db.getTaskBody(taskFromDB.getTaskBody_id());
            taskFromDB.setTaskBody_updated_at(temp.getTaskBody_updated_at());
            taskFromDB.setTaskBody(temp.getTaskBody());
        }
    }

    public void setDataToObject () {
        if (remind_me != null) {
            java.util.Date parsed = dueDateModel.parseStringToDateTime(remind_me, "EEE MMM dd HH:mm:ss Z yyyy");
            remindModel.setCalendarDateTime(parsed);
            checkTime(remindModel.getCalendarDateTime());
            String newLine = "\n<font color=\"black\">" + checkDate(remindModel.getCalendarDateTime(), remindModel.getYear() != now.get(Calendar.YEAR) ? "EE, MMM d, yyyy" : "EE, MMM d") + "</font>" ;
            changeBtnBgColorDrawableColor(String.format("Remind me at %s \n", remindModel.getFormattedDateTime("H:mm a", 12)), R.id.remindMeTask_button);
            remindMe_btn.append(Html.fromHtml(newLine));
        }
        if (due_date != null) {
            java.util.Date parsed = dueDateModel.parseStringToDateTime(due_date, "EEE MMM dd HH:mm:ss Z yyyy");
            dueDateModel.setCalendarDateTime(parsed);
            changeBtnBgColorDrawableColor(String.format("Due %s", checkDate(dueDateModel.getCalendarDateTime(), dueDateModel.getYear() != now.get(Calendar.YEAR) ? "EE, MMM d, yyyy" : "EE, MMM d")), R.id.setDueDateTask_button);
        }

        calendar = Calendar.getInstance();
        dateTimeModel.setCalendarDateTime(calendar);
        task_status.setChecked(status == 1 ? true : false);

        textView_task_title.setText(title);
        editText_task_title.setText(title);
        if (status == 1) {
            textView_task_title.setPaintFlags(textView_task_title.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            textView_task_title.setPaintFlags(textView_task_title.getPaintFlags()| Paint.LINEAR_TEXT_FLAG);
        }

        if (status == 0) {
            java.util.Date parsed = dateTimeModel.parseStringToDateTime(created_at, "EEE MMM dd HH:mm:ss Z yyyy");
            if (parsed != null) {
                calendar.setTime(parsed);
            }
        }else if (status == 1){
            java.util.Date parsed = dateTimeModel.parseStringToDateTime(finished_at, "EEE MMM dd HH:mm:ss Z yyyy");
            Log.d("~ ", String.valueOf(finished_at));
            if (parsed != null) {
                calendar.setTime(parsed);
            }else {
                calendar.setTime(Calendar.getInstance().getTime());
            }
        }

        if (taskFromDB.getTaskBody_id() != null) {
            task_body.setText(taskFromDB.getTaskBody());
            taskBodyUpdateTime.setText(formatUpdateStringDate(taskFromDB.getTaskBody_updated_at()));
        }else {
            task_body.setText("");
            taskBodyUpdateTime.setText("");
        }

        dateTimeModel.setCalendarDateTime(calendar);
        task_createdAt.setText(String.format("%s %s", status == 1 ? "Completed" : "Created on", status == 1 ? checkDate(dateTimeModel.getCalendarDateTime(), now.get(Calendar.YEAR) != dateTimeModel.getYear() ? "EEE, MMM dd yyyy" : "EEE, MMM dd") : dateTimeModel.formatDateTime(calendar, "EEE, MMM dd", 0)));

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                switch (requestCode) {
                    case 1 :
                        String result = data.getStringExtra("result");
                        try {
                            calendar = Calendar.getInstance();
                            calendar.setTime(remindModel.parseStringToDateTime(result, "EE MMM d HH:mm:ss Z yyyy"));
                            remindModel.setCalendarDateTime(calendar);
                            String newLine = "\n<font color=\"black\">" + checkDate(remindModel.getCalendarDateTime(), remindModel.getYear() != now.get(Calendar.YEAR) ? "EE, MMM d, yyyy" : "EE, MMM d") + "</font>" ;
                            changeBtnBgColorDrawableColor(String.format("Remind me at %s \n", remindModel.getFormattedDateTime("H:mm a", 12)), R.id.remindMeTask_button);
                            remindMe_btn.append(Html.fromHtml(newLine));
                        }catch (Exception e) {
                            Log.d("Error parsing", e.getMessage());
                        }
                        break;
                }
                break;


            default:
                break;
        }
    }

    public String getDateTime(int interval, String pattern) {
        dateTimeModel = new DateTimeModel();
        calendar = Calendar.getInstance();
        return dateTimeModel.formatCurrentDateTimeWithIntervalDaysOfWeek(calendar, interval, pattern);
    }

    private void changeBtnBgColorDrawableColor(String date, int btnId) {
        Button btn = findViewById(btnId);
        Drawable leftDrawable = null, closeDrawable, background = null;
        switch (btnId) {
            case R.id.setDueDateTask_button :
                if (resetState) dueDateClose = false;
                else dueDateClose = true;

                if (dueDateClose) {
                    dueDate_close.setVisibility(View.VISIBLE);
                    leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_calendar_green);
                }else {
                    dueDate_close.setVisibility(View.GONE);
                    leftDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_calendar);
                }

                break;
            case R.id.remindMeTask_button :
                if (resetState) remindMeClose = false;
                else remindMeClose = true;

                if (remindMeClose) {
                    remindMe_close.setVisibility(View.VISIBLE);
                    leftDrawable = AppCompatResources.getDrawable(this, warning ? R.drawable.ic_notifications : R.drawable.ic_notifications_green);
                }else {
                    remindMe_close.setVisibility(View.GONE);
                    leftDrawable = AppCompatResources.getDrawable(this,R.drawable.ic_notifications);
                }
                break;
            default:
                break;
        }

        leftDrawable = DrawableCompat.wrap(leftDrawable);
        if (btnId == R.id.setDueDateTask_button && warning) {
            DrawableCompat.setTint(leftDrawable.mutate(), Color.RED );
        }
        leftDrawable.setBounds(0, 0, 60, 60);

        if (resetState) {
            DrawableCompat.setTint(leftDrawable.mutate(), Color.BLACK );
            leftDrawable.setBounds(0, 0, 60, 60);
            background = null;
            btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn.setPadding( 40, 0, 15, 0);
            btn.setTextColor(Color.BLACK);
            btn.setText("Set due date");

        }else {
            background = null;
            btn.setPadding( 40, 0, 15, 0);
            if (warning) {
                if (btnId == R.id.remindMeTask_button) btn.setTextColor(Color.BLACK);
                else btn.setTextColor(Color.RED);
            }else {
                btn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            btn.setText(date);
        }

        btn.setCompoundDrawables(leftDrawable, null, null, null);
        btn.setBackground(background);

        if (resetState) resetState = false;
    }

    public void checkTime (Calendar calendar) {
        Calendar temp = Calendar.getInstance();
        int now = temp.get(Calendar.HOUR_OF_DAY);
        int set = calendar.get(Calendar.HOUR_OF_DAY);
        int dateDifference = (int) dateTimeModel.checkDifferenceDateWithCurrentDate(calendar);
        if (dateDifference == 0) {
            if (set < now) {
                warning = true;
            }else {
                warning = false;
            }
        }
    }

    public String checkDate(Calendar calendar, String pattern) {
        String result = "";
        dateTimeModel = new DateTimeModel();
        int difference = (int) dateTimeModel.checkDifferenceDateWithCurrentDate(calendar);
        if (difference == 0) {
            result = "Today";
        }else if (difference == 1) {
            result = "Tomorrow";
        }else if (difference == 7) {
            result = "Next Week";
        } else {
            if (difference < 0) {
                warning = true;
            }else {
                warning = false;
            }
            result = dateTimeModel.formatDateTime(calendar, pattern, 0);
        }

        return result;
    }

    public void setRemindMe(View view) {
        Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.PopUpMenu);
        PopupMenu popupMenu = new PopupMenu(wrapper, remindMe_btn);
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popupMenu);
            menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(menuPopupHelper, true);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            popupMenu.getMenuInflater().inflate(R.menu.menu_set_task_reminder, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    calendar.set(Calendar.MINUTE, 0);
                    switch (menuItem.getItemId()) {
                        case R.id.item_laterTodayRemind:
                            String today = dateTimeModel.formatCurrentDateTimeWithIntervalHour(calendar, 4, "EEEE, dd-MM-yyyy H:mm a", 24);
                            remindModel.setCalendarDateTime(remindModel.parseStringToDateTime(today, "EEEE, dd-MM-yyyy HH:mm a"));
                            break;
                        case R.id.item_tomorrowRemind:
                            dateTimeModel = new DateTimeModel();
                            java.util.Date parsedTomorrow = dateTimeModel.parseStringToDateTime(getDateTime(1, "EEEE, dd-MM-yyyy HH:mm a"), "EEEE, dd-MM-yyyy HH:mm a");
                            dateTimeModel.setCalendarDateTime(parsedTomorrow);
                            dateTimeModel.setMinute(0);
                            dateTimeModel.setHour(9);
                            String tomorrow = dateTimeModel.getFormattedDateTime("EEEE, dd-MM-yyyy HH:mm a", 24);
                            remindModel.setCalendarDateTime(remindModel.parseStringToDateTime(tomorrow, "EEEE, dd-MM-yyyy HH:mm a"));
                            break;
                        case R.id.item_nextWeekRemind:
                            dateTimeModel = new DateTimeModel();
                            java.util.Date parsedNextWeek = dateTimeModel.parseStringToDateTime(getDateTime(7, "EEEE, dd-MM-yyyy HH:mm a"), "EEEE, dd-MM-yyyy HH:mm a");
                            dateTimeModel.setCalendarDateTime(parsedNextWeek);
                            dateTimeModel.setMinute(0);
                            dateTimeModel.setHour(9);
                            String nextWeek = dateTimeModel.getFormattedDateTime("EEEE, dd-MM-yyyy HH:mm a", 24);
                            remindModel.setCalendarDateTime(remindModel.parseStringToDateTime(nextWeek, "EEEE, dd-MM-yyyy HH:mm a"));
                            break;
                        case R.id.item_customRemind:
                            Intent intent = new Intent(getApplicationContext(), DateTimePickerDialog.class);
                            if (remindModel.getCalendarDateTime() != null) {
                                intent.putExtra("date", remindModel.getDate());
                                intent.putExtra("month", remindModel.getMonth());
                                intent.putExtra("year", remindModel.getYear());
                                intent.putExtra("hour", remindModel.getHour());
                                intent.putExtra("minute", remindModel.getMinute());
                                intent.putExtra("second", remindModel.getSecond());
                            }
                            startActivityForResult(intent, 1);

                            break;
                        default:
                            break;
                    }
                    if (remindModel.getCalendarDateTime() != null && menuItem.getItemId() != R.id.item_customRemind) {
                        checkTime(remindModel.getCalendarDateTime());
                        String newLine = "\n<font color=\"black\">" + checkDate(remindModel.getCalendarDateTime(), "EE, MMM d") + "</font>" ;
                        changeBtnBgColorDrawableColor(String.format("Remind me at %s \n", remindModel.getFormattedDateTime("H:mm a", 12)), R.id.remindMeTask_button);
                        remindMe_btn.append(Html.fromHtml(newLine));
                    }
                    return false;
                }
            });
        }
        Calendar temp = Calendar.getInstance();
        temp.setTime(temp.getTime());
        temp.add(Calendar.HOUR_OF_DAY, 4);
        Menu menus = popupMenu.getMenu();
        if (dueDateModel.checkDifferenceDateWithCurrentDate(temp) == 0) {
            temp.setTime(Calendar.getInstance().getTime());
            temp.set(Calendar.MINUTE, 0);
            menus.getItem(0).setTitle(String.format("Later Today (%s)", dateTimeModel.formatCurrentDateTimeWithIntervalHour(temp, 4, "H:mm a", 12)));
        }else {
            temp.setTime(Calendar.getInstance().getTime());
            temp.set(Calendar.MINUTE, 0);
            menus.getItem(0).setTitle("Later Today").setEnabled(false);
        }
        temp.add(Calendar.DATE, 1);
        temp.set(Calendar.HOUR_OF_DAY, 9);
        menus.getItem(1).setTitle(String.format("Tomorrow (%s)", dateTimeModel.formatCurrentDateTimeWithIntervalHour(temp, 0, "EEE H:mm a", 24)));
        temp.add(Calendar.DATE, 6);
        temp.set(Calendar.HOUR_OF_DAY, 9);
        menus.getItem(2).setTitle(String.format("Next Week (%s)", dateTimeModel.formatCurrentDateTimeWithIntervalHour(temp, 0, "EEE H:mm a", 24)));


        popupMenu.show();
    }

    public void setDueDate(View view) {
        Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.PopUpMenu);
        PopupMenu popupMenu = new PopupMenu(wrapper, dueDate_btn);
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popupMenu);
            menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(menuPopupHelper, true);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            popupMenu.getMenuInflater().inflate(R.menu.menu_set_task_due_date, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.item_todayDue:
                            String today = getDateTime(0, "EEEE, dd-MM-yyyy HH:mm a");
                            dueDateModel.setCalendarDateTime(dueDateModel.parseStringToDateTime(today, "EEEE, dd-MM-yyyy HH:mm a"));
                            break;
                        case R.id.item_tomorrowDue:
                            String tomorrow = getDateTime(1, "EEEE, dd-MM-yyyy HH:mm a");
                            dueDateModel.setCalendarDateTime(dueDateModel.parseStringToDateTime(tomorrow, "EEEE, dd-MM-yyyy HH:mm a"));
                            break;
                        case R.id.item_nextWeekDue:
                            String nextWeek = getDateTime(7, "EEEE, dd-MM-yyyy HH:mm a");
                            dueDateModel.setCalendarDateTime(dueDateModel.parseStringToDateTime(nextWeek, "EEEE, dd-MM-yyyy HH:mm a"));
                            break;
                        case R.id.item_customDue:
                            calendar = Calendar.getInstance();
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            int month = calendar.get(Calendar.MONTH);
                            int year = calendar.get(Calendar.YEAR);
                            if (dueDateModel.getCalendarDateTime() != null) {
                                day = dueDateModel.getDate();
                                month = dueDateModel.getMonth();
                                year = dueDateModel.getYear();
                            }

                            datePickerDialog = new DatePickerDialog(DetailTask.this, R.style.MyDatePickerStyle,new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                    calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(0);
                                    calendar.set(year, month, day, 0,0, 0);
                                    dueDateModel.setCalendarDateTime(calendar);
                                    String when = checkDate(dueDateModel.getCalendarDateTime(), dueDateModel.getYear() != now.get(Calendar.YEAR) ? "EE, MMM d, yyyy" : "EE, MMM d");
                                    changeBtnBgColorDrawableColor(String.format("Due %s", when), R.id.setDueDateTask_button);
                                }
                            }, year, month, day);
                            datePickerDialog.show();
                            break;
                        default:
                            break;
                    }
                    if (dueDateModel.getCalendarDateTime() != null && menuItem.getItemId() != R.id.item_customDue) {
                        changeBtnBgColorDrawableColor(String.format("Due %s", checkDate(dueDateModel.getCalendarDateTime(), dueDateModel.getYear() != now.get(Calendar.YEAR) ? "EE, MMM d, yyyy" : "EE, MMM d")), R.id.setDueDateTask_button);
                    }
                    return false;
                }
            });
            Menu menus = popupMenu.getMenu();
            menus.getItem(0).setTitle(String.format("Today (%s)", getDateTime(0, "EEEE")));
            menus.getItem(1).setTitle(String.format("Tomorrow (%s)", getDateTime(1, "EEEE")));
            menus.getItem(2).setTitle(String.format("Next Week (%s)", getDateTime(7, "EEEE")));
            popupMenu.show();
    }
}

    public void openBodyEditor(View view) {
        Intent intent = new Intent(getApplicationContext(), BodyEditor.class);
        intent.putExtra("task_id", taskFromDB.getId());
        startActivity(intent);
        }

    public void onClear(View view) {
        switch (view.getId()) {
            case R.id.dueDateClose_button:
                resetState = true;
                warning = false;
                changeBtnBgColorDrawableColor("", R.id.setDueDateTask_button);
                dueDateModel = new DateTimeModel();
                break;
            case R.id.remindMeClose_button:
                resetState = true;
                warning = false;
                changeBtnBgColorDrawableColor("", R.id.remindMeTask_button);
                remindModel = new DateTimeModel();
                break;
            default:
                break;
        }
    }

    public void onSwapToEditText(View view) {
        textView_task_title.setVisibility(View.GONE);
        editText_task_title.setVisibility(View.VISIBLE);
        editText_task_title.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText_task_title, InputMethodManager.SHOW_IMPLICIT);
        editText_task_title.setSelection(title.length());
    }
}