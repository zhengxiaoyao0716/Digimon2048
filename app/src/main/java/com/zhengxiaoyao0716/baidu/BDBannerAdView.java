package com.zhengxiaoyao0716.baidu;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.baidu.appx.BDBannerAd;
import com.zhengxiaoyao0716.digimon2048.R;

public class BDBannerAdView {
    private static BDBannerAd bannerAdView;
    public static void showAdView(final Activity activity)
    {
        // 创建广告视图
        // 发布时请使用正确的ApiKey和广告位ID
        // 此处ApiKey和推广位ID均是测试用的
        // 您在正式提交应用的时候，请确认代码中已经更换为您应用对应的Key和ID
        // 具体获取方法请查阅《百度开发者中心交叉换量产品介绍.pdf》
        bannerAdView = new BDBannerAd(activity,
                "THrH0P6Mu2XQkVXynC9akAVGRm0xjxf0", "PMv9HYDpUD8QAdzcpo3jYmaG");

        // 设置横幅广告行为监听器
        bannerAdView.setAdListener(new BDBannerAd.BannerAdListener() {

            @Override
            public void onAdvertisementDataDidLoadFailure() {
                Toast.makeText(activity, R.string.loadFailed, Toast.LENGTH_LONG).show();
                activity.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit().putBoolean("isBannerAdShowing", false).commit();
            }

            boolean alreadyShowed;
            @Override
            public void onAdvertisementDataDidLoadSuccess() {
                if (alreadyShowed) return;
                else alreadyShowed = true;
                Toast.makeText(activity, R.string.loadSucceed, Toast.LENGTH_LONG).show();}
            @Override
            public void onAdvertisementViewDidClick() {}
            @Override
            public void onAdvertisementViewDidShow() {}
            @Override
            public void onAdvertisementViewWillStartNewIntent() {}
        });

        ((RelativeLayout) activity.findViewById(R.id.bannerAdRl))
                .addView(bannerAdView);
    }
    public static void closeAdView(final Activity activity)
    {
        ((RelativeLayout) activity.findViewById(R.id.bannerAdRl))
                .removeView(bannerAdView);
        bannerAdView.destroy();
        bannerAdView = null;
    }
}
