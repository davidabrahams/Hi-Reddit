package com.mobileproto.hireddit.hireddit.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreference {

    private static final String PREFS_NAME = "HI_REDDIT_PREFS";

    public SharedPreference() {
        super();
    }

    public void save(Context context, String key, Boolean text) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        editor.putBoolean(key, text);
        editor.apply();
    }

    public void save(Context context, String key, int commentsToSearch) {

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Editor editor = settings.edit();

        editor.putInt(key, commentsToSearch);

        editor.apply();
    }

    public Boolean getValue(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Boolean text = settings.getBoolean(key, false);
        return text;
    }

    public int getValueInt(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int i = settings.getInt(key, 1);
        return i;
    }
}