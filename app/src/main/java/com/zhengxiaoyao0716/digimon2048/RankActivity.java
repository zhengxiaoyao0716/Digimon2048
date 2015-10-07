package com.zhengxiaoyao0716.digimon2048;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.zhengxiaoyao0716.data.Records;
import com.zhengxiaoyao0716.data.SqlRecords;

import java.util.Collections;
import java.util.Comparator;
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

		loadMyRecord();
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
				loadMyRecord();
				break;

			case R.id.dayRank:
			{
			}break;

			case R.id.weekRank:
			{
			}break;

			case R.id.lowestRank:
			{
			}break;
		}
		return true;
	}

	private void loadMyRecord() {
		//rankListData = Records.getRecordList(this);
		rankListData = new SqlRecords(this).list();
		if (rankListData == null) {
			Toast.makeText(this, R.string.nullRecords, Toast.LENGTH_LONG).show();
			return;
		}
		refreshList();
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
