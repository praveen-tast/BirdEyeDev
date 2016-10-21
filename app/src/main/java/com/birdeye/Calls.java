package com.birdeye;

import android.os.SystemClock;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birdeye.db.LocalTweet;
import com.birdeye.util.Observables;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public final class Calls {
  public static @NonNull Observable<Result<Search>> search(@NonNull String s,
                                                           @Nullable Long since) {
    return Observable.create(sub -> {
      TwitterCore.getInstance().getApiClient().getSearchService()
          .tweets(s, null, null, null, null, null, null, null, null, null, cb(sub));
    });
  }

  public static @NonNull Observable<Result<Tweet>> reply(@NonNull String msg,
                                                         @NonNull LocalTweet replyTo,
                                                         @NonNull TypedFile tf) {
    Timber.d("replying");
    return upload(tf)
        .flatMap(x -> reply(String.format("@%s %s", replyTo.getScreenName(), msg),
                            replyTo.getId(),
                            x.data.mediaIdString));
  }

  static @NonNull Observable<Result<Media>> upload(@NonNull TypedFile tf) {
    return Observable.create(sub -> {
      Timber.d("uploading...");
      TwitterCore.getInstance().getApiClient().getMediaService()
          .upload(tf, null, null, cb(sub));
    });
  }

  static @NonNull Observable<Result<Tweet>> reply(@NonNull String text,
                                                  long replyId,
                                                  @NonNull String mediaId) {
    return Observable.create(sub -> {
      Timber.d("replying...");
      TwitterCore.getInstance().getApiClient().getStatusesService()
          .update(text, replyId, null, null, null, null, null, null, mediaId, cb(sub));
    });
  }

  static @NonNull <T> Callback<T> cb(@NonNull Subscriber<? super Result<T>> sub) {
    final long start = SystemClock.elapsedRealtime();
    return new Callback<T>() {
      @MainThread @Override public void success(@NonNull Result<T> result) {
        final long end = SystemClock.elapsedRealtime() - start;
        final Response r = result.response;
        Timber.d("%s %d %s (%d ms)", r.getReason(), r.getStatus(), r.getUrl(), end);
        Observables.emitLast(result, sub);
      }

      @MainThread @Override public void failure(@NonNull TwitterException e) {
        Timber.e(e, "");
        Observables.emitError(e, sub);
      }
    };
  }
}
