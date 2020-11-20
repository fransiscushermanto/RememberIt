package com.example.rememberit;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

public class LockEditText extends androidx.appcompat.widget.AppCompatEditText {
    public LockEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event)
    {
        // Return true if I handle the event:
        // In my case i want the keyboard to not be dismissible so i simply return true
        // Other people might want to handle the event differently
        clearFocus();
        setVisibility(GONE);
        return false;
    }

    protected void onKeyPress () {}
}
