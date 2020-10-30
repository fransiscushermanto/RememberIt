package com.example.rememberit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

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
    private TabItem datePickerTab, timePickerTab;
    private Button btnCancel, btnSave;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dialog);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_dialog_fragment);

        tabLayout = findViewById(R.id.dateTimePickeR_tabLayout);
        datePickerTab = findViewById(R.id.tab_DatePicker);
        timePickerTab = findViewById(R.id.tab_TimePicker);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        fragmentTransaction.replace(R.id.dateTime_placeholder, datePickerFragment);
        fragmentTransaction.commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        DatePickerFragment datePickerFragment = new DatePickerFragment();
                        fragmentTransaction.replace(R.id.dateTime_placeholder, datePickerFragment);
                        fragmentTransaction.commit();
                        break;
                    case 1:
                        TimePickerFragment timePickerFragment = new TimePickerFragment();
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
}
