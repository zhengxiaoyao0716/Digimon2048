package com.zhengxiaoyao0716.digimon2048;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.zhengxiaoyao0716.game2048.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class GameActivity extends Activity {
	private Context context;
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
		
		ImageView replayButton = (ImageView) findViewById(R.id.replayButton);
		replayButton.setOnClickListener(onButtonClickListener);
		ImageView soundButton = (ImageView) findViewById(R.id.soundButton);
		soundButton.setOnClickListener(onButtonClickListener);
		ImageView offButton = (ImageView) findViewById(R.id.offButton);
		offButton.setOnClickListener(onButtonClickListener);
		
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		scoreTextView = (TextView) findViewById(R.id.scoreTextView);
		boardGrid = (GridLayout) findViewById(R.id.boardGrid);
		boardGrid.setOnTouchListener(onBoardTouchListener);
		boardH = boardW = 4;
		aimNum = 4096;
		boardGrid.setRowCount(boardH);
		boardGrid.setColumnCount(boardW);
		for (int height = 0; height < boardH; height++)
			for (int width = 0; width < boardW; width++)
			{
				ImageView grid = new ImageView(this);
				grid.setBackgroundResource(R.mipmap.cover_grid);
				grid.setPadding(8, 9, 12, 11);
				grid.setTag(0);
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

	private OnClickListener onButtonClickListener = new OnClickListener()
	{
		@Override
		public void onClick(final View v) {
			// TODO Auto-generated method stub
			switch (v.getId())
			{
				case R.id.replayButton:
				{
					new AlertDialog.Builder(context)
							.setNegativeButton(getString(R.string.cancel), null)
							.setNeutralButton(getString(R.string.restart),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											digimons = new int[1];
											digimons[0] = 1 + new Random().nextInt(14);
											game2048.replay(false);
										}})
							.setPositiveButton(getString(R.string.replay),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											game2048.replay(true);
										}
									}).show();
				}break;
				case R.id.soundButton:
				{
					//*
				}break;
				case R.id.offButton:
				{
					new AlertDialog.Builder(context)
							.setNegativeButton(getString(R.string.cancel), null)
							.setNeutralButton(getString(R.string.quitGame),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											Intent intent = new Intent(context, MainActivity.class);
											startActivity(intent);
											finish();
										}
									})
							.setPositiveButton(getString(R.string.exitApp),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											finish();
										}
									}).show();
				}break;
			}
		}
	};

	float touchX=0, touchY=0;
	private View.OnTouchListener onBoardTouchListener = new View.OnTouchListener() {
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
						String imageName = new StringBuilder("grid0_").append(num).toString();
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

	private Game2048Communicate gameCommunicate = new Game2048Communicate()
	{
		@Override
		public Map<String, Object> loadData() {
			// TODO Auto-generated method stub
			int[] dataBackup = {boardH, boardW, aimNum};
			//load game data
			HashMap<String, Object> dataMap = new HashMap<String, Object>();
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
				digimons[0] = 1 + new Random().nextInt(14);
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
			levelTextView.setText("Level:" + level);
			scoreTextView.setText("Score:" + score);

			StringBuilder imageSort = new StringBuilder("grid")
					.append(digimons[level - 1]).append("_");
			for (int height = 0; height < boardH; height++)
				for (int width = 0; width < boardW; width++)
				{
					ImageView grid = (ImageView) boardGrid.getChildAt(boardW * height+ width);
					if (board[height][width]==0) grid.setImageResource(R.mipmap.grid0);
					else if (board[height][width]<=aimNum)
					{
						String imageName
								= new StringBuilder(imageSort)
								.append(board[height][width]).toString();
						grid.setImageResource(getResources().getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
					}
					else
					{
						String imageName = new StringBuilder("grid")
								.append(digimons[board[height][width] - aimNum - 1])
								.append("_").append(aimNum).toString();
						grid.setImageResource(getResources().getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
					}
					grid.setTag(board[height][width]);
				}
		}

		@Override
		public boolean levelUpIsEnterNextLevel(int level, int score) {
			// TODO Auto-generated method stub
			final byte[] chooseDialogResult = {0, 0};
			new Thread(){
				@Override
				public void run() {
					Looper.prepare();
						new AlertDialog.Builder(context).setMessage(R.string.levelUp)
								.setNegativeButton(R.string.replay, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										chooseDialogResult[0] = 1;
									}
								})
								.setPositiveButton(R.string.nextLevel, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										chooseDialogResult[0] = 1;
										chooseDialogResult[1] = 1;
									}
								}).setCancelable(false).show();
					Looper.loop();
				}
			}.start();

			while (chooseDialogResult[0]==0)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			if (chooseDialogResult[1]==0) return false;
			digimons = Arrays.copyOf(digimons, level + 1);
			CHOOSE: while (true)
			{
				digimons[level] = 1 + new Random().nextInt(14);
				for (int index = 0; index < level; index++)
					if (digimons[index]==digimons[level]) continue CHOOSE;
				break;
			}
			return true;
		}

		@Override
		public boolean gameOverIsReplay(int level, int score) {
			// TODO Auto-generated method stub
			final byte[] chooseDialogResult = {0, 0};
			new Thread(){
				@Override
				public void run() {
					Looper.prepare();
					new AlertDialog.Builder(context).setMessage(R.string.gameOver)
							.setNegativeButton(R.string.replayLater, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									chooseDialogResult[0] = 1;
								}
							})
							.setPositiveButton(R.string.replayNow, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									chooseDialogResult[0] = 1;
									chooseDialogResult[1] = 1;
								}
							}).setCancelable(false).show();
					Looper.loop();
				}
			}.start();

			while (chooseDialogResult[0]==0)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			return chooseDialogResult[1]==0 ? false : true;
		}

		@Override
		public boolean saveFailedIsStillQuit() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void noChangeRespond() {
			// TODO Auto-generated method stub
			
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