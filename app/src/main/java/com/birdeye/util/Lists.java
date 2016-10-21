package com.birdeye.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.List;

public final class Lists {
  public static @Nullable <T> T last(@NonNull List<T> l) {
    return l.isEmpty() ? null : l.get(l.size() - 1);
  }
}
