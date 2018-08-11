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
import com.thebrownarrow.baseutils.R;
import com.thebrownarrow.baseutils.util.NetworkUtils;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    private View rootView;
    private ProgressDialog mProgressDialog;
    public InterstitialAd interstitialAd;
    public AdRequest adRequest;

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
                    .addTestDevice("951A389D53ED3891F7FE6B5F981048E8") // Moto G4 Plus
                    .addTestDevice("13BF45B66CBEA5DD87E3ADF5F941FFE0") // Moto E Ankita
                    .addTestDevice("1E737E5DD483267E0A5CD5D50786ABA2") // Moto E Jaydip
                    .addTestDevice("41661F52A3C37891FE88331B6E46EFE1") // Moto G5 Plus
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

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    protected View getRootView() {
        return rootView;
    }

    protected RecyclerView setLinearRecyclerView(RecyclerView recyclerView, boolean reverseLayout) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, reverseLayout);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        return recyclerView;
    }

    protected RecyclerView setRecyclerView(RecyclerView recyclerView, int spanCount) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
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
