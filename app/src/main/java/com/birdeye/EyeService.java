package com.birdeye;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.view.WindowManager;
import com.birdeye.db.LocalTweet;
import com.birdeye.util.Asserts;
import com.birdeye.util.Bitmaps;
import com.birdeye.util.Cameras;
import com.birdeye.util.Files;
import com.birdeye.util.Notifications;
import com.birdeye.util.Queries;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import io.realm.Realm;
import io.realm.RealmResults;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import retrofit.mime.TypedFile;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public final class EyeService extends Service {
  static final String WIFI_TAG           = "keep wifi on";
  static final String WAKE_TAG           = "keep cpu on";
  static final int    NOTIFICATION_ID    = 76234;
  static final int    SEARCH_INTERVAL_MS = 10;

  public static @NonNull Intent create(@NonNull Context ctx) {
    return new Intent(ctx, EyeService.class);
  }

  final @NonNull PublishSubject<EyeService> destroys = PublishSubject.create();
  final @NonNull AtomicBoolean              replying = new AtomicBoolean(false);

  static void save(@NonNull Search search, @NonNull Realm realm) {
    for (Tweet t : search.tweets) {
      final LocalTweet exist = realm.where(LocalTweet.class)
          .equalTo("id", t.id)
          .findFirst();
      if (exist == null) {
        final LocalTweet lt = realm.createObject(LocalTweet.class);
        lt.setId(t.id);
        lt.setScreenName(t.user.screenName);
      }
    }
  }

  @Nullable @Override public IBinder onBind(Intent intent) { return null; }

  @Override public void onCreate() {
    super.onCreate();

    final PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
    final PowerManager.WakeLock wake = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_TAG);
    wake.acquire();

    final WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    final WifiManager.WifiLock wifi = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_TAG);
    wifi.acquire();

    startForeground(NOTIFICATION_ID, Notifications.service(this));
    destroys.subscribe(x -> {
      stopForeground(true);
      wifi.release();
      wake.release();
    });

    Observable.interval(SEARCH_INTERVAL_MS, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
        .startWith(-1L)
        .flatMap(x -> {
          final String usernames = Prefs.usernames.get();
          final String hashtags = Prefs.hashtags.get();
          //noinspection ConstantConditions
          return Calls.search(Queries.query(usernames, hashtags),
                              Prefs.lastId.get())
              .onErrorResumeNext(y -> Observable.empty());
        })
        .takeUntil(destroys)
        .subscribe(x -> {
          final Search data = x.data;
          if (!data.tweets.isEmpty()) {
            Realm.getDefaultInstance().executeTransactionAsync(r -> save(data, r));
            Prefs.lastId.set(data.searchMetadata.maxId);
          }
        });

    Realm.getDefaultInstance()
        .where(LocalTweet.class)
        .equalTo("replied", false)
        .findAllSortedAsync("id")
        .asObservable()
        .filter(x -> {
          Timber.d("got %s", x);
          return !x.isEmpty() && !replying.get();
        })
        .flatMap(this::reply)
        .takeUntil(destroys)
        .subscribe();
  }

  @NonNull Observable<Result<Tweet>> reply(@NonNull @Size(min = 1) RealmResults<LocalTweet> t) {
    Asserts.mainThread();
    replying.set(true);

    final String name = String.format(Locale.US, "%d.jpg", System.currentTimeMillis());
    final File file = new File(Files.dir(this), name);

    return takePicture(file)
        .flatMap(b -> {
          Asserts.mainThread();

          final LocalTweet t1 = t.get(0);
          return Calls.reply(Queries.reply(), t1, new TypedFile("image/jpeg", file))
              .onErrorResumeNext(y -> Observable.empty())
              .doOnNext(x -> {
                final NotificationManager nm =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                final String tag = Long.toString(t1.getId());
                nm.notify(tag, (int) t1.getId(), Notifications.tweet(this, x.data, b));

                Realm.getDefaultInstance().executeTransaction(r -> t1.setReplied(true));
              });
        })
        .doOnTerminate(() -> replying.set(false));
  }

  @NonNull Observable<Bitmap> takePicture(@NonNull File file) {
    return Observable.fromCallable(() -> {
      Asserts.workerThread();
      final int rotation = getWindowManager().getDefaultDisplay().getRotation();
      final Facing facing = Prefs.facing.get();
      //noinspection ConstantConditions
      final CameraPicture data = Cameras.takePicture(facing);
      return Bitmaps.write(file, data, rotation);
    }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @NonNull WindowManager getWindowManager() {
    return (WindowManager) getSystemService(WINDOW_SERVICE);
  }

  @Override public void onDestroy() {
    destroys.onNext(this);
    super.onDestroy();
  }
}
