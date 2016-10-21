package com.birdeye.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import com.birdeye.MainActivity;
import com.birdeye.R;
import com.twitter.sdk.android.core.models.Tweet;
import java.util.Locale;

public final class Notifications {
  public static @NonNull Notification service(@NonNull Context ctx) {
    final PendingIntent pi = PendingIntent.getActivity(ctx, 123, MainActivity.create(ctx), 0);
    return new NotificationCompat.Builder(ctx)
        .setSmallIcon(R.drawable.ic_camera)
        .setTicker(ctx.getString(R.string.notification_ticker))
        .setContentTitle(ctx.getString(R.string.notification_title))
        .setContentText(ctx.getString(R.string.notification_text))
        .setContentIntent(pi)
        .build();
  }

  public static @NonNull Notification tweet(@NonNull Context ctx,
                                            @NonNull Tweet lt,
                                            @NonNull Bitmap b) {
    final String title = ctx.getString(R.string.tweet_sent);
    final String text = ctx.getString(R.string.tweet_text);
    final PendingIntent pi = PendingIntent.getActivity(ctx, (int) lt.getId(), intent(url(lt)), 0);
    return new NotificationCompat.Builder(ctx)
        .setSmallIcon(R.drawable.ic_photo)
        .setTicker(title)
        .setContentTitle(title)
        .setContentText(text)
        .setContentIntent(pi)
        .setStyle(new NotificationCompat.BigPictureStyle()
                      .setBigContentTitle(title)
                      .setSummaryText(text)
                      .bigPicture(b))
        .setAutoCancel(true)
        .build();
  }

  static @NonNull Intent intent(@NonNull String url) {
    return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
  }

  static @NonNull String url(@NonNull Tweet lt) {
    return String.format(Locale.US, "https://twitter.com/%s/status/%d",
                         lt.user.screenName,
                         lt.getId());
  }
}
