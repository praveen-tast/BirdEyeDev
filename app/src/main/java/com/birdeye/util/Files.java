package com.birdeye.util;

import android.content.Context;
import android.support.annotation.NonNull;
import java.io.File;

public final class Files {
  public static @NonNull File dir(@NonNull Context c) {
    final File ext = c.getExternalFilesDir("birdeye");
    final File dir = ext != null ? ext : c.getFilesDir();
    //noinspection ResultOfMethodCallIgnored
    dir.mkdirs();
    return dir;
  }
}
