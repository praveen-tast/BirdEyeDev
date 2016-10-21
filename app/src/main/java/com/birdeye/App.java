package com.birdeye;

import android.app.Application;
import android.support.annotation.NonNull;
import com.crashlytics.android.core.CrashlyticsCore;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

public final class App extends Application {
  @SuppressWarnings("NullableProblems") public static @NonNull App app;

  @Override public void onCreate() {
    app = this;
    super.onCreate();

    TwitterAuthConfig authConfig = new TwitterAuthConfig("GZuUgCh2cHEjKJS6XFTmmMqzP",
                                                         "9Xd3zDMyGBJZ7RDY3Wo5sHwHK5hWGNDFPp8DRwuXEHoXKHkY5m");
    Fabric.with(this, new Twitter(authConfig), new CrashlyticsCore());

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    Realm.setDefaultConfiguration(new RealmConfiguration.Builder(this)
                                      .deleteRealmIfMigrationNeeded()
                                      .build());
  }
}
