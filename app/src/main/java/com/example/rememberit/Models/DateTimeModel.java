package com.example.rememberit.Models;

import android.util.Log;

import androidx.annotation.LongDef;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DateTimeModel {
    Calendar calendar;
    int year, month, date, hour, minute, second;

    public DateTimeModel() { }

    public DateTimeModel(int year, int month, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.calendar = Calendar.getInstance();
        this.calendar.set(year, month, date, hour, minute, second);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void setCalendarDateTime(Calendar calendar) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(calendar.getTime());
        setYear(calendar.get(Calendar.YEAR));
        setMonth(calendar.get(Calendar.MONTH));
        setDate(calendar.get(Calendar.DATE));
        setHour(calendar.get(Calendar.HOUR_OF_DAY));
        setSecond(calendar.get(Calendar.SECOND));
    }

    public void setCalendarDateTime(java.util.Date date) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(date);
        setYear(calendar.get(Calendar.YEAR));
        setMonth(calendar.get(Calendar.MONTH));
        setDate(calendar.get(Calendar.DATE));
        setHour(calendar.get(Calendar.HOUR_OF_DAY));
        setSecond(calendar.get(Calendar.SECOND));
    }

    public java.util.Date getDateTime() {
        java.util.Date dateTime = calendar.getTime();
        return dateTime;
    }

    public Calendar getCalendarDateTime() {
        if (calendar != null) {
            calendar.setTime(this.calendar.getTime());
            return this.calendar;
        }
        return calendar;
    }

    public String getFormattedDateTime(String pattern, int timeFormat) {
        return formatDateTime(calendar, pattern, timeFormat);
    }

    public Calendar getCalendarCurrentDateTime() {
        calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        return calendar;
    }

    public String getYearMonthDate() {
        return formatDateTime(calendar, "dd-MMMM-yyyy", 24);
    }

    public String getHourMinuteSecond24Hour() {
        return formatDateTime(calendar, "HH:mm:ss", 24);
    }

    public String getHourMinuteSecond12Hour() {
        return formatDateTime(calendar, "HH:mm:ss a", 12);
    }

    public String formatCurrentDateTimeWithIntervalDaysOfWeek(Calendar calendar, int interval,String pattern) {
        calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.MINUTE, 1);
        calendar.add(Calendar.DAY_OF_WEEK, interval);
        return formatDateTime(calendar, pattern, 24);
    }

    public String formatCurrentDateTimeWithIntervalYear(Calendar calendar, int interval, String pattern) {
        calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.YEAR, interval);
        return formatDateTime(calendar, pattern, 24);
    }

    public String formatCurrentDateTimeWithIntervalMonth(Calendar calendar, int interval, String pattern) {
        calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
        calendar.add(Calendar.MONTH, interval);
        return formatDateTime(calendar, pattern, 24);
    }

    public java.util.Date parseStringToDateTime(String value, String pattern) {
        DateFormat parseString = new SimpleDateFormat(pattern);
        try {
            java.util.Date parsed = parseString.parse(value);
            return parsed;
        }catch (Exception e) {
            return null;
        }
    }

    public String formatDateTime(Calendar calendar, String pattern, int timeFormat) {
        String formattedDatetime = "";
        DateFormat dateFormat;
        //timeFormat 12 or 24
        if (timeFormat == 12)
        {
            int halfFormat = calendar.get(Calendar.HOUR_OF_DAY) > 12 ? calendar.get(Calendar.HOUR_OF_DAY) - 12 : calendar.get(Calendar.HOUR_OF_DAY);
            String hour = halfFormat > 9 ? String.valueOf(halfFormat) : String.format("0%d",halfFormat);
            dateFormat = new SimpleDateFormat("mm a");
            formattedDatetime = String.format("%s:%s", hour, dateFormat.format(calendar.getTime()));
        }else if (timeFormat == 24 || timeFormat == 0) {
            dateFormat = new SimpleDateFormat(pattern);
            formattedDatetime = dateFormat.format(calendar.getTime());
        }
        return formattedDatetime;
    }

    public long checkDifferenceDateWithCurrentDate(Calendar calendar) {
        //NOW
        Calendar tempNow = Calendar.getInstance();
        tempNow.setTime(tempNow.getTime());
        tempNow.set(tempNow.get(Calendar.YEAR), tempNow.get(Calendar.MONTH), tempNow.get(Calendar.DATE), 0, 0, 0);
        tempNow.set(Calendar.MILLISECOND, 0);

        //CUSTOM TIME FROM PARAMS
        Calendar tempCustom = calendar;
        tempCustom.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
        tempCustom.set(Calendar.MILLISECOND, 0);
        return TimeUnit.DAYS.convert( calendar.getTimeInMillis() - tempNow.getTimeInMillis() , TimeUnit.MILLISECONDS);
    }
}
