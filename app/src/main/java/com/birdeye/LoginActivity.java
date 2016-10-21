package com.birdeye;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.birdeye.lifecycle.Lifecycles;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public final class LoginActivity extends AppCompatActivity {
	
  public static Intent create(Context ctx) {
    return new Intent(ctx, LoginActivity.class);
  }

  @SuppressWarnings("NullableProblems") @NonNull TwitterLoginButton loginButton;
  private AdView mAdView;
  private String TAG = LoginActivity.class.getSimpleName();
  InterstitialAd mInterstitialAd;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    if (Twitter.getSessionManager().getActiveSession() != null) {
      //noinspection ConstantConditions
      //  getSupportActionBar().hide();
      Observable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
              .takeUntil(Lifecycles.create(this).filter(Lifecycles.DESTROYS))
              .subscribe(x -> go());
      return;
    }



    setContentView(R.layout.login);
    //noinspection ConstantConditions
    loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
    loginButton.setCallback(new Callback<TwitterSession>() {
      @Override public void success(@NonNull Result<TwitterSession> result) {
        go();
      }

      @Override public void failure(@NonNull TwitterException e) { }
    });
    getWindow().setBackgroundDrawableResource(R.color.bg);



    mAdView = (AdView) findViewById(R.id.adView);
    mInterstitialAd = new InterstitialAd(this);

    // set the ad unit ID
    mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

    AdRequest adRequest = new AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            // Check the LogCat to get your test device ID
            .addTestDevice("EB02375D2DA62FFA0F6F145AD2302B3D")
            .build();

    mAdView.setAdListener(new AdListener() {
      @Override
      public void onAdLoaded() {
      }

      @Override
      public void onAdClosed() {
        Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onAdFailedToLoad(int errorCode) {
        Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onAdLeftApplication() {
        Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onAdOpened() {
        super.onAdOpened();
      }
    });

    mAdView.loadAd(adRequest);

    mInterstitialAd.loadAd(adRequest);

    mInterstitialAd.setAdListener(new AdListener() {
		
      public void onAdLoaded() {
        showInterstitial();
      }

      @Override
      public void onAdClosed() {
        Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onAdFailedToLoad(int errorCode) {
        Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onAdLeftApplication() {
        Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onAdOpened() {
        Toast.makeText(getApplicationContext(), "Ad is opened!", Toast.LENGTH_SHORT).show();
      }
    });

  }

  void go() {
    startActivity(MainActivity.create(this));
    finish();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    loginButton.onActivityResult(requestCode, resultCode, data);
  }


  @Override
  public void onPause() {
    if (mAdView != null) {
      mAdView.pause();
    }
    super.onPause();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mAdView != null) {
      mAdView.resume();
    }
  }

  @Override
  public void onDestroy() {
    if (mAdView != null) {
      mAdView.destroy();
    }
    super.onDestroy();
  }

  private void showInterstitial() {
    if (mInterstitialAd.isLoaded()) {
      mInterstitialAd.show();
    }
  }

}