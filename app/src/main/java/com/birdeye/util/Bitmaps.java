package com.birdeye.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.Surface;
import com.birdeye.CameraPicture;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import timber.log.Timber;

public final class Bitmaps {
  public static @NonNull Bitmap write(@NonNull File file,
                                      @NonNull CameraPicture data,
                                      int rotation) {
    final Bitmap b2;
    if (degrees(rotation) % 180 == 0) {
      final Bitmap src = src(data.bytes);
      b2 = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), data.rotate, false);
    } else {
      b2 = src(data.bytes);
    }

    Timber.d("bitmap size %d x %d", b2.getWidth(), b2.getHeight());

    try (final FileOutputStream fos = new FileOutputStream(file)) {
      b2.compress(Bitmap.CompressFormat.JPEG, 90, fos);
      return b2;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static Bitmap src(@NonNull byte[] data) {
    final BitmapFactory.Options opts = new BitmapFactory.Options();
    return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
  }

  static int degrees(int rotation) {
    switch (rotation) {
      case Surface.ROTATION_0:
        return 0;
      case Surface.ROTATION_90:
        return 90;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_270:
        return 270;
    }
    throw new RuntimeException();
  }
}
