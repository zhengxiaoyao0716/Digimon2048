package com.zhengxiaoyao0716.baidu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;
import com.baidu.appx.BDInterstitialAd;
import com.zhengxiaoyao0716.digimon2048.R;

public class BDInterstitialAdView {
    public static void showAdView(final Activity activity)
    {
        //加载中
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(activity.getString(R.string.adLoading));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final BDInterstitialAd appxInterstitialAdView = new BDInterstitialAd(activity,
                "THrH0P6Mu2XQkVXynC9akAVGRm0xjxf0", "Au4wkoOQNtaKdth0uijq8z3t");
        // 设置插屏广告行为监听器
        appxInterstitialAdView.setAdListener(new BDInterstitialAd.InterstitialAdListener() {
            @Override
            public void onAdvertisementDataDidLoadFailure() {
                progressDialog.dismiss();
                Toast.makeText(activity, R.string.loadFailed, Toast.LENGTH_LONG).show();
            }
            boolean alreadyShowed;
            @Override
            public void onAdvertisementDataDidLoadSuccess() {
                if (alreadyShowed) return;
                else alreadyShowed = true;
                progressDialog.dismiss();
                appxInterstitialAdView.showAd();
            }

            @Override
            public void onAdvertisementViewDidClick() {}
            @Override
            public void onAdvertisementViewDidHide() {}
            @Override
            public void onAdvertisementViewDidShow() {}
            @Override
            public void onAdvertisementViewWillStartNewIntent() {}
        });
        // 加载广告
        appxInterstitialAdView.loadAd();
    }
}
