package com.birdeye;

import android.graphics.Matrix;
import android.support.annotation.NonNull;

public final class CameraPicture {
	
  public final @NonNull byte[] bytes;
  public final @NonNull Matrix rotate;

  public CameraPicture(@NonNull byte[] bytes, @NonNull Matrix rotate) {
    this.bytes = bytes;
    this.rotate = rotate;
  }
  
}