package com.birdeye.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by tuff on 12-Apr-16.
 */


public class SharedPref {

    public static void set_savedCards1(Context activity, String savedCards){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("savedCards", savedCards);
        editor.commit();

    }

    public  static  String get_savedCards1(Context activity){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String savedCards = preferences.getString("savedCards", "");
        return savedCards;
    }

}