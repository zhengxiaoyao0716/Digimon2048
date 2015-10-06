package com.zhengxiaoyao0716.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.zhengxiaoyao0716.adapter.ChoosePagerAdapter;
import com.zhengxiaoyao0716.digimon2048.R;

import java.util.ArrayList;
import java.util.Random;

public abstract class ChooseDigimonDialog {
    Context context;
    private static final int DIGIMON_NUMS = 14;

    public ChooseDigimonDialog(Context context)
    {
        this.context = context;
    }

    //等待chooseDigimon后执行
    public abstract void doAfterChoose();

    //选择数码宝贝
    public void chooseDigimon(final int[] digimons, final int posInDigimons)
    {
        final ArrayList<Integer> digimonArray = new ArrayList<>(DIGIMON_NUMS);
        for (int i = 1; i <= DIGIMON_NUMS; i++) digimonArray.add(i);
        for (int index = 0; index < posInDigimons; index++)
            digimonArray.remove((Integer)digimons[index]);

        final ArrayList<View> digimonViews = new ArrayList<>();
        for (int digimon : digimonArray) {
            String imageName
                    ="grid" + digimon;
            ImageView digimonIV = new ImageView(context);
            digimonIV.setImageResource(context.getResources().getIdentifier(imageName,
                    "mipmap", "com.zhengxiaoyao0716.digimon2048"));
            digimonViews.add(digimonIV);
        }
        //首位插入末位的图片
        ImageView tempDigimonIV = new ImageView(context);
        tempDigimonIV .setImageDrawable(
                ((ImageView) digimonViews.get(digimonViews.size() - 1)).getDrawable());
        digimonViews.add(0, tempDigimonIV);
        //末尾增加首位（现在是次位）的图片
        tempDigimonIV = new ImageView(context);
        tempDigimonIV .setImageDrawable(
                ((ImageView) digimonViews.get(1)).getDrawable());
        digimonViews.add(tempDigimonIV);

        //加载布局
        View chooseDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_choose_digimon, null);
        final AlertDialog chooseAD = new AlertDialog.Builder(context)
                .setTitle(R.string.pleaseChoose).setView(chooseDialogView)
                .setCancelable(false).create();

        //设置ViewPager
        final ViewPager chooseVP = (ViewPager) chooseDialogView.findViewById(R.id.chooseViewPager);
        chooseVP.setAdapter(new ChoosePagerAdapter(digimonViews));
        chooseVP.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //其实是digimonViews.size() - 2，也就是digimonArray - 1 + 1;
                //第二个参数false。默认true是平滑过渡，但现在其实已经过渡完成，自然应当是false
                if (position == 0 && positionOffset == 0) chooseVP.setCurrentItem(digimonArray.size(), false);
                else if (position == digimonViews.size() - 1 && positionOffset == 0)
                    chooseVP.setCurrentItem(1, false);
                else if (positionOffset == 0) digimons[posInDigimons] = digimonArray.get(position - 1);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //从随机位置开始，重要的是一定不能是首末位置（0、digimonViews.size() - 1)
        chooseVP.setCurrentItem(1 + new Random().nextInt(digimonArray.size()));

        //设置Button
        View.OnClickListener OnChooseButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.prevDigimonButton:
                        chooseVP.setCurrentItem(chooseVP.getCurrentItem() - 1);
                        break;
                    case R.id.chooseThisButton:
                        chooseAD.dismiss();
                        doAfterChoose();
                        break;
                    case R.id.nextDigimonButton:
                        chooseVP.setCurrentItem(chooseVP.getCurrentItem() + 1);
                        break;
                }
            }
        };
        chooseDialogView.findViewById(R.id.prevDigimonButton).setOnClickListener(OnChooseButtonClick);
        chooseDialogView.findViewById(R.id.chooseThisButton).setOnClickListener(OnChooseButtonClick);
        chooseDialogView.findViewById(R.id.nextDigimonButton).setOnClickListener(OnChooseButtonClick);

        //展示Dialog
        chooseAD.show();
    }
}
