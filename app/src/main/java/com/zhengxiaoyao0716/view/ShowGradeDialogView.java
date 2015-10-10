package com.zhengxiaoyao0716.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.zhengxiaoyao0716.data.Records;
import com.zhengxiaoyao0716.digimon2048.R;
import com.zhengxiaoyao0716.net.GetPosition;

public class ShowGradeDialogView {
    public static View getGradeDialogView(Context context, int level, int score)
    {
        final View gradeDialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_view_show_grade, null);
        final TextView yourRankView
                = ((TextView) gradeDialogView.findViewById(R.id.yourRank));
        yourRankView.setText(
                context.getString(R.string.yourRank) + "waiting...");
        //获取在线排名位置
        new Thread(new GetPosition(new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                yourRankView.setText(msg.arg1);
            }
        }, level, score)).start();
        ((TextView) gradeDialogView.findViewById(R.id.yourLevel)).setText(
                String.format(context.getString(R.string.yourLevel), level));
        ((TextView) gradeDialogView.findViewById(R.id.yourScore)).setText(
                String.format(context.getString(R.string.yourScore), score));

        //写入本地游戏记录
        long time = System.currentTimeMillis();
        new Records(context).insert(level, score, time);
        //TODO 在线提交记录
        return gradeDialogView;
    }
}
