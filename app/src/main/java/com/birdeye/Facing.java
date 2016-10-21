package com.birdeye;

import android.support.annotation.StringRes;

public enum Facing {
  BACK,
  FRONT;

  @StringRes int def() {
    switch (this) {
      case BACK:
        return R.string.back;
      case FRONT:
        return R.string.front;
    }
    throw new RuntimeException();
  }

  public int index() {
    switch (this) {
      case BACK:
        return 0;
      case FRONT:
        return 1;
    }
    throw new RuntimeException();
  }

  @Override public String toString() {
    return App.app.getString(def());
  }
}