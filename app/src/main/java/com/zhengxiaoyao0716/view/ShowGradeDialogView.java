package com.zhengxiaoyao0716.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.zhengxiaoyao0716.data.Records;
import com.zhengxiaoyao0716.digimon2048.R;

public class ShowGradeDialogView {
    public static View getGradeDialogView(Context context, int level, int score)
    {
        View gradeDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_view_show_grade, null);
        ((TextView) gradeDialogView.findViewById(R.id.yourLevel)).setText(
                String.format(context.getString(R.string.yourLevel), level));
        ((TextView) gradeDialogView.findViewById(R.id.yourScore)).setText(
                String.format(context.getString(R.string.yourScore), score));

        //写入一次游戏记录
        long time = System.currentTimeMillis();
        new Records(context).insert(level, score, time);
        //TODO 提交在线成绩并获取排名
        int rank = 0;
        ((TextView) gradeDialogView.findViewById(R.id.yourRank)).setText(
                context.getString(R.string.yourRank) + (rank == 0 ? "?????" : rank));
        return gradeDialogView;
    }
}
