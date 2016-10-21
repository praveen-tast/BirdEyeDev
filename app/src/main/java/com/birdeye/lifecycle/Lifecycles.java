package com.birdeye.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

public final class Lifecycles {
  public static @NonNull Observable<Lifecycle> create(@NonNull Activity a) {
    return Observable.create(sub -> {
      final Application.ActivityLifecycleCallbacks alc =
          new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, @Nullable Bundle savedInstanceState) {
              sub.onNext(Lifecycle.CREATE);
            }

            @Override public void onActivityStarted(Activity activity) {
              sub.onNext(Lifecycle.START);
            }

            @Override public void onActivityResumed(Activity activity) {
              sub.onNext(Lifecycle.RESUME);
            }

            @Override public void onActivityPaused(Activity activity) {
              sub.onNext(Lifecycle.PAUSE);
            }

            @Override public void onActivityStopped(Activity activity) {
              sub.onNext(Lifecycle.STOP);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, @NonNull Bundle outState) { }

            @Override public void onActivityDestroyed(Activity activity) {
              sub.onNext(Lifecycle.DESTROY);
              sub.onCompleted();
            }
          };
      sub.add(Subscriptions.create(
          () -> a.getApplication().unregisterActivityLifecycleCallbacks(alc)));
      a.getApplication().registerActivityLifecycleCallbacks(alc);
    });
  }

  public static final Func1<Lifecycle, Boolean> DESTROYS = l -> l == Lifecycle.DESTROY;
}
