package com.zhengxiaoyao0716.digimon2048;

import java.util.ArrayList;
import java.util.List;

import com.zhengxiaoyao0716.adapter.GuidePagerAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity implements OnClickListener, OnPageChangeListener{

    //引导图片资源
    private int[] pics;
    //布局资源
    private List<View> views;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        //选择引导图片
        pics = new int[]{ R.mipmap.guide0, R.mipmap.guide1, R.mipmap.guide2 };

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        views = new ArrayList<>();
        //初始化引导图片列表
        for (int picId : pics) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(picId);
            views.add(iv);
        }
        viewPager = (ViewPager) findViewById(R.id.guideViewPager);
        //初始化Adapter
        viewPager.setAdapter(new GuidePagerAdapter(views));
        //绑定回调
        viewPager.setOnPageChangeListener(this);

        //初始化底部小点
        initDots();

    }

    //底部小店图片
    private ImageView[] dots ;
    //记录当前选中位置
    private int currentIndex;
    private void initDots() {
        LinearLayout dotLinearLayout = (LinearLayout) findViewById(R.id.dotLinearLayout);

        dots = new ImageView[pics.length];

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 0);
        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.dot);
            dots[i].setLayoutParams(layoutParams);
            dots[i].setEnabled(true);//都设为灰色
            dots[i].setClickable(true);
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
            dotLinearLayout.addView(dots[i]);
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);//设置为白色，即选中状态
    }

    /**
     *设置当前的引导页
     */
    private void setCurView(int position)
    {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     *这只当前引导小点的选中
     */
    private void setCurDot(int position)
    {
        if (position < 0 || position >= pics.length || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }

    //当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {}

    //当当前页面被滑动时调用
    boolean isEnd;
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        //末页跳转，这个比起循环滚动简单很多
        if (isEnd&&arg0==pics.length - 1) {
            getSharedPreferences("Settings", MODE_PRIVATE).edit()
                    .putBoolean("firstUse", false).commit();
            finish();
        }
        else isEnd = (arg0==pics.length - 1);
    }

    //当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        //设置底部小点选中状态
        setCurDot(arg0);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurView(position);
        setCurDot(position);
    }
}