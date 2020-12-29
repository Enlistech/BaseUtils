package app.enlistech.basic.utils

import android.os.Bundle
import android.view.View
import com.enlistech.baseutils.ui.BaseActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize

/**
 * @author Jaydip Umaretiya.
 * Email - jaydip@enlistech.com
 */
class AdsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)
    }

    override fun hasAds(): Boolean {
        return true
    }

    override fun setContent() {
        // Don't forgot to initialize
        initializeAds(getString(R.string.ad_app_id))

        // Load the banner ads
        loadBannerAd(R.id.linearLayoutAdView, getString(R.string.banner_ad_id), AdSize.SMART_BANNER)

        // Load the interstitial ads
        loadInterstitialAd(getString(R.string.interstitial_ad_id))

        // Load the rewarded video ads
        initializeRewardedVideoAd()
        //        loadRewardedVideoAd(getString(R.string.rewarded_video_id));
//        rewardedVideoAd.setRewardedVideoAdListener(this);
    }

    fun openInterstitialAds(view: View?) {
        if (interstitialAd != null && interstitialAd.isLoaded) {
            interstitialAd.show()
            interstitialAd.adListener = object : AdListener() {
                override fun onAdLoaded() {}
                override fun onAdClosed() {
                    interstitialAd.loadAd(adRequest)
                }
            }
        }
    }

    fun openRewordVideoAds(view: View?) {
        if (rewardedVideoAd.isLoaded) {
            rewardedVideoAd.show()
        }
    }

    public override fun onResume() {
        rewardedVideoAd.resume(this)
        super.onResume()
    }

    public override fun onPause() {
        rewardedVideoAd.pause(this)
        super.onPause()
    }

    public override fun onDestroy() {
        rewardedVideoAd.destroy(this)
        super.onDestroy()
    }
}