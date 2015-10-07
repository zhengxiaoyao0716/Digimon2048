package com.zhengxiaoyao0716.digimon2048;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Base64;
import com.zhengxiaoyao0716.data.SqlRecords;
import com.zhengxiaoyao0716.dialog.ChooseDigimonDialog;
import com.zhengxiaoyao0716.game2048.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengxiaoyao0716.sounds.Sounds;
import org.json.JSONArray;
import org.json.JSONObject;

public class GameActivity extends Activity {
	private Context context;
	private RelativeLayout gameRelativeLayout;
	private TextView levelTextView;
	private TextView scoreTextView;
	private GridLayout boardGrid;

	private int boardH, boardW, aimNum;
	private Game2048 game2048;
	private int[] digimons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		context = this;

		Drawable[] gameBackground = new Drawable[2];
		Resources resources = getResources();
		//随机取色
		int colorId = resources.getIdentifier("gameBgColor" + new Random().nextInt(9),
				"color", "com.zhengxiaoyao0716.digimon2048");
		gameBackground[0] = new ColorDrawable(resources.getColor(colorId));
		//被这倒霉玩意儿恶心到了，要是按网上的方法改API，只能越改越高。。。
		//而且在xml中也没法根据SDK版本来if。。。
		if (Build.VERSION.SDK_INT >= 21) {
			gameBackground[1] = getDrawable(R.mipmap.cover_body);
		}
		else //noinspection deprecation
			gameBackground[1] = resources.getDrawable(R.mipmap.cover_body);
		gameRelativeLayout = (RelativeLayout) findViewById(R.id.gameRelativeLayout);
		if (Build.VERSION.SDK_INT >= 16)
			gameRelativeLayout.setBackground(new LayerDrawable(gameBackground));
		else //noinspection deprecation
			gameRelativeLayout.setBackgroundDrawable(new LayerDrawable(gameBackground));

