package com.birdeye.util;

import android.support.annotation.NonNull;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public final class Observables {
  public static <T> void emit(@NonNull T t, @NonNull Subscriber<? super T> sub) {
    if (!sub.isUnsubscribed()) {
      sub.onNext(t);
    }
  }

  public static <T> void emitLast(@NonNull T result, @NonNull Subscriber<? super T> sub) {
    if (sub.isUnsubscribed()) {
      return;
    }
    sub.onNext(result);
    sub.onCompleted();
  }

  public static void emitError(@NonNull Throwable e, @NonNull Subscriber<?> sub) {
    if (sub.isUnsubscribed()) {
      return;
    }
    sub.onError(e);
  }

  public static @NonNull Observable<Long> eb(@NonNull Observable<? extends Throwable> ts,
                                             int count,
                                             long min,
                                             @NonNull TimeUnit tu) {
    return ts.zipWith(Observable.range(1, count), (throwable, integer) -> integer)
        .flatMap(c -> {
          final double n = Math.pow(2.0, c) - 1;
          final long k = Math.round(Math.random() * n);

          final long exponent = min * k;
          return Observable.timer(exponent, tu, Schedulers.immediate());
        });
  }
}
