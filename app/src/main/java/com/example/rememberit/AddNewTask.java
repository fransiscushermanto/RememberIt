package com.example.rememberit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.rememberit.Models.DateTimeModel;
import com.example.rememberit.Models.ToDoModel;
import com.example.rememberit.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;


public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";

    private EditText newTaskTitleText;
    private Button newTaskSaveButton, setDueDateButton, setRemindMe;
    private DateFormat dateFormat;
    private Calendar calendar;
    private DatabaseHandler db;
    private boolean dueDateClose = false, remindMeClose = false, resetState = false;
    private DatePickerDialog datePickerDialog;
    private ToDoModel task;
    private DateTimeModel dateTimeModel, dueDateModel, remindModel;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskTitleText = getView().findViewById(R.id.newTaskText);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);
        setDueDateButton = getView().findViewById(R.id.setDueDateTask_button);
        setRemindMe = getView().findViewById(R.id.remindMeTask_button);

        dueDateModel = new DateTimeModel();
        remindModel = new DateTimeModel();

        task = new ToDoModel();

        newTaskTitleText.requestFocus();

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskTitleText.setText(task);
            newTaskTitleText.setSelection(task.length());
            if (task.length() > 0) {
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
        }
        newTaskTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = newTaskTitleText.getText().toString();
                if (finalIsUpdate) {
                    task.setId(bundle.getString("id"));
                    task.setTaskBody_id("");
                    task.setTaskTitle(text);
                    db.updateTask(task);
                    ((MainActivity)getContext()).sendBroadcast(new Intent(MainActivity.DATABASE_CHANGED));
                }
                else {
                    Calendar now = Calendar.getInstance();
                    task.setId(String.valueOf(now.getTimeInMillis()));
                    task.setTaskTitle(text);
                    task.setStatus(0);
                    task.setDueDate(dueDateModel.getCalendarDateTime() == null ? null : dueDateModel.getCalendarDateTime().getTime().toString());
                    task.setRemindMe(remindModel.getCalendarDateTime() == null ? null : remindModel.getCalendarDateTime().getTime().toString());
                    task.setCreated_at(now.getTime().toString());
                    db.insertTask(task);
                }
                dismiss();
            }
        });

        setDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopUpMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper,setDueDateButton);
                try {
                    //1st method
                    Field field = popupMenu.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    menuPopupHelper.getClass().getDeclaredMethod("setForceShowIcon", boolean.class).invoke(menuPopupHelper, true);

                    //2nd method
//                    Field[] fields = popup.getClass().getDeclaredFields();
//                    for (Field field : fields) {
//                        if ("mPopup".equals(field.getName())) {
//                            field.setAccessible(true);
//                            Object menuPopupHelper = field.get(popup);
//                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
//                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
//                            setForceIcons.invoke(menuPopupHelper, true);
//                            break;
//                        }
//                    }
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

                                    datePickerDialog = new DatePickerDialog(getContext(), R.style.MyDatePickerStyle,new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                            Calendar now = Calendar.getInstance();
                                            calendar = Calendar.getInstance();
                                            calendar.setTimeInMillis(0);
                                            calendar.set(year, month, day, 0,0, 0);
                                            dueDateModel.setCalendarDateTime(calendar);
                                            changeBtnBgColorDrawableColor(String.format("Due %s", checkDate(dueDateModel.getCalendarDateTime(), now.get(Calendar.YEAR) !=  dueDateModel.getYear() ? "EE, MMM d, yyyy" : "EE, MMM d")), R.id.setDueDateTask_button);
                                        }
                                    }, year, month, day);
                                    datePickerDialog.show();
                                    break;
                                default:
                                    break;
                            }

                            if (dueDateModel.getCalendarDateTime() != null && menuItem.getItemId() != R.id.item_customDue) {
                                changeBtnBgColorDrawableColor(String.format("Due %s", checkDate(dueDateModel.getCalendarDateTime(), calendar.get(Calendar.YEAR) !=  dueDateModel.getYear() ?"EE, MMM d, yyyy" : "EE, MMM d")), R.id.setDueDateTask_button);
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
        });

        setDueDateButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (dueDateClose) {
                        int drawableRightWidth = setDueDateButton.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                        if(event.getX() + drawableRightWidth >= setDueDateButton.getMeasuredWidth()) {
                            resetState = true;
                            changeBtnBgColorDrawableColor("", R.id.setDueDateTask_button);
                            dueDateModel = new DateTimeModel();
                            return true;
                        }
                    }
                }
                return false;

            }
        });

        setRemindMe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (remindMeClose) {
                        int drawableRightWidth = setRemindMe.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                        if(event.getX() + drawableRightWidth >= setRemindMe.getMeasuredWidth()) {
                            resetState = true;
                            changeBtnBgColorDrawableColor("", R.id.remindMeTask_button);
                            remindModel = new DateTimeModel();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        setRemindMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopUpMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper, setRemindMe);
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
                            calendar = Calendar.getInstance();
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
                                    Intent intent = new Intent(getContext(), DateTimePickerDialog.class);
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
                                changeBtnBgColorDrawableColor(String.format("Remind me at %s \n%s", remindModel.getFormattedDateTime("H:mm a", 12), checkDate(remindModel.getCalendarDateTime(), "EE, MMM d")), R.id.remindMeTask_button);
                            }
                            return false;
                        }
                    });
                }
                Calendar temp = Calendar.getInstance();
                dateTimeModel = new DateTimeModel();
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
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                String result = data.getStringExtra("result");
                Calendar now = Calendar.getInstance();
                calendar = Calendar.getInstance();
                try {
                    calendar.setTime(remindModel.parseStringToDateTime(result, "EE MMM d HH:mm:ss Z yyyy"));
                    remindModel.setCalendarDateTime(calendar);
                    changeBtnBgColorDrawableColor(String.format("Remind me at %s \n%s", remindModel.getFormattedDateTime("H:mm a", 12), checkDate(remindModel.getCalendarDateTime(), now.get(Calendar.YEAR) != remindModel.getYear() ? "EE, MMM d, yyyy" : "EE, MMM d")), R.id.remindMeTask_button);
                }catch (Exception e) {
                    Log.d("Error parsing", e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }

    public String getDateTime(int interval, String pattern) {
        dateTimeModel = new DateTimeModel();
        calendar = Calendar.getInstance();
        return dateTimeModel.formatCurrentDateTimeWithIntervalDaysOfWeek(calendar, interval, pattern);
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
            result = dateTimeModel.formatDateTime(calendar, pattern, 0);
        }

        return result;
    }

    private void changeBtnBgColorDrawableColor(String date, int btnId) {
        Button btn = getView().findViewById(btnId);
        Drawable leftDrawable = null, closeDrawable, background = null;

        switch (btnId) {
            case R.id.setDueDateTask_button :
                leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_calendar_main);
                if (resetState) dueDateClose = false;
                else dueDateClose = true;
                break;
            case R.id.remindMeTask_button :
                leftDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_notifications);
                if (resetState) remindMeClose = false;
                else remindMeClose = true;

                break;
            default:
                break;
        }
        
        leftDrawable = DrawableCompat.wrap(leftDrawable);
        DrawableCompat.setTint(leftDrawable.mutate(), resetState ? Color.BLACK : Color.WHITE );
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());

        closeDrawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_times_circle);
        closeDrawable = DrawableCompat.wrap(closeDrawable);
        DrawableCompat.setTint(closeDrawable.mutate(), resetState ? Color.BLACK : Color.WHITE);
        closeDrawable.setBounds(0, 0, closeDrawable.getIntrinsicWidth(), closeDrawable.getIntrinsicHeight());


        if (resetState) {
            background = null;
            btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn.setPadding( 0, 0, 0, 0);
            btn.setTextColor(Color.BLACK);
            btn.setText("Set due date");

        }else {
            background = AppCompatResources.getDrawable(getContext(), R.drawable.add_new_task_rounded_btn);
            btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            btn.setPadding( 20, 0, 20, 0);
            btn.setTextColor(getResources().getColor(R.color.colorAccent));
            btn.setText(date);
        }

        btn.setCompoundDrawables(leftDrawable, null, resetState ? null : closeDrawable, null);
        btn.setBackground(background);
        if (resetState) resetState = false;
    }


}
