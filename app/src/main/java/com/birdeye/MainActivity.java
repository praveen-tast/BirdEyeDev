package com.birdeye;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.birdeye.db.LocalTweet;
import com.birdeye.util.Globals;
import com.birdeye.util.SharedPref;
import com.birdeye.view.TextViews;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Libraries.CBLogging;
import com.chartboost.sdk.Model.CBError;
import com.f2prateek.rx.preferences.Preference;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.playlog.internal.LogEvent;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.twitter.sdk.android.Twitter;

import org.json.JSONException;

import io.realm.Realm;
import io.realm.RealmResults;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public final class MainActivity extends AppCompatActivity {
    public static
    @NonNull
    Intent create(@NonNull Context c) {
        return new Intent(c, MainActivity.class);
    }

    @Bind(R.id.usernames)
    TextInputEditText usernames;
    @Bind(R.id.hashtags)
    TextInputEditText hashtags;
    @Bind(R.id.message1)     TextInputEditText message1;
    @Bind(R.id.message2)     TextInputEditText message2;
    @Bind(R.id.message3)     TextInputEditText message3;
    @Bind(R.id.message4)     TextInputEditText message4;
    @Bind(R.id.message5)     TextInputEditText message5;
    @Bind(R.id.message6)     TextInputEditText message6;
    @Bind(R.id.message7)     TextInputEditText message7;
    @Bind(R.id.message8)     TextInputEditText message8;
    @Bind(R.id.message9)     TextInputEditText message9;
    @Bind(R.id.message10)    TextInputEditText message10;
    @Bind(R.id.message11)    TextInputEditText message11;
    @Bind(R.id.message12)    TextInputEditText message12;
    @Bind(R.id.message13)    TextInputEditText message13;
    @Bind(R.id.message14)    TextInputEditText message14;
    @Bind(R.id.message15)    TextInputEditText message15;
    @Bind(R.id.message16)    TextInputEditText message16;
    @Bind(R.id.message17)    TextInputEditText message17;
    @Bind(R.id.message18)    TextInputEditText message18;
    @Bind(R.id.message19)    TextInputEditText message19;
    @Bind(R.id.message20)    TextInputEditText message20;
    @Bind(R.id.message21)    TextInputEditText message21;
    @Bind(R.id.message22)    TextInputEditText message22;
    @Bind(R.id.message23)    TextInputEditText message23;
    @Bind(R.id.message24)    TextInputEditText message24;
    @Bind(R.id.message25)    TextInputEditText message25;


    @Bind(R.id.cameras)
    Spinner cameras;
    @SuppressWarnings("NullableProblems")
    @NonNull
    SwitchCompat enabled;
    String pausedTimerValue, checkPausedTime;

    @NonNull
    List<TextInputEditText> ets = Collections.emptyList();

    static
    @NonNull
    TextInputLayout textLayout(@NonNull TextInputEditText et) {
        return (TextInputLayout) et.getParent();
    }

    AdRequest adRequest;
    TextView tv_cancel;
    boolean counterStarted = false;
    Button tvSetTimer;

    private static final String TAG = "paymentExample";
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     * <p/>
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * <p/>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AZztOl9WOTprWL2joVgkZscpCdz9c2KlhVNaVTYk7KPDYwV8niRtsxlTptbY0hyuw58khRG45wqnwNt-";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    private AdView mAdView;
    InterstitialAd mInterstitialAd;


    private static final String LOG_TAG = "BillingCheck";
    private static final String SUBSCRIPTION_ID = "com.birdeyecamera.subs1";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtZh7no87sL6hdmPK/Q+BK7aGXemW2dLmXF2FZdxNT+OX13idbtPBRqz4WiU7dxQL+f1BIShPQ4P/tIFOxlOJZEDgzn8118yqumJNplXbUM8EfwaP7ljjw9K2tWjQsptGyxT0vAl3st8w8kSGMBUyOMFuavUk7SKGhQXqPIQKZCcIukU0KBchM5y2Pg9AQeSL+Qv++SjEkbtO8EKAWudoITvzusJUN8abSdBBEdPkGArGagJn4QImiklEdlzboambkFJrZQUrv3bga/MWp6rtG8i4zvT47+cZEP6Rzc+GHSumVgDVH4gW4AHyahc/InUWB6fmVq4hPQNJWFkQdiLujQIDAQAB"; // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;

    private BillingProcessor bp;
    private boolean readyToPurchase = false;
    SkuDetails subs;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Prefs.usernames.isSet()) {
            Prefs.usernames.set(Twitter.getSessionManager().getActiveSession().getUserName());
        }

        setContentView(R.layout.main);
        ButterKnife.bind(this);

       // hideSystemUI(this);
        Chartboost.startWithAppId(MainActivity.this, "57552756f6cd4526e365b9d1", "239b2e3aa657ccdcdf0b9674c8750f077b5e0aed");
        Chartboost.setLoggingLevel(CBLogging.Level.ALL);
        Chartboost.setDelegate(delegate);
        Chartboost.onCreate(this);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tvSetTimer = (Button) findViewById(R.id.tvSetTimer);

        if(!BillingProcessor.isIabServiceAvailable(MainActivity.this)) {
           // showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
            onBackPressed();
        }


        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                showToast("onProductPurchased: " + productId);
               // updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, Throwable error) {
                showToast("onBillingError: " + Integer.toString(errorCode));
            }
            @Override
            public void onBillingInitialized() {
                showToast("onBillingInitialized");
                readyToPurchase = true;
                //updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                //updateTextViews();
                Log.i(LOG_TAG,String.format("%s is%s subscribed", SUBSCRIPTION_ID, bp.isSubscribed(SUBSCRIPTION_ID) ? "" : " not"));
            }
        });

        subs = bp.getSubscriptionListingDetails(SUBSCRIPTION_ID);
       // showToast(subs != null ? subs.toString() : "Failed to load subscription details");



        if (subs == null){
          //  showToast(subs != null ? subs.toString() : "Failed to load subscription details");
            Globals.hasPaid= false;
        }
        else {
            Globals.hasPaid = true;
        }


        if (Globals.hasPaid) {
            tvSetTimer.setVisibility(View.VISIBLE);

            message4.setVisibility(View.VISIBLE);
            message5.setVisibility(View.VISIBLE);
            message6.setVisibility(View.VISIBLE);
            message7.setVisibility(View.VISIBLE);
            message8.setVisibility(View.VISIBLE);
            message9.setVisibility(View.VISIBLE);
            message10.setVisibility(View.VISIBLE);
            message11.setVisibility(View.VISIBLE);
            message12.setVisibility(View.VISIBLE);
            message13.setVisibility(View.VISIBLE);
            message14.setVisibility(View.VISIBLE);
            message15.setVisibility(View.VISIBLE);
            message16.setVisibility(View.VISIBLE);
            message17.setVisibility(View.VISIBLE);
            message18.setVisibility(View.VISIBLE);
            message19.setVisibility(View.VISIBLE);
            message20.setVisibility(View.VISIBLE);
            message21.setVisibility(View.VISIBLE);
            message22.setVisibility(View.VISIBLE);
            message23.setVisibility(View.VISIBLE);
            message24.setVisibility(View.VISIBLE);
            message25.setVisibility(View.VISIBLE);

            ets = Arrays.asList(usernames, hashtags, message1, message2, message3, message4, message5, message6, message7,
                    message8, message9, message10, message11, message12, message13, message14, message15, message16, message17,
                    message18, message19, message20, message21, message22, message23, message24, message25);

        } else {
            tvSetTimer.setVisibility(View.GONE);
            message4.setVisibility(View.GONE);
            message5.setVisibility(View.GONE);
            message6.setVisibility(View.GONE);
            message7.setVisibility(View.GONE);
            message8.setVisibility(View.GONE);
            message9.setVisibility(View.GONE);
            message10.setVisibility(View.GONE);
            message11.setVisibility(View.GONE);
            message12.setVisibility(View.GONE);
            message13.setVisibility(View.GONE);
            message14.setVisibility(View.GONE);
            message15.setVisibility(View.GONE);
            message16.setVisibility(View.GONE);
            message17.setVisibility(View.GONE);
            message18.setVisibility(View.GONE);
            message19.setVisibility(View.GONE);
            message20.setVisibility(View.GONE);
            message21.setVisibility(View.GONE);
            message22.setVisibility(View.GONE);
            message23.setVisibility(View.GONE);
            message24.setVisibility(View.GONE);
            message25.setVisibility(View.GONE);
            ets = Arrays.asList(usernames, hashtags, message1, message2, message3);
        }

        tvSetTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.input_time);
                dialog.setCancelable(true);


                Button bt_set = (Button) dialog.findViewById(R.id.bt_set);
              final EditText et_time = (EditText) dialog.findViewById(R.id.et_time);



                bt_set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(et_time.getText().toString().equalsIgnoreCase("")){
                            Toast.makeText(MainActivity.this, "Please enter time to auto shut down", Toast.LENGTH_SHORT).show();
                        }

                        else {

                            int valueTimer = Integer.parseInt(et_time.getText().toString()) * 1000;

                            initateCounter(valueTimer);
                            tv_cancel.setText("Auto ShutDown started");
                            tvSetTimer.setVisibility(View.GONE);
                            tv_cancel.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }


                    }
                });


                dialog.show();


            }
        });


        cameras.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Facing.values()));
        //noinspection ConstantConditions
        cameras.setSelection(Prefs.facing.get().ordinal());
        cameras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Prefs.facing.set(Facing.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        for (TextInputEditText et : ets) {
            DrawableCompat.setTint(et.getBackground(), ContextCompat.getColor(this, R.color.colorAccent));
            final TextInputLayout til = textLayout(et);
            til.setError(null);
            til.setErrorEnabled(false);
        }

        TextViews.setText(usernames, Prefs.usernames.get());
        TextViews.setText(hashtags, Prefs.hashtags.get());
        TextViews.setText(message1, Prefs.message1.get());
        TextViews.setText(message2, Prefs.message2.get());
        TextViews.setText(message3, Prefs.message3.get());

        TextViews.setText(message4, Prefs.message4.get());
        TextViews.setText(message5, Prefs.message5.get());
        TextViews.setText(message6, Prefs.message6.get());
        TextViews.setText(message7, Prefs.message7.get());
        TextViews.setText(message8, Prefs.message8.get());
        TextViews.setText(message9, Prefs.message9.get());
        TextViews.setText(message10, Prefs.message10.get());
        TextViews.setText(message11, Prefs.message11.get());
        TextViews.setText(message12, Prefs.message12.get());
        TextViews.setText(message13, Prefs.message13.get());
        TextViews.setText(message14, Prefs.message14.get());
        TextViews.setText(message15, Prefs.message15.get());
        TextViews.setText(message16, Prefs.message16.get());
        TextViews.setText(message17, Prefs.message17.get());
        TextViews.setText(message18, Prefs.message18.get());
        TextViews.setText(message19, Prefs.message19.get());
        TextViews.setText(message20, Prefs.message20.get());
        TextViews.setText(message21, Prefs.message21.get());
        TextViews.setText(message22, Prefs.message22.get());
        TextViews.setText(message23, Prefs.message23.get());
        TextViews.setText(message24, Prefs.message24.get());
        TextViews.setText(message25, Prefs.message25.get());


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setBackgroundDrawableResource(R.color.bg);


        mAdView = (AdView) findViewById(R.id.adView);
        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("EB02375D2DA62FFA0F6F145AD2302B3D")
                .build();

        // adRequest = new AdRequest.Builder().build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
             //   Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
              //  Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
              //  Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
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

        if (Globals.hasPaid) {
            tv_cancel.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
        } else {

            // initateCounter(120000);
            startRepeatingTask();
            mAdView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        enabled = (SwitchCompat) menu.findItem(R.id.switch_item).getActionView();
        enabled.setText(R.string.enable);
        //noinspection ConstantConditions
        enabled.setChecked(Prefs.enable.get());
        enabled.setOnCheckedChangeListener((x, checked) -> {
            if (canEnable()) {
                enable(checked);

                if (!Globals.hasPaid) {
                    if (counterStarted) {

                    } else {
                        initateCounter(15*60*1000);
                        counterStarted = true;
                    }

                }
                SharedPref.set_savedCards1(MainActivity.this, "true");


            } else {
                enable(false);
                enabled.setChecked(false);
                SharedPref.set_savedCards1(MainActivity.this, "false");
            }
        });
        //noinspection ConstantConditions
        initial(Prefs.enable.get());


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {
            case R.id.logout:

                fullReset();
                startActivity(LoginActivity.create(this));
                finish();

                return true;

            case R.id.About:

                final Dialog dialog2 = new Dialog(MainActivity.this);
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog2.setContentView(R.layout.about);
                dialog2.setCancelable(false);

                dialog2.show();

                return true;


            case R.id.ShareApp:

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>Hey, am using this really cool hashtag activated camera app. Get it here #birdeyecamera.</p>"));
                startActivity(Intent.createChooser(sharingIntent, "Share using"));

                return true;

            case R.id.Recommend:

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("BirdEyeCamera", "birdeyecamera@digitalbabi.es", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feature Recommendation");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));


                return true;


            case R.id.rateApp:

                final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

                if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
                    startActivity(rateAppIntent);
                } else {
    /* handle your error case: the device has no way to handle market urls */
                }


                return true;

            case R.id.RemoveAds:

                if (Globals.hasPaid) {
                    Toast.makeText(MainActivity.this, "You already are in a premium account", Toast.LENGTH_SHORT).show();

                } else {

                    removeAdsDialog();

                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeAdsDialog() {



        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_pre);

        TextView tv_pay15 = (TextView) dialog.findViewById(R.id.tv_pay15);

        tv_pay15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!BillingProcessor.isIabServiceAvailable(MainActivity.this)) {
                    showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
                }

                else {


                //    asdasd

                  //  onFuturePaymentPressed(v);
                    bp.subscribe(MainActivity.this,SUBSCRIPTION_ID);

                }

                dialog.dismiss();

            }
        });

        dialog.setCancelable(true);

        dialog.show();

    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    void fullReset() {
        service(false);
        //noinspection Convert2streamapi
        for (Preference<?> p : Prefs.loginData) {
            p.delete();
        }
        Realm.getDefaultInstance().executeTransactionAsync(Realm::deleteAll);
        Twitter.getSessionManager().clearActiveSession();
    }

    private void enable(boolean checked) {
        Prefs.enable.set(checked);
        service(checked);

    }

    void initial(boolean checked) {
        if (canEnable()) {
            service(checked);
            enabled.setChecked(checked);
        }
    }

    void service(boolean checked) {
        final Intent svc = EyeService.create(this);
        if (checked) {
            startService(svc);
        } else {
            stopService(svc);
        }
    }

    @OnClick(R.id.apply)
    void onClick() {

        if (Globals.hasPaid){
            Prefs.usernames.set(TextViews.trimmed(usernames));
            Prefs.hashtags.set(TextViews.trimmed(hashtags));
            Prefs.message1.set(TextViews.trimmed(message1));
            Prefs.message2.set(TextViews.trimmed(message2));
            Prefs.message3.set(TextViews.trimmed(message3));


            Prefs.message4.set(TextViews.trimmed(message4));
            Prefs.message5.set(TextViews.trimmed(message5));
            Prefs.message6.set(TextViews.trimmed(message6));
            Prefs.message7.set(TextViews.trimmed(message7));
            Prefs.message8.set(TextViews.trimmed(message8));
            Prefs.message9.set(TextViews.trimmed(message9));
            Prefs.message10.set(TextViews.trimmed(message10));
            Prefs.message11.set(TextViews.trimmed(message11));
            Prefs.message12.set(TextViews.trimmed(message12));
            Prefs.message13.set(TextViews.trimmed(message13));
            Prefs.message14.set(TextViews.trimmed(message14));
            Prefs.message15.set(TextViews.trimmed(message15));
            Prefs.message16.set(TextViews.trimmed(message16));
            Prefs.message17.set(TextViews.trimmed(message17));
            Prefs.message18.set(TextViews.trimmed(message18));
            Prefs.message19.set(TextViews.trimmed(message19));
            Prefs.message20.set(TextViews.trimmed(message20));
            Prefs.message21.set(TextViews.trimmed(message21));
            Prefs.message22.set(TextViews.trimmed(message22));
            Prefs.message23.set(TextViews.trimmed(message23));
            Prefs.message24.set(TextViews.trimmed(message24));
            Prefs.message25.set(TextViews.trimmed(message25));

        }
        else{
            Prefs.usernames.set(TextViews.trimmed(usernames));
            Prefs.hashtags.set(TextViews.trimmed(hashtags));
            Prefs.message1.set(TextViews.trimmed(message1));
            Prefs.message2.set(TextViews.trimmed(message2));
            Prefs.message3.set(TextViews.trimmed(message3));
        }



        if (canEnable()) {
            textLayout(usernames).setError(null);
            textLayout(usernames).setErrorEnabled(false);
            textLayout(hashtags).setError(null);
            textLayout(hashtags).setErrorEnabled(false);

            Realm.getDefaultInstance().executeTransactionAsync(realm -> {
                final RealmResults<LocalTweet> tweets = realm.where(LocalTweet.class)
                        .equalTo("replied", false)
                        .findAll();
                for (LocalTweet replied : tweets) {
                    replied.deleteFromRealm();
                }
            });
            Prefs.lastId.set(null);

            //noinspection ConstantConditions
            if (Prefs.enable.get()) {
                final Intent svc = EyeService.create(this);
                stopService(svc);
                startService(svc);

                startActivity(new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } else {
            final String err = getString(R.string.at_least_one_user);
            textLayout(usernames).setError(err);
            textLayout(hashtags).setError(err);

            Prefs.enable.set(false);
            initial(false);
        }
    }

    boolean canEnable() {
        return !TextUtils.isEmpty(Prefs.usernames.get())
                || !TextUtils.isEmpty(Prefs.hashtags.get());
    }


    @Override
    public void onStart() {
        super.onStart();
        Chartboost.onStart(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Chartboost.onResume(this);
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Chartboost.onPause(this);
        if (mAdView != null) {
            mAdView.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Chartboost.onStop(this);
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
        Chartboost.onDestroy(this);
        if (mAdView != null) {
            mAdView.destroy();
        }

    }

    @Override
    public void onBackPressed() {
        // If an interstitial is on screen, close it.
        if (Chartboost.onBackPressed())
            return;
        else
            super.onBackPressed();
    }

    //-------------------METHODS FOR ADS-------------


    private ChartboostDelegate delegate = new ChartboostDelegate() {

        @Override
        public boolean shouldRequestInterstitial(String location) {
            Log.i(TAG, "SHOULD REQUEST INTERSTITIAL '" + (location != null ? location : "null"));
            return true;
        }

        @Override
        public boolean shouldDisplayInterstitial(String location) {
            Log.i(TAG, "SHOULD DISPLAY INTERSTITIAL '" + (location != null ? location : "null"));
            return true;
        }

        @Override
        public void didCacheInterstitial(String location) {
            Log.i(TAG, "DID CACHE INTERSTITIAL '" + (location != null ? location : "null"));
        }

        @Override
        public void didPauseClickForConfirmation(Activity activity) {
            super.didPauseClickForConfirmation(activity);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setMessage("Are 18 or older?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Chartboost.didPassAgeGate(true);
                        }
                    });
            alertDialogBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Chartboost.didPassAgeGate(false);
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        @Override
        public void didFailToLoadInterstitial(String location, CBError.CBImpressionError error) {
            Log.i(TAG, "DID FAIL TO LOAD INTERSTITIAL '" + (location != null ? location : "null") + " Error: " + error.name());
            //  Toast.makeText(getApplicationContext(), "INTERSTITIAL '"+location+"' REQUEST FAILED - " + error.name(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didDismissInterstitial(String location) {
            Log.i(TAG, "DID DISMISS INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public void didCloseInterstitial(String location) {
            Log.i(TAG, "DID CLOSE INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public void didClickInterstitial(String location) {
            Log.i(TAG, "DID CLICK INTERSTITIAL: " + (location != null ? location : "null"));
        }

        @Override
        public void didDisplayInterstitial(String location) {
            Log.i(TAG, "DID DISPLAY INTERSTITIAL: " + (location != null ? location : "null"));
        }


    };

    public void onLoadButtonClick() {
        Chartboost.cacheInterstitial(CBLocation.LOCATION_DEFAULT);
        Chartboost.showInterstitial(CBLocation.LOCATION_DEFAULT);
    }

    Handler mHandler = new Handler();
    int mInterval = 1000*60*2;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {

                if (Globals.hasPaid) {

                } else if (!Globals.hasPaid) {


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onLoadButtonClick();
                        }
                    }, 60*1000);

                    mInterstitialAd.loadAd(adRequest);
                }


            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();

    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }



    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public void initateCounter(int timer) {

        // 900000 15 min
        new CountDownTimer(timer, 1000) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {

                Log.e("millisUntilFinished", ""+millisUntilFinished);
                String pausedTimerValue = ""+millisUntilFinished;


                String timerText = "The app closes in " + String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + " you can remove shut off timer by upgrading for only & $2.99 ";

                tv_cancel.setText(timerText + Html.fromHtml("<p><u>SUBSCRIBE</u></p>"));
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeAdsDialog();
                    }
                });
                // tv_cancel.setText(timerText);
            }

            public void onFinish() {


                if(!Globals.hasPaid){

                    enabled.setChecked(false);
                    enable(false);
                    enabled.setChecked(false);
                    initial(false);
                    MainActivity.this.finish();
                    System.exit(0);
                    if (SharedPref.get_savedCards1(MainActivity.this).equalsIgnoreCase("true")){
                    }


                }

            }

        }.start();
    }



}
