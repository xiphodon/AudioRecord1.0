package com.example.audiorecord;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 实现按住说话功能
 * 按住说话，开始录音，停止录音，显示到列表，点击列表项播放。
 */
public class RecordActivity extends Activity {
	/** log标记 */
	private static final String LOG_TAG = "AudioRecordTest";
	/** 语音文件保存路径 */
	private String mFileName = null;
	/** 按住说话按钮 */
	private Button mBtnVoice;
	/** 用于语音播放 */
	private MediaPlayer mPlayer = null;
	/** 用于完成录音 */
	private MediaRecorder mRecorder = null;
	/** 显示语音列表 */
	private ListView mVoidListView;
	/** 语音列表适配器 */
	private MyListAdapter mAdapter;
	/** 语音列表 */
	private List<String> mVoicesList;
	/** 录音存储路径 */
	private static final String PATH = "/sdcard/MyVoiceForder/Record/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initData();
		initView();
	}

	/** 初始化控件 */
	private void initView() {
		mVoidListView = (ListView) findViewById(R.id.voidList);
		mAdapter = new MyListAdapter(this);
		mVoidListView.setAdapter(mAdapter);
		mVoidListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				try {
					mPlayer.reset();
					mPlayer.setDataSource(mVoicesList.get(position));
					mPlayer.prepare();
					mPlayer.start();
				} catch (IOException e) {
					Log.e(LOG_TAG, "播放失败");
				}
			}
		});
		mBtnVoice = (Button) findViewById(R.id.btn_voice);
		mBtnVoice.setText("按住说话");
		mBtnVoice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startVoice();
					break;
				case MotionEvent.ACTION_UP:
					stopVoice();
					break;
				default:
					break;
				}
				return false;
			}
		});
	}

	/** 初始化数据 */
	private void initData() {
		mVoicesList = new ArrayList<String>();
		mPlayer = new MediaPlayer();
	}

	/** 开始录音 */
	private void startVoice() {
		// 设置录音保存路径
		mFileName = PATH + UUID.randomUUID().toString() + ".amr";
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			Log.i(LOG_TAG, "SD Card is not mounted,It is  " + state + ".");
		}
		File directory = new File(mFileName).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			Log.i(LOG_TAG, "Path to file could not be created");
		}
		Toast.makeText(getApplicationContext(), "开始录音", 0).show();
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
		mRecorder.start();
	}

	/** 停止录音 */
	private void stopVoice() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		mVoicesList.add(mFileName);
		mAdapter = new MyListAdapter(RecordActivity.this);
		mVoidListView.setAdapter(mAdapter);
		Toast.makeText(getApplicationContext(), "保存录音" + mFileName, 0).show();
	}

	/** 语音列表适配器 */
	private class MyListAdapter extends BaseAdapter {
		LayoutInflater mInflater;

		public MyListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mVoicesList.size();
		}

		@Override
		public Object getItem(int position) {

			return position;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.item_voicelist, null);
			TextView tv = (TextView) convertView.findViewById(R.id.tv_armName);
			tv.setText(mVoicesList.get(position));
			return convertView;
		}
	}
}