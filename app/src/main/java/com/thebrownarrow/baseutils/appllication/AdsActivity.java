package com.thebrownarrow.baseutils.appllication;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.thebrownarrow.baseutils.ui.BaseActivity;

/**
 * @author The Brown Arrow.
 * Email - thebrownarrow@gmail.com
 */
public class AdsActivity extends BaseActivity {

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
        initializeAds(getString(R.string.ad_app_id));
        loadInterstitialAd(getString(R.string.interstitial_ad_id));
        loadBannerAd(R.id.linearLayoutAdView, getString(R.string.banner_ad_id), AdSize.SMART_BANNER);
    }

    public void openAds(View view) {
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
}
