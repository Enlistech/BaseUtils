package com.thebrownarrow.baseutils.appllication;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.thebrownarrow.baseutils.ui.BaseActivity;

/**
 * @author The Brown Arrow.
 * Email - thebrownarrow@gmail.com
 */
public class AdsActivity extends BaseActivity implements RewardedVideoAdListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
    }

    @Override
    protected boolean hasAds() {
        return true;
    }

    @Override
    protected void setContent() {
        // Don't forgot to initialize
        initializeAds(getString(R.string.ad_app_id));

        // Load the banner ads
        loadBannerAd(R.id.linearLayoutAdView, getString(R.string.banner_ad_id), AdSize.SMART_BANNER);

        // Load the interstitial ads
        loadInterstitialAd(getString(R.string.interstitial_ad_id));

        // Load the rewarded video ads
        initializeRewardedVideoAd();
        loadRewardedVideoAd(getString(R.string.rewarded_video_id));
        rewardedVideoAd.setRewardedVideoAdListener(this);
    }

    public void openInterstitialAds(View view) {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {

                }

                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(adRequest);
                }
            });
        }
    }

    public void openRewordVideoAds(View view) {
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd(getString(R.string.rewarded_video_id));
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    public void onResume() {
        rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        rewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}
