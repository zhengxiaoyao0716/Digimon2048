package com.zhengxiaoyao0716.digimon2048;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RankActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
		loadRanking();
		onTodayClick();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true); 
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		finish();
		return true;
	}
	
	private void onTodayClick()
	{
		ListView rankingList = (ListView)findViewById(R.id.rankingList);
		String[] playerName = {"wait", "wait"};
		ArrayAdapter<String> rankingAdapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_expandable_list_item_1, playerName);
		rankingList.setAdapter(rankingAdapter);
	}
	
	private void loadRanking()
	{
		///**/
	}
}
