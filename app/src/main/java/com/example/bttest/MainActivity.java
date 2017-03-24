package com.example.bttest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.btutil.BTUtil;

public class MainActivity extends Activity {
    private static String TAG = "yzh";
    BTUtil mBTUtil;
    private boolean mRunFlag;

    //ui
    private Button mBtnConnect;
    private Button mBtnDisconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBTUtil = new BTUtil();
        setViewListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBTUtil.openDevice();
        mRunFlag = true;
        new ReadThread("ReadThread").start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRunFlag = false;
        mBTUtil.closeDevice();
    }

    class ReadThread extends Thread {

        ReadThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (mRunFlag) {
                if(mBTUtil == null) {
                    Log.e(TAG, "mBTUtil == null");
                    return;
                }
                byte[] data = new byte[20];
                int ret = mBTUtil.read(data, 0, data.length);
                if(ret < 0) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                String tmp = new String(data, 0, ret);
                Log.d(TAG, "ret:" + ret + " read data: "+ tmp);
            }
            Log.d(TAG, "read thread exit");
        }
    }

    private void setViewListener() {
        mBtnConnect = (Button) findViewById(R.id.btn_connect);
        mBtnDisconnect = (Button) findViewById(R.id.btn_disconnect);
        mBtnConnect.setOnClickListener(onClickListener);
        mBtnDisconnect.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mBTUtil == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.btn_connect:
                    mBTUtil.connect();
                    break;

                case R.id.btn_disconnect:
                    mBTUtil.disconnect();
                    break;

                default:
                    break;
            }
        }
    };
}
