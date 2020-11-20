package com.example.rememberit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class TimePickerFragment extends Fragment {
    TimePicker timePicker;
    Calendar calendar;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.time_picker_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        timePicker = view.findViewById(R.id.timepicker);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 4);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (bundle != null) {
            timePicker.setHour(bundle.getInt("hour"));
            timePicker.setMinute(bundle.getInt("minute"));
        }else {
            timePicker.setHour(hour);
            timePicker.setMinute(0);
        }
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                ((DateTimePickerDialog)getActivity()).setTime(hour, minute, 0);
            }
        });
    }
}
