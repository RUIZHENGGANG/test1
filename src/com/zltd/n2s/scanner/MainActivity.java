package com.zltd.n2s.scanner;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity extends BaseScanActivity {
	protected int mIndex = 0;
	private RadioButton mRbOnlyScan;
	private RadioButton mRbContinueScan;
	private RadioGroup mRadioGroup;
	private CheckBox mSoundSwitch;

	private ListView mListView;
	private ArrayList<String> mBarcodeList = new ArrayList<String>();
	private ArrayAdapter<String> mListAdaper;
	private WakeLock mWakeLock;
	private TextView mScanCountTv;
	private SoundUtils mSoundUtil;
	private int mScanCount;
	private TextView mPressCountTv;
	private int mPressCount;
	// For N2屏蔽Home Key
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"com.zltd.autotest");
		mWakeLock.acquire();
		mSoundUtil = SoundUtils.getInstance();
		mSoundUtil.init(this);
		initView();
		// For N2 屏蔽Home Key
		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED,
				FLAG_HOMEKEY_DISPATCHED);
	}

	@Override
	protected void onScan(String barcode) {
		mBarcodeList.add(0, barcode);
		int size = mBarcodeList.size();
		if (size > 50) {
			mBarcodeList.remove(size - 1);
		}
		mListAdaper.notifyDataSetChanged();
		mScanCount++;
		updateCount();
		mSoundUtil.success();
	}

	private void initView() {
		setContentView(R.layout.main);
		mRadioGroup = (RadioGroup) findViewById(R.id.rg);
		mRbOnlyScan = (RadioButton) findViewById(R.id.rbOnlyScan);
		mRbContinueScan = (RadioButton) findViewById(R.id.rbContinueScan);
		mSoundSwitch = (CheckBox) findViewById(R.id.sound);
		mScanCountTv = (TextView) findViewById(R.id.scan_count);
		mPressCountTv = (TextView) findViewById(R.id.press_count);
		mListView = (ListView) findViewById(android.R.id.list);
		mListAdaper = new ArrayAdapter<String>(this, R.layout.list_item,
				mBarcodeList);
		mListView.setAdapter(mListAdaper);

		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rbOnlyScan) {
					continuousScanMode(false);
				} else {
					continuousScanMode(true);
				}
			}
		});

		mSoundSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KEY_SCAN && super.onKeyDown(keyCode, event)) {
			mPressCount++;
			updateCount();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 清零按钮事件监听，嵌入在xml
	 * @param v
	 */
	public void clear(View v) {
		mScanCount = 0;
		mPressCount = 0;
		updateCount();
		mBarcodeList.clear();
		mListAdaper.notifyDataSetChanged();
	}

	private void updateCount() {
		mScanCountTv.setText("scanned:" + mScanCount);
		mPressCountTv.setText("pressed:" + mPressCount);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mWakeLock != null) {
			if (mWakeLock.isHeld()) {
				mWakeLock.release();
			}
			mWakeLock = null;
		}
	}

}
