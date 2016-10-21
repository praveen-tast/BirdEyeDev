package com.birdeye.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.birdeye.Prefs;
import com.f2prateek.rx.preferences.Preference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class Queries {
  static final Pattern SPLIT = Pattern.compile("[ ,]");

  public static @NonNull String query(@NonNull String usernames, @NonNull String hashtags) {
    return String.format("%s -filter:retweets", queryStr(usernames, hashtags));
  }

  public static @NonNull String reply() {
    final int round = (int) (Math.round(Prefs.messages.size() * Math.random()) - 1);
    final int index = Math.max(0, round);
    final String msg = Prefs.messages.get(index).get();
    if (msg != null) {
      return msg;
    }
    for (Preference<String> message : Prefs.messages) {
      final String str = message.get();
      if (!TextUtils.isEmpty(str)) {
        return str;
      }
    }
    return "";
  }

  static @NonNull List<String> split(@NonNull String uss, String prefix) {
    final ArrayList<String> usernames = new ArrayList<>();
    for (String s : SPLIT.split(uss)) {
      if (!s.trim().isEmpty()) {
        usernames.add(prefix + s.replace(prefix, ""));
      }
    }
    return usernames;
  }

  static @NonNull String queryStr(@NonNull String usernames, @NonNull String hashtags) {
    final StringBuilder builder = new StringBuilder();
    for (String s : Queries.split(usernames, "@")) {
      builder.append(" ").append(s).append(" AND");
    }
    for (String s : Queries.split(hashtags, "#")) {
      builder.append(" ").append(s).append(" AND");
    }

    final int len = builder.length();
    if (len >= 4) {
      return builder.substring(1, len - 4);
    } else {
      return builder.toString();
    }
  }
}
