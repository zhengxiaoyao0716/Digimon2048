package com.zhengxiaoyao0716.digimon2048;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.zhengxiaoyao0716.data.Records;
import com.zhengxiaoyao0716.net.GetRankList;

import java.util.List;
import java.util.Map;

public class RankActivity extends Activity
{

	private List<? extends Map<String,?>> rankListData;
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
				loadNetRankList(1, 50, true);
				break;

			case R.id.weekRank:
				loadNetRankList(7, 100, true);
				break;

			case R.id.lowestRank:
				loadNetRankList(7, 50, false);
				break;
		}
		return true;
	}

	private void loadMyRecords() {
		rankListData = new Records(this).getRecordsList();
		if (rankListData == null) {
			Toast.makeText(this, R.string.nullRecords, Toast.LENGTH_LONG).show();
			return;
		}
		refreshList();
	}
	private void loadNetRankList(int days, int number, boolean isDESC) {
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("请稍候...");
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();

		new Thread(new GetRankList(new Handler(){
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				if (msg.obj != null) {
					rankListData = (List<? extends Map<String, ?>>) msg.obj;
					refreshList();
				}
				progressDialog.dismiss();
			}
		}, days, number, isDESC)).start();
	}
	private void refreshList()
	{
		ListView rankList = (ListView) findViewById(R.id.rankList);
		rankList.setAdapter(new SimpleAdapter(this, rankListData, R.layout.list_item_rank,
				new String[]{"number", "score", "name", "time"},
				new int[]{
						R.id.rankListItemNumber,
						R.id.rankListItemScore,
						R.id.rankListItemName,
						R.id.rankListItemTime
				}));
	}
}
