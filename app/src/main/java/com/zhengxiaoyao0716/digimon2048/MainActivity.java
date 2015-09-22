package com.zhengxiaoyao0716.digimon2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
		case R.id.shareItem:
		{
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT,
			getString(R.string.shareString));
			startActivity(intent);
		}break;
		
		case R.id.aboutItem:
		{
			AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
			aboutDialog.setTitle(getString(R.string.about));
			String copyright = getString(R.string.copyright);
			aboutDialog.setMessage(copyright);
			aboutDialog.setNegativeButton(getString(R.string.attentionMe),
			new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface d, int i)
				{
					Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://blog.sina.cn/zhengxiaoyao0716"));
					startActivity(intent);
				}
			});
			aboutDialog.setPositiveButton(getString(R.string.iKnow), null);
			aboutDialog.show();
		}break;
		
		case R.id.exitItem:
		{
			finish();
		}break;
		
		}
		return true;
	}
	public void onStartClick(View view)
	{
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
		finish();
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
		int rand = R.string.helpString0+(int)(Math.random()*3);
		String helpString = getString(rand);
		Toast helpToast = Toast.makeText(this, helpString, Toast.LENGTH_SHORT);
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