		//设置按钮的监听
		findViewById(R.id.replayButton).setOnClickListener(onButtonClickListener);
		findViewById(R.id.soundButton).setOnClickListener(onButtonClickListener);
		findViewById(R.id.offButton).setOnClickListener(onButtonClickListener);
		//初始化控件
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		scoreTextView = (TextView) findViewById(R.id.scoreTextView);
		boardGrid = (GridLayout) findViewById(R.id.boardGrid);
		boardGrid.setOnTouchListener(onBoardTouchListener);
		boardH = boardW = 4;
		aimNum = 2048;
		boardGrid.setRowCount(boardH);
		boardGrid.setColumnCount(boardW);
		for (int height = 0; height < boardH; height++)
			for (int width = 0; width < boardW; width++)
			{
				ImageView grid = new ImageView(this);
				grid.setBackgroundResource(R.mipmap.cover_grid);
				grid.setPadding(8, 9, 12, 11);
				grid.setTag(0);
				grid.setImageResource(R.mipmap.grid0);
				boardGrid.addView(grid);
			}
		game2048 = new Game2048(gameCommunicate, boardH, boardW, aimNum);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		game2048.startGame();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		game2048.finishGame();
	}

	//界面侧边按钮的点击事件
	private final OnClickListener onButtonClickListener = new OnClickListener()
	{
		@Override
		public void onClick(final View v) {
			switch (v.getId())
			{
				case R.id.replayButton:
				{
					new AlertDialog.Builder(context)
							.setNegativeButton(R.string.restart,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											digimons = new int[1];
											new ChooseDigimonDialog(context) {
												@Override
												public void doAfterChoose() {
													game2048.replay(false);
												}
											}.chooseDigimon(digimons, 0);
										}
									})
							.setPositiveButton(R.string.replay,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											new ChooseDigimonDialog(context) {
												@Override
												public void doAfterChoose() {
													game2048.replay(false);
												}
											}.chooseDigimon(digimons, digimons.length - 1);
										}
									}).show();
				}break;
				case R.id.soundButton:
				{
					new AlertDialog.Builder(context)
							.setNegativeButton(R.string.closeSound,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {

										}
									})
							.setPositiveButton(R.string.closeMusic,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
										}
									}).show();
				}break;
				case R.id.offButton:
				{
					new AlertDialog.Builder(context)
							.setNegativeButton(R.string.cancel, null)
							.setPositiveButton(R.string.quitGame,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											finish();
										}
									}).show();
				}break;
			}
		}
	};
	//棋盘上的滑动与点击事件
	private float touchX=0, touchY=0;
	private final View.OnTouchListener onBoardTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (MotionEvent.ACTION_DOWN==event.getAction())
			{
				touchX = event.getX();
				touchY = event.getY();
			}
			else if (MotionEvent.ACTION_UP==event.getAction())
			{
				touchX -= event.getX();
				touchY -= event.getY();
				if (touchX >= -16 && touchX <= 16 && touchY >= -16 && touchY <= 16)
				{
					float viewH = v.getHeight(), viewW = v.getWidth();
					float clickH = event.getY(), clickW = event.getX();
					int height, width;
					if (clickH==viewH) height = boardH - 1;
					else height = (int)( boardH * clickH / viewH);
					if (clickW==viewW) width = boardW - 1;
					else width = (int) (boardW * clickW / viewW);
					ImageView grid = (ImageView) boardGrid.getChildAt(boardW * height + width);
					int num = (int) grid.getTag();
					if (num != 0 && num <= aimNum)
					{
						//显示普通模式数字
						String imageName = "grid0_" + num;
						grid.setImageResource(getResources().getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
						grid.setTag(0);
					}
				}
				else
				{
					game2048.action(((touchY + touchX > 0) ? 0 : 2)
							+ ((touchY - touchX > 0) ? 0 : 1));
				}
			}
			return true;
		}
	};

	private final Game2048Communicate gameCommunicate = new Game2048Communicate()
	{
		@Override
		public Map<String, Object> loadData() {
			int[] dataBackup = {boardH, boardW, aimNum};
			//load game data
			HashMap<String, Object> dataMap = new HashMap<>();
			try {
				//read file
				FileInputStream inputStream = openFileInput("gameData");
				byte[] bytes = new byte[1024];
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				while (inputStream.read(bytes) != -1) {
					arrayOutputStream.write(bytes, 0, bytes.length);
				}
				inputStream.close();
				arrayOutputStream.close();

				//read json, write map
				JSONObject dataJO = new JSONObject(new String(
						Base64.decode(arrayOutputStream.toByteArray(), Base64.DEFAULT)
				));
				aimNum = dataJO.getInt("aimNum");
				dataMap.put("aimNum", aimNum);
				dataMap.put("level", dataJO.getInt("level"));
				dataMap.put("score", dataJO.getInt("score"));
				JSONArray boardJA = dataJO.getJSONArray("board");
				boardH = boardJA.length();
				boardW = boardJA.optJSONArray(0).length();
				int[][] board = new int[boardH][boardW];
				for (int height = 0; height < boardJA.length(); height++) {
					JSONArray rowJA = boardJA.getJSONArray(height);
					for (int width = 0; width < rowJA.length(); width++)
						board[height][width] = rowJA.getInt(width);
				}
				dataMap.put("board", board);

				//read json, write digimons
				JSONArray digimonJA = dataJO.getJSONArray("digimons");
				int digimonNums = digimonJA.length();
				digimons = new int[digimonNums];
				for (int index = 0; index < digimonNums; index++)
					digimons[index] = digimonJA.getInt(index);
			} catch (Exception e) {
				e.printStackTrace();
				boardH = dataBackup[0];
				boardW = dataBackup[1];
				aimNum = dataBackup[2];
				return null;
			}
			return dataMap;
		}

		@Override
		public boolean saveData(Map<String, Object> dataMap) {
			//save game data
			try {
				//read map, write json
				JSONObject dataJO = new JSONObject();
				dataJO.put("aimNum", dataMap.get("aimNum"));
				dataJO.put("level", dataMap.get("level"));
				dataJO.put("score", dataMap.get("score"));
				int[][] board = (int[][]) dataMap.get("board");
				JSONArray boardJA = new JSONArray(Arrays.deepToString(board));
				dataJO.put("board", boardJA);

				//read digimons, make json
				JSONArray digimonJA = new JSONArray(Arrays.toString(digimons));
				dataJO.put("digimons", digimonJA);

				//write file
				FileOutputStream outputStream = openFileOutput("gameData",
						Activity.MODE_PRIVATE);
				outputStream.write(
						//虽然是开源的，还是加密一下吧，至少拦住小白
						Base64.encode(dataJO.toString().getBytes(), Base64.DEFAULT)
				);
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		public void showData(int level, int score, int[][] board) {
			levelTextView.setText(getString(R.string.level) + level);
			scoreTextView.setText(getString(R.string.score) + score);

			String imageSort = String.format("grid%d_", digimons[level - 1]);
			Resources resources = getResources();
			for (int height = 0; height < boardH; height++)
				for (int width = 0; width < boardW; width++)
				{
					ImageView grid = (ImageView) boardGrid.getChildAt(boardW * height+ width);
					int boardNum = board[height][width];
					if (boardNum == (int) grid.getTag()) continue;
					else grid.setTag(boardNum);
					if (boardNum == 0) grid.setImageResource(R.mipmap.grid0);
					else if (board[height][width] <= aimNum)
					{
						//我要吐槽这里为毛会有警告？？？难道说
						//imageName = imageSort.toString() + board[heigjt][width];效率更高？！
						String imageName
								= imageSort + board[height][width];
						grid.setImageResource(resources.getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
					}
					else
					{
						//这么多个append了还建议String？！
						String imageName = String.format("grid%d_%d",
								digimons[board[height][width] - aimNum - 1], 2 * aimNum);
						grid.setImageResource(resources.getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
					}
				}
		}

		@Override
		public void noChangeRespond() {
			((Vibrator) getSystemService(Service.VIBRATOR_SERVICE)).vibrate(300);
		}

		@Override
		public void movedRespond() {
			Sounds.getInstance().playSound("move");
		}

		@Override
		public void mergedRespond() {
			Sounds.getInstance().playSound("merge");
		}

		@Override
		public void loadFailedIsStartNew(final Informer informer) {
			digimons = new int[1];
			new ChooseDigimonDialog(context) {
				@Override
				public void doAfterChoose() {
					informer.commit(true);
				}
			}.chooseDigimon(digimons, 0);
		}

		@Override
		public void saveFailedIsStillFinish(final Informer informer) {
			informer.commit(true);
		}

		@Override
		public void gameEndReplayThisLevel(int level, int score, final Informer informer) {
			SharedPreferences sharedPreference
					= getSharedPreferences("Records", MODE_PRIVATE);
			if (score < sharedPreference.getInt("minScore" + level, 500))
			{
				//写入最低成绩
				SharedPreferences.Editor editor = sharedPreference.edit();
				editor.putInt("minScore" + level, score);
				editor.commit();
			}
			else if (score > sharedPreference.getInt("maxScore" + level, 0))
			{
				//新的最高记录
				SharedPreferences.Editor editor = sharedPreference.edit();
				editor.putInt("maxScore" + level, score);
				editor.commit();
			}
			new AlertDialog.Builder(context).setMessage(R.string.gameOver)
					//TODO 在线排名系统
					.setNegativeButton(R.string.replayLater, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							informer.commit(false);
						}
					})
					.setPositiveButton(R.string.replayNow, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							informer.commit(true);
						}
					}).setCancelable(false).show();

			//写入一次游戏记录
			new SqlRecords(context).insert(level, score);
		}

		@Override
		public void levelUpEnterNextLevel(final int level, final int score, final Informer informer) {
			Sounds.getInstance().playSound("level_up");

			SharedPreferences sharedPreference
					= getSharedPreferences("Records", MODE_PRIVATE);
			if (score > sharedPreference.getInt("maxScore" + level, 0))
			{
				//新的最高记录
				SharedPreferences.Editor editor = sharedPreference.edit();
				editor.putInt("maxScore" + level, score);
				editor.commit();
			}

			new AlertDialog.Builder(context).setMessage(R.string.levelUp)
					//TODO 在线排名系统
					.setNegativeButton(R.string.replay, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							informer.commit(false);
						}
					})
					.setPositiveButton(R.string.nextLevel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							digimons = Arrays.copyOf(digimons, level + 1);
							new ChooseDigimonDialog(context) {
								@Override
								public void doAfterChoose() {
									informer.commit(true);
								}
							}.chooseDigimon(digimons, level);
						}
					}).setCancelable(false).show();

			//写入一次游戏记录
			new SqlRecords(context).insert(level, score);
		}
	};
}