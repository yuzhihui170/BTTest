package com.example.btutil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class BtService extends Service {
    private static String TAG = "yzh";
    private BTUtil mBTUtil;
    private Object lock = new Object();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBTUtil = BTUtil.getInstance();
        mBTUtil.openDevice();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBTUtil.closeDevice();
    }

    private boolean isRunning = false;
    public class ReadStatusThread extends Thread {
        @Override
        public void run() {
            synchronized (lock) {
                isRunning = true;
                if (mBTUtil != null) {
                    mBTUtil.search();

                } else {
                    return;

                }

                while (isRunning) {
                    String result = mBTUtil.readStatus();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//				mBTUtil.stopSearch();
                Log.d(TAG, " Search Thread exit.");
            }
        }

        public void stopThread() {
            isRunning = false;
        }
    }
}
