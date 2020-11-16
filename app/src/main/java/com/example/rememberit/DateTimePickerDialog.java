package com.example.rememberit;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.rememberit.Models.DateTimeModel;
import com.example.rememberit.Models.ToDoModel;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

public class DateTimePickerDialog extends AppCompatActivity {
    public static final String TAG = "DateTimePickerDialog";
    private TabLayout tabLayout;
    private Button btnCancel, btnSave;
    private Calendar calendar;
    private boolean isDateSet = false, isTimeSet = false;
    private int year, month, date, hour, minute, second;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_dialog_fragment);

        final Intent intent = getIntent();
        if (getIntent().getExtras() != null) {
            year = intent.getIntExtra("year", 0);
            month = intent.getIntExtra("month", 0);
            date = intent.getIntExtra("date", 0);
            hour = intent.getIntExtra("hour", 0);
            minute = intent.getIntExtra("minute", 0);
            second = intent.getIntExtra("second", 0);
        }
        tabLayout = findViewById(R.id.dateTimePickeR_tabLayout);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        calendar = Calendar.getInstance();

        Bundle datePickerBundle = new Bundle();
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        if (intent.getExtras() != null) {
            datePickerBundle.putInt("date", date);
            datePickerBundle.putInt("month", month);
            datePickerBundle.putInt("year", year);
            datePickerFragment.setArguments(datePickerBundle);
        }
        fragmentTransaction.replace(R.id.dateTime_placeholder, datePickerFragment);
        fragmentTransaction.commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        Bundle datePickerBundle = new Bundle();
                        DatePickerFragment datePickerFragment = new DatePickerFragment();
                        if (isDateSet) {
                            datePickerBundle.putInt("date", date);
                            datePickerBundle.putInt("month", month);
                            datePickerBundle.putInt("year", year);
                            datePickerFragment.setArguments(datePickerBundle);
                        }
                        fragmentTransaction.replace(R.id.dateTime_placeholder, datePickerFragment);
                        fragmentTransaction.commit();
                        break;
                    case 1:
                        Bundle timePickerBundle = new Bundle();
                        TimePickerFragment timePickerFragment = new TimePickerFragment();
                        if (isTimeSet) {
                            timePickerBundle.putInt("hour", hour);
                            timePickerBundle.putInt("minute", minute);
                            timePickerBundle.putInt("second", second);
                            timePickerFragment.setArguments(timePickerBundle);
                        }
                        fragmentTransaction.replace(R.id.dateTime_placeholder, timePickerFragment);
                        fragmentTransaction.commit();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent.getExtras() == null) {
                    checkDateTime();
                }
                calendar.set(year, month, date, hour, minute, second);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", calendar.getTime().toString());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void changeTab(int tabIndex) {
        tabLayout.getTabAt(tabIndex).select();
    }

    public void setDate (int year, int month, int date) {
        this.year = year;
        this.month = month;
        this.date = date;
        isDateSet = true;
    }

    public void setTime (int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        isTimeSet = true;
    }

    private void checkDateTime () {
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.HOUR_OF_DAY, 4);
        Log.d("~ ", String.valueOf(year));
        if (year <= 0) {
            year = calendar.get(Calendar.YEAR);
        }

        if (month <= 0) {
            month = calendar.get(Calendar.MONTH);
        }

        if (date <= 0) {
            date = calendar.get(Calendar.DATE);
        }

        if (hour <= 0) {
           hour = calendar.get(Calendar.HOUR_OF_DAY);
        }
//        Log.d("~ AM PM", String.valueOf(calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM"));
        calendar.setTime(calendar.getTime());
    }
}
