package com.birdeye.db;

import android.support.annotation.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class LocalTweet extends RealmObject {
  private @PrimaryKey long    id;
  private @Required   String  screenName;
  private             boolean replied;

  @NonNull public String getScreenName() {
    return screenName;
  }

  public void setScreenName(@NonNull String screenName) {
    this.screenName = screenName;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public boolean isReplied() {
    return replied;
  }

  public void setReplied(boolean replied) {
    this.replied = replied;
  }
}
