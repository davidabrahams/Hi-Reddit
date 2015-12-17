package com.mobileproto.hireddit.hireddit.visuals;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * CustomEditText: A class that extends EditText and makes exiting the EditText keyboard
 * equivalent to pressing 'done'
 */
public class CustomEditText extends EditText {

    private Context context;
    private AttributeSet attrs;
    private int defStyle;
    private CustomEditTextCallback customEditTextCallback;

    public CustomEditText(Context context) {
        super(context);
        this.context = context;
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.attrs = attrs;
        this.defStyle = defStyle;
    }

    public void setCallback(CustomEditTextCallback customEditTextCallback){
        this.customEditTextCallback = customEditTextCallback;
    }

    @Override public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            Log.d("EditText", "You pressed the back button");
            InputMethodManager mgr = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
            customEditTextCallback.leavingEditTextCallback();
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}
