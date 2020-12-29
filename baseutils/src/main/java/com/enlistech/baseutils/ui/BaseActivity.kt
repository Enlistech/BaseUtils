package com.enlistech.baseutils.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.enlistech.baseutils.BuildConfig
import com.enlistech.baseutils.R
import com.enlistech.baseutils.util.NetworkUtils
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardedVideoAd

/**
 * @author Jaydip Umaretiya.
 * Email - jaydip@enlistech.com
 */
abstract class BaseActivity : AppCompatActivity() {

    private var mProgressDialog: ProgressDialog? = null
    var deviceHeight = 0
    var deviceWidth = 0
    var adRequest: AdRequest? = null
    var interstitialAd: InterstitialAd? = null
    var rewardedVideoAd: RewardedVideoAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isActionBarOverlay) {
            requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
            supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        deviceHeight = displayMetrics.heightPixels
        deviceWidth = displayMetrics.widthPixels
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        if (hasAds()) {
            adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("9EBF8E91088F72D589CB4161B66206FF") // JD - Moto G4 Plus
                .addTestDevice("C8E9C41B3FEE9E5CACB4388A1B2AFECA") // Anku - Moto G5 Plus
                .build()
        }
        setContent()
    }

    protected abstract fun setContent()
    protected val isActionBarOverlay: Boolean
        protected get() = false

    protected open fun hasAds(): Boolean {
        return false
    }

    val isNetworkConnected: Boolean
        get() = if (!NetworkUtils.isNetworkConnected(this@BaseActivity)) {
            showErrorToast(getString(R.string.error_internet))
            false
        } else {
            true
        }

    fun showErrorToast(errorMsg: String?) {
        Toast.makeText(this@BaseActivity, errorMsg, Toast.LENGTH_LONG).show()
    }

    fun showSuccessToast(successMsg: String?) {
        Toast.makeText(this@BaseActivity, successMsg, Toast.LENGTH_SHORT).show()
    }

    fun printLog(tag: String?, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, "--------------------------$message")
        }
    }

    protected fun setLinearRecyclerView(
        recyclerView: RecyclerView,
        reverseLayout: Boolean
    ): RecyclerView {
        val linearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, reverseLayout)
        recyclerView.setHasFixedSize(false)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = linearLayoutManager
        return recyclerView
    }

    protected fun setGridRecyclerView(recyclerView: RecyclerView, spanCount: Int): RecyclerView {
        val gridLayoutManager = GridLayoutManager(this, spanCount)
        recyclerView.setHasFixedSize(false)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = gridLayoutManager
        return recyclerView
    }

    fun showLoading(message: String?) {
        mProgressDialog = ProgressDialog(this@BaseActivity)
        mProgressDialog!!.setMessage(message)
        mProgressDialog!!.isIndeterminate = true
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.show()
    }

    fun hideLoading() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.cancel()
        }
    }

    fun initializeAds(adsAppId: String?) {
        MobileAds.initialize(this, adsAppId)
    }

    fun loadBannerAd(linearLayout: Int, bannerAdId: String?, adSize: AdSize?) {
        val adView = AdView(this)
        val linearLayoutAdView = findViewById<LinearLayout>(linearLayout)
        adView.adUnitId = bannerAdId
        adView.adSize = adSize
        adView.loadAd(adRequest)
        adView.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                linearLayoutAdView.visibility = View.GONE
            }

            override fun onAdFailedToLoad(i: Int) {
                super.onAdFailedToLoad(i)
                linearLayoutAdView.visibility = View.GONE
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                linearLayoutAdView.visibility = View.VISIBLE
            }
        }
        linearLayoutAdView.addView(adView)
    }

    fun loadInterstitialAd(interstitialAdId: String?) {
        interstitialAd = InterstitialAd(this)
        interstitialAd!!.adUnitId = interstitialAdId
        interstitialAd!!.loadAd(adRequest)
    }

    fun initializeRewardedVideoAd() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
    }

    fun loadRewardedVideoAd(rewardedVideoAdId: String?) {
        rewardedVideoAd!!.loadAd(rewardedVideoAdId, adRequest)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}