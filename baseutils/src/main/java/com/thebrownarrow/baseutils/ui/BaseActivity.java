package com.thebrownarrow.baseutils.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.thebrownarrow.baseutils.BuildConfig;
import com.thebrownarrow.baseutils.R;
import com.thebrownarrow.baseutils.util.NetworkUtils;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    public int deviceHeight;
    public int deviceWidth;
    public AdRequest adRequest;
    public InterstitialAd interstitialAd;
    public RewardedVideoAd rewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isActionBarOverlay()) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
            supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);

        if (hasAds()) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("9EBF8E91088F72D589CB4161B66206FF") // JD - Moto G4 Plus
                    .addTestDevice("C8E9C41B3FEE9E5CACB4388A1B2AFECA") // Anku - Moto G5 Plus
                    .build();
        }

        setContent();
    }

    protected abstract void setContent();

    protected boolean isActionBarOverlay() {
        return false;
    }

    protected boolean hasAds() {
        return false;
    }

    public boolean isNetworkConnected() {
        if (!NetworkUtils.isNetworkConnected(BaseActivity.this)) {
            showErrorToast(getString(R.string.error_internet));
            return false;
        } else {
            return true;
        }
    }

    public void showErrorToast(String errorMsg) {
        Toast.makeText(BaseActivity.this, errorMsg, Toast.LENGTH_LONG).show();
    }

    public void showSuccessToast(String successMsg) {
        Toast.makeText(BaseActivity.this, successMsg, Toast.LENGTH_SHORT).show();
    }

    public void printLog(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, "--------------------------" + message);
        }
    }

    protected RecyclerView setLinearRecyclerView(RecyclerView recyclerView, boolean reverseLayout) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, reverseLayout);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        return recyclerView;
    }

    protected RecyclerView setGridRecyclerView(RecyclerView recyclerView, int spanCount) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        return recyclerView;
    }

    public void showLoading(String message) {
        mProgressDialog = new ProgressDialog(BaseActivity.this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    public void initializeAds(String adsAppId) {
        MobileAds.initialize(this, adsAppId);
    }

    public void loadBannerAd(int linearLayout, String bannerAdId, AdSize adSize) {
        AdView adView = new AdView(this);
        final LinearLayout linearLayoutAdView = findViewById(linearLayout);

        adView.setAdUnitId(bannerAdId);
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                linearLayoutAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                linearLayoutAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                linearLayoutAdView.setVisibility(View.VISIBLE);
            }
        });
        linearLayoutAdView.addView(adView);
    }

    public void loadInterstitialAd(String interstitialAdId) {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(interstitialAdId);
        interstitialAd.loadAd(adRequest);
    }

    public void initializeRewardedVideoAd() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
    }

    public void loadRewardedVideoAd(String rewardedVideoAdId) {
        rewardedVideoAd.loadAd(rewardedVideoAdId, adRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
