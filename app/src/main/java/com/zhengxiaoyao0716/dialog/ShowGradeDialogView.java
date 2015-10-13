package com.zhengxiaoyao0716.dialog;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.zhengxiaoyao0716.baidu.BDInterstitialAdView;
import com.zhengxiaoyao0716.digimon2048.R;
import com.zhengxiaoyao0716.net.FindPosition;

public class ShowGradeDialogView {
    public static View getGradeDialogView(final Activity activity, int level, int score)
    {
        final View gradeDialogView = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_view_show_grade, null);
        final TextView yourRankView
                = ((TextView) gradeDialogView.findViewById(R.id.yourRank));
        yourRankView.setText(
                activity.getString(R.string.yourRank) + "waiting...");
        //获取在线排名位置
        new Thread(new FindPosition(new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                yourRankView.setText(activity.getString(R.string.yourRank) + msg.arg1);
            }
        }, level, score)).start();
        ((TextView) gradeDialogView.findViewById(R.id.yourLevel)).setText(
                String.format(activity.getString(R.string.yourLevel), level));
        ((TextView) gradeDialogView.findViewById(R.id.yourScore)).setText(
                String.format(activity.getString(R.string.yourScore), score));
        gradeDialogView.findViewById(R.id.openAdView).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BDInterstitialAdView.showAdView(activity);
                    }
                }
        );
        return gradeDialogView;
    }
}
