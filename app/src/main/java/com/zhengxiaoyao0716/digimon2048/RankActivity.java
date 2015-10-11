package com.zhengxiaoyao0716.digimon2048;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;
import com.zhengxiaoyao0716.adapter.RankExpandableLA;
import com.zhengxiaoyao0716.data.Records;
import com.zhengxiaoyao0716.net.GetRanks;
import org.json.JSONArray;

public class RankActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
		
		ActionBar actionBar = getActionBar();
		if (actionBar!=null)
			actionBar.setDisplayHomeAsUpEnabled(true);

		loadMyRecords();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_rank, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				break;

			case R.id.myRecord:
				loadMyRecords();
				break;

			case R.id.dayRank:
				loadNetRankList(1, 500, true);
				break;

			case R.id.weekRank:
				loadNetRankList(7, 1000, true);
				break;

			case R.id.lowestRank:
				loadNetRankList(7, 500, false);
				break;
		}
		return true;
	}

	private void loadMyRecords() {
		JSONArray recordListJA = new Records(this).getRecordsList();
		if (recordListJA.length() == 0) {
			Toast.makeText(this, R.string.getRankFailed, Toast.LENGTH_LONG).show();
			return;
		}
		ExpandableListView rankExpandableList
				= (ExpandableListView) findViewById(R.id.rankExpandableList);
		rankExpandableList.setAdapter(
				new RankExpandableLA(RankActivity.this, recordListJA));
		rankExpandableList.expandGroup(0);
	}
	private void loadNetRankList(int days, int number, boolean isDESC) {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("请稍候...");
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();

		new Thread(new GetRanks(new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.obj != null) {
					JSONArray rankListJA = (JSONArray) msg.obj;
					ExpandableListView rankExpandableList
							= (ExpandableListView) findViewById(R.id.rankExpandableList);
					rankExpandableList.setAdapter(
							new RankExpandableLA(RankActivity.this, rankListJA));
					rankExpandableList.expandGroup(0);
				}
				else Toast.makeText(
						RankActivity.this, R.string.getRankFailed, Toast.LENGTH_LONG).show();
				progressDialog.dismiss();
			}
		}, days, number, isDESC)).start();
	}
}
