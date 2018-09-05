package com.thebrownarrow.baseutils.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * @author The Brown Arrow.
 * Email - thebrownarrow@gmail.com
 */
public abstract class BaseFragment extends Fragment {

    private View rootView;
    private ProgressDialog mProgressDialog;
    public InterstitialAd interstitialAd;
    public AdRequest adRequest;
    public RewardedVideoAd rewardedVideoAd;

    protected abstract int setFragmentLayout();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(setFragmentLayout(), container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setRootView(view);

        if (hasAds()) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("9EBF8E91088F72D589CB4161B66206FF") // JD - Moto G4 Plus
                    .addTestDevice("C8E9C41B3FEE9E5CACB4388A1B2AFECA") // Anku - Moto G5 Plus
                    .build();
        }

        setContent(view);

        super.onViewCreated(view, savedInstanceState);
    }

    protected abstract void setContent(View rootView);

    protected boolean hasAds() {
        return false;
    }

    public boolean isNetworkConnected() {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            showErrorToast(getString(R.string.error_internet));
            return false;
        } else {
            return true;
        }
    }

    public void showErrorToast(String errorMsg) {
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
    }

    public void showSuccessToast(String successMsg) {
        Toast.makeText(getActivity(), successMsg, Toast.LENGTH_SHORT).show();
    }

    public void printLog(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, "--------------------------" + message);
        }
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    protected View getRootView() {
        return rootView;
    }

    protected RecyclerView setLinearRecyclerView(RecyclerView recyclerView, int orientation, boolean reverseLayout,
                                                 boolean hasFixedSize, boolean nestedScrollingEnabled) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), orientation, reverseLayout);
        recyclerView.setHasFixedSize(hasFixedSize);
        recyclerView.setNestedScrollingEnabled(nestedScrollingEnabled);
        recyclerView.setLayoutManager(linearLayoutManager);
        return recyclerView;
    }

    protected RecyclerView setGridRecyclerView(RecyclerView recyclerView, int orientation, boolean reverseLayout, int spanCount,
                                               boolean hasFixedSize, boolean nestedScrollingEnabled) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount, orientation, reverseLayout);
        recyclerView.setHasFixedSize(hasFixedSize);
        recyclerView.setNestedScrollingEnabled(nestedScrollingEnabled);
        recyclerView.setLayoutManager(gridLayoutManager);
        return recyclerView;
    }

    public void showLoading(String message) {
        mProgressDialog = new ProgressDialog(getActivity());
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
        MobileAds.initialize(getActivity(), adsAppId);
    }

    public void loadBannerAd(int linearLayout, String bannerAdId, AdSize adSize) {
        AdView adView = new AdView(getActivity());
        final LinearLayout linearLayoutAdView = getRootView().findViewById(linearLayout);

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
        interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdUnitId(interstitialAdId);
        interstitialAd.loadAd(adRequest);
    }

    public void initializeRewardedVideoAd() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
    }

    public void loadRewardedVideoAd(String rewardedVideoAdId) {
        rewardedVideoAd.loadAd(rewardedVideoAdId, adRequest);
    }

    public void pushFragmentWithBackStack(Fragment DestinationFragment) {
        try {
            Fragment SourceFragment = this;
            int viewResourceID = ((ViewGroup) SourceFragment.getView().getParent()).getId();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(viewResourceID, DestinationFragment);
            ft.hide(SourceFragment);
            ft.addToBackStack(SourceFragment.getClass().getName());
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void replaceFragment(Fragment DestinationFragment) {
        Fragment SourceFragment = this;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        int viewResourceID = ((ViewGroup) SourceFragment.getView().getParent()).getId();
        ft.replace(viewResourceID, DestinationFragment);
        ft.commit();
    }

    static public void replaceFragmentInContainer(Fragment DestinationFragment, int containerResourceID, FragmentManager fragmentManager) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        int viewResourceID = containerResourceID;
        ft.replace(viewResourceID, DestinationFragment);
        ft.commit();
    }

    public boolean popFragment(Fragment SourceFragment) {
        getFragmentManager().popBackStack();
        return true;
    }
}
