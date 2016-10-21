package com.birdeye;

import android.preference.PreferenceManager;
import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Prefs {
	
  static final RxSharedPreferences prefs =
      RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(App.app));

  public static final Preference<Boolean> enable    = prefs.getBoolean("enable", false);
  public static final Preference<String>  usernames = prefs.getString("usernames", "");
  public static final Preference<String>  hashtags  = prefs.getString("hashtags", "");
  public static final Preference<Long>    lastId    = prefs.getLong("last_id", null);
  public static final Preference<String>  message1  = prefs.getString("message1", null);
  public static final Preference<String>  message2  = prefs.getString("message2", null);
  public static final Preference<String>  message3  = prefs.getString("message3", null);

  public static final Preference<String>  message4  = prefs.getString("message4", null);
  public static final Preference<String>  message5  = prefs.getString("message5", null);
  public static final Preference<String>  message6  = prefs.getString("message6", null);
  public static final Preference<String>  message7  = prefs.getString("message7", null);
  public static final Preference<String>  message8  = prefs.getString("message8", null);
  public static final Preference<String>  message9  = prefs.getString("message9", null);
  public static final Preference<String>  message10  = prefs.getString("message10", null);
  public static final Preference<String>  message11  = prefs.getString("message11", null);
  public static final Preference<String>  message12  = prefs.getString("message12", null);
  public static final Preference<String>  message13  = prefs.getString("message13", null);
  public static final Preference<String>  message14  = prefs.getString("message14", null);
  public static final Preference<String>  message15  = prefs.getString("message15", null);
  public static final Preference<String>  message16  = prefs.getString("message16", null);
  public static final Preference<String>  message17  = prefs.getString("message17", null);
  public static final Preference<String>  message18  = prefs.getString("message18", null);
  public static final Preference<String>  message19  = prefs.getString("message19", null);
  public static final Preference<String>  message20  = prefs.getString("message20", null);
  public static final Preference<String>  message21  = prefs.getString("message21", null);
  public static final Preference<String>  message22  = prefs.getString("message22", null);
  public static final Preference<String>  message23  = prefs.getString("message23", null);
  public static final Preference<String>  message24  = prefs.getString("message24", null);
  public static final Preference<String>  message25  = prefs.getString("message25", null);


  public static final Preference<Facing>  facing    =
      prefs.getEnum("active_camera", Facing.BACK, Facing.class);

  public static final List<Preference<String>> messages  =
      Collections.unmodifiableList(Arrays.asList(message1, message2, message3, message4, message5, message6,
              message7, message8, message9, message10, message11, message12, message13, message14, message15,
              message16, message17, message18, message19, message20, message21, message22, message23, message24, message25));

  public static final List<Preference<?>>      loginData =
      Collections.unmodifiableList(Arrays.asList(enable, lastId));
}
