package com.birdeye.util;

import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

public final class Asserts {
  @MainThread
  public static void mainThread() {
    if (Looper.myLooper() != Looper.getMainLooper()) {
      throw new RuntimeException("thread");
    }
  }

  @WorkerThread
  public static void workerThread() {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      throw new RuntimeException("thread");
    }
  }
}
