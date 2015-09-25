package com.zhengxiaoyao0716.syncdialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 同步的对话框.
 * @author 正逍遥0716
 */
public class SyncDialog {
    private PopupWindow popupWindow;
    private View parent;
    private SyncDialog() {}

    /**
     * 在默认的位置显示同步对话框.<br>
     *     即屏幕的中心位置。<br>
     */
    public void show() {
        popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    /**
     * 获取this的PopupWindow视图.
     * @return PopupWindow视图
     */
    public PopupWindow asPopupWindow()
    {
        return popupWindow;
    }

    /**
     * SyncDialog的构造类.
     */
    public static class Builder {
        private final PopupWindow popupWindow;
        private final Context context;

        private View titleView;
        private View messageView;
        private View buttonView;

        private boolean cancelable;
        private Drawable backgroundDrawable;

        /**
         * 模仿AlterDialog的构造类.
         * @param context 上下文
         */
        public Builder(@NonNull Context context) {
            popupWindow = new PopupWindow();
            this.context = context;

            cancelable = true;
            backgroundDrawable = new ColorDrawable(Color.WHITE);
        }

        /**
         * 通过字符串的id设置标题，内部调用了setTitle(View titleView);.
         * @param titleId 标题字符串的id，如{@code R.string.title}.
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(@StringRes int titleId) {
            return setTitle(context.getString(titleId));
        }
        /**
         * 设置标题为字符序列，内部调用了setTitle(View titleView);.
         * @param title 标题字符序列
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(CharSequence title)
        {
            TextView titleTV = new TextView(context);
            titleTV.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            titleTV.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            titleTV.setText(title);
            return setTitle(titleTV);
        }
        /**
         * 设置标题为自定义的View.
         * @param titleView 标题View
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(View titleView) {
            this.titleView = titleView;
            return this;
        }

        /**
         * 通过字符串id设置文字内容，setMessage(View messageView);.
         * @param messageId 内容字符串的id，如{@code R.string.message}
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(@StringRes int messageId) {
            return setMessage(context.getString(messageId));
        }
        /**
         * 设置内容为字符串序列，内部调用了setMessage(View messageView);.
         * @param message 内容字符序列
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(CharSequence message)
        {
            TextView messageTV = new TextView(context);
            messageTV.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            messageTV.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            messageTV.setText(message);
            return setMessage(messageTV);
        }
        /**
         * 设置内容为自定义的View.
         * @param messageView 内容View
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(View messageView)
        {
            this.messageView = messageView;
            return this;
        }

        /**
         * 通过字符串id为底部栏添加一个按钮，请避免和setButtons();一同使用.<br>
         *     如果之前调用过setButtons();并传入了非LinearLayout的View<br>
         *         再调用该方法会导致其被覆盖为一个空的LinearLayout<br>
         * @param textId 字符串id
         * @param onClickListener 监听器
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder addButton(@StringRes int textId, final View.OnClickListener onClickListener)
        {
            return addButton(context.getString(textId), onClickListener);
        }
        /**
         * 通过字符序列为底部栏添加一个按钮，请避免和setButtons();一同使用.<br>
         *     如果之前调用过setButtons();并传入了非LinearLayout的View<br>
         *         再调用该方法会导致其被覆盖为一个空的LinearLayout<br>
         * @param text 字符序列
         * @param onClickListener 监听器
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder addButton(CharSequence text, final View.OnClickListener onClickListener)
        {
            LinearLayout buttonLL;
            if (buttonView != null && buttonView instanceof LinearLayout)
                buttonLL = (LinearLayout) buttonView;
            else
            {
                //可能会覆盖掉冲突的底部栏
                buttonLL = new LinearLayout(context);
                buttonView = buttonLL;
                buttonLL.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                buttonLL.setLayoutParams(layoutParams);
            }
            Button button = new Button(context);
            button.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            button.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
            button.setText(text);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v);
                    popupWindow.dismiss();
                }
            });
            buttonLL.addView(button);
            return this;
        }
        /**
         * 设置自定义底部栏，请避免和addButton();一同使用.<br>
         *     该方法会完全覆盖addButton();方法的效果<br>
         * @param buttonView 自定义的底部栏
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setButtons(View buttonView)
        {
            this.buttonView = buttonView;
            return this;
        }

        /**
         * 对话框是否可被取消.<br>
         * @param cancelable 默认true
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * 通过Drawable的id设置背景.
         * @param drawableId 图片的id
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setBackgroundDrawable(@DrawableRes int drawableId)
        {
            if (Build.VERSION.SDK_INT >= 21)
                backgroundDrawable = context.getDrawable(drawableId);
            else //noinspection deprecation
                backgroundDrawable = context.getResources().getDrawable(drawableId);
            return this;
        }
        /**
         * 设置背景.
         * @param drawable 作为背景的图片
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setBackgroundDrawable(Drawable drawable)
        {
            backgroundDrawable = drawable;
            return this;
        }

        /**
         * 创建并返回SyncDialog实例.
         * @return SyncDialog实例
         */
        public SyncDialog create() {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout popupWindowLL = new LinearLayout(context);
            popupWindowLL.setLayoutParams(layoutParams);
            popupWindowLL.setOrientation(LinearLayout.VERTICAL);
            if (titleView != null) popupWindowLL.addView(titleView);
            if (messageView != null) popupWindowLL.addView(messageView);
            if (buttonView != null) popupWindowLL.addView(buttonView);

            popupWindow.setContentView(popupWindowLL);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(backgroundDrawable);
            popupWindow.setOutsideTouchable(cancelable);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                }
            });

            SyncDialog syncDialog = new SyncDialog();
            syncDialog.popupWindow = popupWindow;
            syncDialog.parent = popupWindowLL;
            return syncDialog;
        }

        /**
         * 创建、显示并返回SyncDialog实例.
         * @return SyncDialog实例
         */
        public SyncDialog show() {
            SyncDialog syncDialog = create();
            syncDialog.show();
            return syncDialog;
        }
    }
}
