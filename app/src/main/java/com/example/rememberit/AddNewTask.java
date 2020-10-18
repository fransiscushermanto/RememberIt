package com.example.rememberit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.rememberit.Models.ToDoModel;
import com.example.rememberit.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AddNewTask extends BottomSheetDialogFragment {
    public static final String TAG = "ActionBottomDialog";

    private EditText newTaskTitleText;
    private Button newTaskSaveButton, setDueDateButton, setRemindMe;
    private DateFormat dateFormat;
    private Calendar calendar;
    private DatabaseHandler db;
    private String date;
    private DatePickerDialog datePickerDialog;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskTitleText = getView().findViewById(R.id.newTaskText);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);
        setDueDateButton = getView().findViewById(R.id.setDueDateTask_button);
        setRemindMe = getView().findViewById(R.id.remindMeTask_button);

        newTaskTitleText.requestFocus();

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        Log.d("Bundle Item", String.valueOf(bundle) );
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskTitleText.setText(task);
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
                    Log.d("Already Update", "finalIsUpdate");
                    db.updateTask(bundle.getString("id"), text, "");
                }
                else {
                    long now = Calendar.getInstance().getTimeInMillis();
                    ToDoModel task = new ToDoModel();
                    task.setId(String.valueOf(now));
                    task.setTaskTitle(text);
                    task.setStatus(0);
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
                                    Toast.makeText(getContext(), String.valueOf(getDateTime(0, "EEEE, dd-MM-YYYY HH:mm a")), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.item_tomorrowDue:
                                    Toast.makeText(getContext(), String.valueOf(getDateTime(1, "EEEE, dd-MM-YYYY HH:mm a")), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.item_nextWeekDue:
                                    Toast.makeText(getContext(), String.valueOf(getDateTime(7, "EEEE, dd-MM-YYYY HH:mm a")), Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.item_customDue:
                                    calendar = Calendar.getInstance();
                                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                                    int month = calendar.get(Calendar.MONTH);
                                    int year = calendar.get(Calendar.YEAR);
                                    datePickerDialog = new DatePickerDialog(getContext(), R.style.MyDatePickerStyle,new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                            Toast.makeText(getContext(), String.valueOf(day + "/" + month + "/" + year), Toast.LENGTH_SHORT).show();
                                        }
                                    }, year, month, day);
                                    datePickerDialog.show();
                                    break;
                                default:
                                    break;
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
        calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.DAY_OF_WEEK, interval);
        dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(calendar.getTime());
    }
}
