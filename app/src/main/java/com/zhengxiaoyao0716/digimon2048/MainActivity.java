package com.zhengxiaoyao0716.digimon2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.zhengxiaoyao0716.baidu.BDBannerAdView;
import com.zhengxiaoyao0716.dialog.EditInfoDialog;
import com.zhengxiaoyao0716.sound.Sounds;

import java.util.Random;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Sounds.INSTANCE.initSounds(this);
		SharedPreferences preferences
				= getSharedPreferences("Settings", MODE_PRIVATE);
		if (preferences.getString("playerName", "Unknown")
				.equals("Unknown"))
			EditInfoDialog.editInfo(this);
		if (preferences.getBoolean("isBannerAdShowing", false))
			BDBannerAdView.showAdView(this);
		//百度更新检测
		BDAutoUpdateSDK.uiUpdateAction(this, new UICheckUpdateCallback() {
			@Override
			public void onCheckComplete() {}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Sounds.INSTANCE.releaseSounds();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.editItem:
				EditInfoDialog.editInfo(this);
				break;
			case R.id.aboutItem: {
				final SharedPreferences preferences
						= getSharedPreferences("Settings", MODE_PRIVATE);
				final boolean isBannerAdShowing
						= preferences.getBoolean("isBannerAdShowing", false);
				new AlertDialog.Builder(this).setTitle(R.string.aboutDialogTitle)
						.setMessage(getString(isBannerAdShowing ?
								R.string.adIsShowing : R.string.adWasClosed)
								+ getString(R.string.copyright))
						.setNegativeButton(R.string.attentionMe,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface d, int i) {
										Intent intent = new Intent(Intent.ACTION_VIEW,
												Uri.parse("http://zhengxiaoyao0716.lofter.com/"));
										startActivity(intent);
									}
								})
						.setPositiveButton(isBannerAdShowing ?
										R.string.closeAd : R.string.supportMe,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface d, int i) {
										if (isBannerAdShowing)
											BDBannerAdView.closeAdView(MainActivity.this);
										else
											BDBannerAdView.showAdView(MainActivity.this);
										preferences.edit().putBoolean("isBannerAdShowing",
												!isBannerAdShowing).commit();
									}
								}).show();
			}break;
			case R.id.exitItem:
				finish();
				break;
		}
		return true;
	}

	public void onStartClick(View view)
	{
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
	}
	
	public void onRankClick(View view)
	{
		Intent intent = new Intent(this, RankActivity.class);
		startActivity(intent);
	}
	public void onHelpClick(View view)
	{
		final ImageView helpButton =
		(ImageView)findViewById(R.id.helpButton);
		helpButton.setImageResource(R.mipmap.help1);
		Toast helpToast = Toast.makeText(this,
				R.string.helpString0 + new Random().nextInt(3),
				Toast.LENGTH_SHORT);
		helpToast.setGravity(Gravity.CENTER, 0, 0);
		helpToast.show();
		new Handler().postDelayed(new Runnable(){
			public void run()
			{
				helpButton.setImageResource(R.mipmap.help0);
			}
		}, 2000); 
	}
}
