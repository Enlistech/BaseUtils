package com.enlistech.baseutils.util

import android.content.Context
import android.view.View
import app.nativeads.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.UnifiedNativeAd

object Ads {

    @JvmStatic
    fun showBannerAd(context: Context, adView: AdView) {
        MobileAds.initialize(context) {}
//            googleAdView.adSize = AdSize.SMART_BANNER
//            googleAdView.adUnitId = context.getString(R.string.SMART_BANNER_ad_id)
        adView.loadAd(AdRequest.Builder().build())
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }
    }

    @JvmStatic
    fun showNativeAd(context: Context, templateView: TemplateView) {
        MobileAds.initialize(context)
        val adLoader: AdLoader =
            AdLoader.Builder(context, "")
                .forUnifiedNativeAd { unifiedNativeAd: UnifiedNativeAd? ->
                    templateView.visibility = View.VISIBLE
                    templateView.setNativeAd(unifiedNativeAd)
                }.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    @JvmStatic
    fun showInterstitialAd(context: Context): InterstitialAd? {
        val interstitialAd = InterstitialAd(context)
        interstitialAd.adUnitId = ""
        interstitialAd.loadAd(AdRequest.Builder().build())
        return interstitialAd
    }
}