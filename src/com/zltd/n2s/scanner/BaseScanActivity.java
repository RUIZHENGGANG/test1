package com.zltd.n2s.scanner;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import com.zltd.industry.ScannerManager;
import com.zltd.industry.ScannerManager.IScannerStatusListener;
/**
 * 扫描方法的基类,请根据使用场景做修改
 * @author jan
 */
public class BaseScanActivity extends Activity  {
	private static final String TAG = "BaseScanActivity";
	private ScannerManager mScannerManager;
	private Handler mHandler = new Handler();
	public int KEY_SCAN = 96;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setN2sScanner();
	}
	private void setN2sScanner() {
		//1.创建ScannerManager实例,主要就是用于扫描的一个管理类
		mScannerManager = ScannerManager.getInstance();
		scanSwitch(true);
		continuousScanMode(false);
		//2.可选. 设置当前调用方式通过API来调用
		mScannerManager.setDataTransferType(ScannerManager.TRANSFER_BY_API);
		//3.添加扫描的回调函数
		mScannerManager.addScannerStatusListener(mIScannerStatusListener);
	}
	/**
	 * 对扫描接口的开启和关闭
	 * @param scanSwitch
	 */
	public void scanSwitch(boolean scanSwitch) {
		//设置开关
		mScannerManager.scannerEnable(scanSwitch);
	}
	/**
	 * 是否连扫的设置
	 * @param continuousScan
	 */
	public void continuousScanMode(boolean continuousScan) {
		if (continuousScan&& mScannerManager.getScanMode() == 1) { //1代表当前为单扫模式
			mScannerManager.setScanMode(ScannerManager.SCAN_CONTINUOUS_MODE);
			
		} else if (!continuousScan&& mScannerManager.getScanMode() == 2) { //2代表当前为连扫模式
			mScannerManager.setScanMode(ScannerManager.SCAN_SINGLE_MODE);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//这里可以移除扫码事件监听器,释放资源
		mScannerManager.removeScannerStatusListener(mIScannerStatusListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//这里是为扫码管理类 添加一个 监听
		mScannerManager.addScannerStatusListener(mIScannerStatusListener);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//4.在关闭应用的时候记得释放资源,关闭扫描头
		scanSwitch(false);
	}
	
	protected void onScan(String barcode) {
	}
	
	private IScannerStatusListener mIScannerStatusListener = new IScannerStatusListener() {

		@Override
		public void onScannerStatusChanage(int arg0) {
		}
		/**
		 * 一旦扫描到结果就会调用
		 */
		@Override
		public void onScannerResultChanage(final byte[] arg0) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					String s = null;
					try {
						s = new String(arg0, "UTF-8");
						//这里通过onScan方法来接受这个string
						onScan(s);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (s != null) {
						Log.i(TAG, "barcode="+s);
					}
				}
			});
		}
	};
}
