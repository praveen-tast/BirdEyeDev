package com.birdeye.util;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import com.birdeye.CameraPicture;
import com.birdeye.Facing;
import java.io.IOException;
import java.util.List;
import rx.Observable;
import timber.log.Timber;

import static com.birdeye.util.Observables.emitLast;

@SuppressWarnings("deprecation")
public final class Cameras {
  static final int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

  static final int WIDTH  = 1280;
  static final int HEIGHT = 720;

  @WorkerThread
  public static @NonNull CameraPicture takePicture(@NonNull Facing facing) {
    Asserts.workerThread();

    final int index = facing.index();
    final Camera camera = Camera.open(index);
    camera.setParameters(params(camera.getParameters()));

    final SurfaceTexture texture = new SurfaceTexture(GL_TEXTURE_EXTERNAL_OES);
    try {
      camera.setPreviewTexture(texture);
    } catch (IOException ignored) {
    }
    camera.startPreview();

    final byte[] data = takePicture(camera).toBlocking().first();
    camera.stopPreview();
    texture.release();
    camera.release();

    final Camera.CameraInfo info = new Camera.CameraInfo();
    Camera.getCameraInfo(index, info);
    final Matrix rotate = new Matrix();
    rotate.postRotate(info.orientation, 0.5F, 0.5F);
    return new CameraPicture(data, rotate);
  }

  static @NonNull Observable<Camera> autoFocus(@NonNull Camera camera) {
    return Observable.create(sub -> camera.autoFocus((sccs, c) -> emitLast(c, sub)));
  }

  static @NonNull Observable<byte[]> takePicture(@NonNull Camera camera) {
    return Observable.create(sub -> {
      final Camera.ShutterCallback shutter = null;
      final Camera.PictureCallback raw = null;
      final Camera.PictureCallback jpeg = (data, c) -> emitLast(data, sub);
      camera.takePicture(shutter, raw, jpeg);
    });
  }

  static @NonNull Camera.Parameters params(@NonNull Camera.Parameters ps) {
    final List<String> focuses = ps.getSupportedFocusModes();
    for (String s : focuses) {
      Timber.d("available focus %s", s);
    }
    for (Camera.Size size : ps.getSupportedPreviewSizes()) {
      Timber.d("available size %d x %d", size.width, size.height);
    }

    ps.setFocusMode(focusMode(focuses));
    ps.set("cam_mode", 1); // don't even ask
    ps.setPreviewSize(WIDTH, HEIGHT);
    ps.setPictureSize(WIDTH, HEIGHT);

    return ps;
  }

  static @NonNull String focusMode(@NonNull List<String> focuses) {
    final String auto = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
    final String fixed = Camera.Parameters.FOCUS_MODE_FIXED;
    final String focus;
    if (focuses.contains(auto)) {
      focus = auto;
    } else if (focuses.contains(fixed)) {
      focus = fixed;
    } else {
      focus = focuses.get(0);
    }
    return focus;
  }
}
