package com.enlistech.baseutils.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
abstract class BaseFragment : Fragment() {

    protected var rootView: View? = null
        set
    private var mProgressDialog: ProgressDialog? = null
    var interstitialAd: InterstitialAd? = null
    var adRequest: AdRequest? = null
    var rewardedVideoAd: RewardedVideoAd? = null
    protected abstract fun setFragmentLayout(): Int
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(setFragmentLayout(), container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rootView = view
        if (hasAds()) {
            adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("9EBF8E91088F72D589CB4161B66206FF") // JD - Moto G4 Plus
                .addTestDevice("C8E9C41B3FEE9E5CACB4388A1B2AFECA") // Anku - Moto G5 Plus
                .build()
        }
        setContent(view)
        super.onViewCreated(view, savedInstanceState)
    }

    protected abstract fun setContent(rootView: View?)
    protected fun hasAds(): Boolean {
        return false
    }

    val isNetworkConnected: Boolean
        get() = if (!NetworkUtils.isNetworkConnected(activity)) {
            showErrorToast(getString(R.string.error_internet))
            false
        } else {
            true
        }

    fun showErrorToast(errorMsg: String?) {
        Toast.makeText(activity, errorMsg, Toast.LENGTH_LONG).show()
    }

    fun showSuccessToast(successMsg: String?) {
        Toast.makeText(activity, successMsg, Toast.LENGTH_SHORT).show()
    }

    fun printLog(tag: String?, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, "--------------------------$message")
        }
    }

    protected fun setLinearRecyclerView(
        recyclerView: RecyclerView, orientation: Int, reverseLayout: Boolean,
        hasFixedSize: Boolean, nestedScrollingEnabled: Boolean
    ): RecyclerView {
        val linearLayoutManager = LinearLayoutManager(activity, orientation, reverseLayout)
        recyclerView.setHasFixedSize(hasFixedSize)
        recyclerView.isNestedScrollingEnabled = nestedScrollingEnabled
        recyclerView.layoutManager = linearLayoutManager
        return recyclerView
    }

    protected fun setGridRecyclerView(
        recyclerView: RecyclerView, orientation: Int, reverseLayout: Boolean, spanCount: Int,
        hasFixedSize: Boolean, nestedScrollingEnabled: Boolean
    ): RecyclerView {
        val gridLayoutManager = GridLayoutManager(activity, spanCount, orientation, reverseLayout)
        recyclerView.setHasFixedSize(hasFixedSize)
        recyclerView.isNestedScrollingEnabled = nestedScrollingEnabled
        recyclerView.layoutManager = gridLayoutManager
        return recyclerView
    }

    fun showLoading(message: String?) {
        mProgressDialog = ProgressDialog(activity)
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
        MobileAds.initialize(activity, adsAppId)
    }

    fun loadBannerAd(linearLayout: Int, bannerAdId: String?, adSize: AdSize?) {
        val adView = AdView(activity)
        val linearLayoutAdView = rootView!!.findViewById<LinearLayout>(linearLayout)
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
        interstitialAd = InterstitialAd(activity)
        interstitialAd!!.adUnitId = interstitialAdId
        interstitialAd!!.loadAd(adRequest)
    }

    fun initializeRewardedVideoAd() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity)
    }

    fun loadRewardedVideoAd(rewardedVideoAdId: String?) {
        rewardedVideoAd!!.loadAd(rewardedVideoAdId, adRequest)
    }

    fun pushFragmentWithBackStack(DestinationFragment: Fragment?) {
        try {
            val SourceFragment: Fragment = this
            val viewResourceID = (SourceFragment.view!!.parent as ViewGroup).id
            val fragmentManager = fragmentManager
            val ft = fragmentManager!!.beginTransaction()
            ft.add(viewResourceID, DestinationFragment!!)
            ft.hide(SourceFragment)
            ft.addToBackStack(SourceFragment.javaClass.name)
            ft.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun replaceFragment(DestinationFragment: Fragment?) {
        val SourceFragment: Fragment = this
        val fragmentManager = fragmentManager
        val ft = fragmentManager!!.beginTransaction()
        val viewResourceID = (SourceFragment.view!!.parent as ViewGroup).id
        ft.replace(viewResourceID, DestinationFragment!!)
        ft.commit()
    }

    fun popFragment(SourceFragment: Fragment?): Boolean {
        fragmentManager!!.popBackStack()
        return true
    }

    companion object {
        fun replaceFragmentInContainer(
            DestinationFragment: Fragment?,
            containerResourceID: Int,
            fragmentManager: FragmentManager
        ) {
            val ft = fragmentManager.beginTransaction()
            ft.replace(containerResourceID, DestinationFragment!!)
            ft.commit()
        }
    }
}