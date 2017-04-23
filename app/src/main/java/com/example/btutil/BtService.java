package com.example.btutil;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

public class BtService extends Service {
    private static String TAG = "yzh";
    public final static int H_CONNECT = 1;
    public final static int H_DISCONNECT = 2;

    private BTUtil mBTUtil;
    private Object lock = new Object();
    private MyHandler mHandler;

    private ReadStatusThread mThread;

    private boolean mMusicPlaying = false;
    //HF 状态
    private String mHFStatus;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "[BtService]: onBind ");
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "[BtService]: onUnbind ");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBTUtil = BTUtil.getInstance();
        mBTUtil.openDevice();

        mThread = new ReadStatusThread();
        mThread.start();
        Log.d(TAG, "[BtService]: onCreate ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "[BtService]: onStartCommand ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThread.stopThread();
        mBTUtil.closeDevice();
        Log.d(TAG, "[BtService]: onDestroy ");
    }

    private MyBinder myBinder = new MyBinder();
    public class MyBinder extends Binder {

        public void connect(String btAddr) {
            mBTUtil.connect(btAddr);
        }

        public void disconnect() {
            mBTUtil.disconnect();
        }

        public void search() {
            mBTUtil.search();
        }

        public void stopSearch() {
            mBTUtil.stopSearch();
        }

        //播放下一首歌曲
        public void nextMusic() {
            mBTUtil.nextMusic();
        }

        //播放上一首歌曲
        public void preMusic() {
            mBTUtil.preMusic();
        }

        //播放或者暂停音乐
        public void playOrPauseMusic() {
            mBTUtil.playOrPauseMusic();
        }

        //停止音乐
        public void stopMusic() {
            mBTUtil.stopMusic();
        }

        //声音切到手机
        public void switchVoiceToPhone() {
            mBTUtil.switchVoiceToPhone();
        }

        //声音切到蓝牙
        public void swtichVoiceToBT() {
            mBTUtil.switchVoiceToBT();
        }

        public void switchVoiceToPhoneOrBT() {
//            mBTUtil.switchVoiceToPhoneOrBT();
        }

        //获取音乐是否在播放
        public boolean getMusicStatic() {
            return mMusicPlaying;
        }

        //拨号
        public void dial(String num) {
            mBTUtil.dail(num);
        }

        //查询 HF 状态
        public void inquireHFStatus() {
            mBTUtil.inquireHFStatus();
        }

        //读取电话本;
        public void readContact() {
            mBTUtil.readContact();
        }

    }

    private boolean isRunning = false;
    public class ReadStatusThread extends Thread {
        @Override
        public void run() {
            synchronized (lock) {
                Log.d(TAG, " Read Thread start... ");
                isRunning = true;
                while (isRunning) {
                    String result = mBTUtil.readStatus();
                    parseResult(result);

                    try {
                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, " Read Thread exit... ");
            }
        }

        public void stopThread() {
            isRunning = false;
        }
    }

    /** 解析接收数据 */
    private void parseResult(String result) {
        String[] status = result.split("\r\n");
        for (String s : status) {
            Log.d(TAG, "-- status: " + s);
            if (s.contains(CmdConstant.MACTCH_LIST)) {
//                int index = Integer.parseInt(s.substring(2, 2));
                String deviceAddr = s.substring(3, 15);
                String deviceName = s.substring(15);
                Intent intent = new Intent(ActionConstant.ACTION_FOUND_DEVICE);
                intent.putExtra("deviceAddr", deviceAddr);
                intent.putExtra("deviceName", deviceName);
                sendBroadcast(intent);

            } else if (s.contains(CmdConstant.MUSIC_PLAYING)) {
                mMusicPlaying = true;
                Intent intent = new Intent(ActionConstant.ACTION_MUSIC_STATUS_CHANGE);
                intent.putExtra("musicStatus", true);
                sendBroadcast(intent);
                Log.d(TAG, "Music is playing!");

            } else if (s.contains(CmdConstant.MUSIC_PAUSE)) {
                mMusicPlaying = false;
                Intent intent = new Intent(ActionConstant.ACTION_MUSIC_STATUS_CHANGE);
                intent.putExtra("musicStatus", false);
                sendBroadcast(intent);
                Log.d(TAG, "Music is pause!");

            } else if (s.contains(CmdConstant.HF_STATUS + "3")) { //蓝牙连接成功 MG3
                mHFStatus = "MG3";
                Intent intent = new Intent(ActionConstant.ACTION_HF_STATUS_CHANGE);
                intent.putExtra("hfStatus", "MG3");
                sendBroadcast(intent);
                Log.d(TAG, "hf is connected!");

            } else if(s.contains(CmdConstant.HF_STATUS + "1")) { //蓝牙未连接 MG1
                mHFStatus = "MG1";
                Intent intent = new Intent(ActionConstant.ACTION_HF_STATUS_CHANGE);
                intent.putExtra("hfStatus", "MG1");
                sendBroadcast(intent);
                Log.d(TAG, "hf is not connected!");

            }
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case H_CONNECT:
                    break;

                case H_DISCONNECT:
                    break;

                default:
                    break;
            }
        }
    }

}
