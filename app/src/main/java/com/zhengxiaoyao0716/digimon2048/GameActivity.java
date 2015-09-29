package com.zhengxiaoyao0716.digimon2048;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.zhengxiaoyao0716.adapter.ChoosePagerAdapter;
import com.zhengxiaoyao0716.game2048.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
		gameBackground[0] = new ColorDrawable(Color.parseColor("#28A306"));
		//被这倒霉玩意儿恶心到了，要是按网上的方法改API，只能越改越高。。。
		//而且在xml中也没法根据SDK版本来if。。。
		if (Build.VERSION.SDK_INT >= 21) {
			gameBackground[1] = getDrawable(R.mipmap.cover_body);
		}
		else //noinspection deprecation
			gameBackground[1] = getResources().getDrawable(R.mipmap.cover_body);

		gameRelativeLayout = (RelativeLayout) findViewById(R.id.gameRelativeLayout);
		if (Build.VERSION.SDK_INT >= 16)
			gameRelativeLayout.setBackground(new LayerDrawable(gameBackground));
		else //noinspection deprecation
			gameRelativeLayout.setBackgroundDrawable(new LayerDrawable(gameBackground));

		((ImageView) findViewById(R.id.replayButton)).setOnClickListener(onButtonClickListener);
		((ImageView) findViewById(R.id.soundButton)).setOnClickListener(onButtonClickListener);
		((ImageView) findViewById(R.id.offButton)).setOnClickListener(onButtonClickListener);
		((ImageView) findViewById(R.id.optionsButton)).setOnClickListener(onButtonClickListener);
		
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		scoreTextView = (TextView) findViewById(R.id.scoreTextView);
		boardGrid = (GridLayout) findViewById(R.id.boardGrid);
		boardGrid.setOnTouchListener(onBoardTouchListener);
		boardH = boardW = 4;
		aimNum = 64;
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
		try {
			game2048 = new Game2048(gameCommunicate, boardH, boardW, aimNum);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		game2048.quitGame();
	}

	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(context, MainActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	//界面侧边按钮的点击事件
	private final OnClickListener onButtonClickListener = new OnClickListener()
	{
		@Override
		public void onClick(final View v) {
			// TODO Auto-generated method stub
			switch (v.getId())
			{
				case R.id.replayButton:
				{
					new AlertDialog.Builder(context)
							.setNegativeButton(R.string.cancel, null)
							.setNeutralButton(R.string.restart,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											digimons = new int[1];
											chooseDigimon(0);
											game2048.replay(false);
										}})
							.setPositiveButton(R.string.replay,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											chooseDigimon(digimons.length - 1);
											game2048.replay(true);
										}
									}).show();
				}break;
				case R.id.soundButton:
				{
					new AlertDialog.Builder(context)
							.setNegativeButton(R.string.cancel, null)
							.setNeutralButton(R.string.closeSound,
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
							.setNeutralButton(R.string.quitGame,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											Intent intent = new Intent(context, MainActivity.class);
											startActivity(intent);
											finish();
										}
									})
							.setPositiveButton(R.string.exitApp,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											finish();
										}
									}).show();
				}break;
				case R.id.optionsButton:
				{
					new AlertDialog.Builder(context)
							.setMessage("options").show();
				}break;
			}
		}
	};
	//棋盘上的滑动与点击事件
	private float touchX=0, touchY=0;
	private final View.OnTouchListener onBoardTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO: Implement this method
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
						String imageName = "grid0_" + num;
						grid.setImageResource(getResources().getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
					}
				}
				else
				{
					try {
						game2048.action(((touchY + touchX > 0) ? 0 : 2)
								+ ((touchY - touchX > 0) ? 0 : 1));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
			return true;
		}
	};
	//选择数码宝贝
	private final static int DIGIMON_NUMS = 14;
	private void chooseDigimon(final int posInDigimons)
	{
		final ArrayList<Integer> digimonArray = new ArrayList<>(DIGIMON_NUMS);
		for (int i = 1; i <= DIGIMON_NUMS; i++) digimonArray.add(i);
		for (int index = 0; index < posInDigimons; index++)
			digimonArray.remove((Integer)digimons[index]);

		final ArrayList<View> digimonViews = new ArrayList<>();
		for (int digimon : digimonArray) {
			String imageName = new StringBuilder("grid")
					.append(digimon).append("_64").toString();
			ImageView digimonIV = new ImageView(context);
			digimonIV.setImageResource(getResources().getIdentifier(imageName,
					"mipmap", "com.zhengxiaoyao0716.digimon2048"));
			digimonViews.add(digimonIV);
		}

		//错误的做法，View不能复用！
		/*
		//首位插入末位的View
		digimonViews.add(0, digimonViews.get(digimonViews.size() - 1));
		//末尾增加首位（现在是次位）的View
		digimonViews.add(0, digimonViews.get(1));
		*/
		//然而图片是可以复用的
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
		View chooseDialogView = getLayoutInflater().inflate(R.layout.dialog_choose_digimon, null);
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
				//在这里做跳转效果并不理想。onPageSelected方法是在跳转过程中调用的
				//所以会导致首位连接处的跳转少半个周期
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		//从随机位置开始，重要的是一定不能是首末位置（0、digimonViews.size() - 1)
		chooseVP.setCurrentItem(1 + new Random().nextInt(digimonArray.size()));

		//设置Button
		OnClickListener OnChooseButtonClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId())
				{
					case R.id.prevDigimonButton:
						chooseVP.setCurrentItem(chooseVP.getCurrentItem() - 1);
						break;
					case R.id.chooseThisButton:
					{
						//用户更换数码宝贝后棋盘需要刷新，因为非阻塞。。。
						for (int height = 0; height < boardH; height++)
							for (int width = 0; width < boardW; width++) {
								ImageView grid = (ImageView) boardGrid.getChildAt(
										boardW * height + width);
								int gridNum = (int) grid.getTag();
								if (gridNum != 0 && gridNum < aimNum) {
									String imageName = new StringBuilder("grid")
											.append(digimons[posInDigimons]).append("_")
											.append(gridNum).toString();
									grid.setImageResource(getResources().getIdentifier(imageName,
											"mipmap", "com.zhengxiaoyao0716.digimon2048"));
								}
							}
						chooseAD.dismiss();
					}break;
					case R.id.nextDigimonButton:
						chooseVP.setCurrentItem(chooseVP.getCurrentItem() + 1);
						break;
				}
			}
		};
		((Button) chooseDialogView.findViewById(R.id.prevDigimonButton)).setOnClickListener(OnChooseButtonClick);
		((Button) chooseDialogView.findViewById(R.id.chooseThisButton)).setOnClickListener(OnChooseButtonClick);
		((Button) chooseDialogView.findViewById(R.id.nextDigimonButton)).setOnClickListener(OnChooseButtonClick);

		//展示Dialog
		chooseAD.show();
	}

	private final Game2048Communicate gameCommunicate = new Game2048Communicate()
	{
		@Override
		public Map<String, Object> loadData() {
			// TODO Auto-generated method stub
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
				String dataStr = new String(arrayOutputStream.toByteArray());

				//read json, write map
				JSONObject dataJO = new JSONObject(dataStr);
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
				boardH = dataBackup[0];
				boardW = dataBackup[1];
				aimNum = dataBackup[2];
				digimons = new int[1];
				chooseDigimon(0);
				return null;
			}
			return dataMap;
		}

		@Override
		public boolean saveData(Map<String, Object> dataMap) {
			// TODO Auto-generated method stub
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
				outputStream.write(dataJO.toString().getBytes());
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		@Override
		public void showData(int level, int score, int[][] board) {
			// TODO Auto-generated method stub
			levelTextView.setText(getString(R.string.level) + level);
			scoreTextView.setText(getString(R.string.score)+ score);

			StringBuilder imageSort
					= new StringBuilder("grid").append(digimons[level - 1]).append("_");
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
								= new StringBuilder(imageSort)
								.append(board[height][width]).toString();
						grid.setImageResource(getResources().getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
					}
					else
					{
						//这么多个append了还建议String？！
						String imageName = new StringBuilder("grid")
								.append(digimons[board[height][width] - aimNum - 1])
								.append("_").append(2 * aimNum).toString();
						grid.setImageResource(getResources().getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
					}
				}
		}

		@Override
		public boolean levelUpIsEnterNextLevel(int level, int score) {
			// TODO Auto-generated method stub
			final boolean[] chooseDialogResult = new boolean[2];
			new Thread(){
				@Override
				public void run() {
					Looper.prepare();
						new AlertDialog.Builder(context).setMessage(R.string.levelUp)
								.setNegativeButton(R.string.replay, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										chooseDialogResult[0] = true;
									}
								})
								.setPositiveButton(R.string.nextLevel, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										chooseDialogResult[0] = true;
										chooseDialogResult[1] = true;
									}
								}).setCancelable(false).show();
					Looper.loop();
				}
			}.start();
			//Wait dialog return.
			//我艹，为毛棋盘没有更新？！
			// 如果UI是主线程里绘制，现在应该已经绘制好了。
			// 如果是专门的线程，我阻塞主线程管它毛事，继续绘制啊！！！
			// invalidate家族一系列方法都尝试过了。。。
			while (!chooseDialogResult[0])
				try {
					//Prevent ANR
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			if (!chooseDialogResult[1]) return false;
			digimons = Arrays.copyOf(digimons, level + 1);
			chooseDigimon(level);
			return true;
		}

		@Override
		public boolean gameOverIsReplay(int level, int score) {
			// TODO Auto-generated method stub
			((Vibrator) getSystemService(Service.VIBRATOR_SERVICE)).vibrate(1000);

			final boolean[] chooseDialogResult = new boolean[2];

			new Thread(){
				@Override
				public void run() {
					Looper.prepare();
					new AlertDialog.Builder(context).setMessage(R.string.gameOver)
							.setNegativeButton(R.string.replayLater, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									chooseDialogResult[0] = true;
								}
							})
							.setPositiveButton(R.string.replayNow, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									chooseDialogResult[0] = true;
									chooseDialogResult[1] = true;
								}
							}).setCancelable(false).show();
					Looper.loop();
				}
			}.start();
			//Wait dialog return.
			while (chooseDialogResult[0] == false)
				try {
					//Prevent ANR
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			return chooseDialogResult[1];
		}

		@Override
		public boolean saveFailedIsStillQuit() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void noChangeRespond() {
			// TODO Auto-generated method stub
			((Vibrator) getSystemService(Service.VIBRATOR_SERVICE)).vibrate(300);
		}

		@Override
		public void movedRespond() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mergedRespond() {
			// TODO Auto-generated method stub
			
		}
	};
}