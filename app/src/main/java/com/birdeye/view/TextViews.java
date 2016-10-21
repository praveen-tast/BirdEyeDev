package com.birdeye.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

public final class TextViews {
	
  public static @NonNull String trimmed(@NonNull TextView tv) {
    return tv.getText().toString().trim();
  }

  public static void setText(@NonNull EditText tv, @Nullable String s) {
    tv.setText(s);
    tv.setSelection(s != null ? s.length() : 0);
  }
  
}