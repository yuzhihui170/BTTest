package com.example.btutil;

import android.util.Log;

import org.winplus.serial.utils.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.btutil.CmdConstant.BT_DISCONNECT;
import static com.example.btutil.CmdConstant.CONNECT;
import static com.example.btutil.CmdConstant.DISCONNECT;
import static com.example.btutil.CmdConstant.END;
import static com.example.btutil.CmdConstant.HEAD;

public class BTUtil {
    private static String TAG = "yzh";
    private SerialPort mSerialPort;
    private String mPathDefault = "/dev/ttyS3"; //ttyGS3 ttyS3
    private int mBaudrateDefault = 115200;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    private static BTUtil btUtil = null;

    //静态工厂方法
    public static BTUtil getInstance() {
        if(btUtil == null) {
            synchronized (BTUtil.class) {
                if(btUtil == null) {
                    btUtil = new BTUtil();
                }
            }
        }
        return btUtil;
    }

    //打开设备
    public void openDevice() {
        if(null == mSerialPort) {
            try {
                mSerialPort = new SerialPort(new File(mPathDefault), mBaudrateDefault, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
            } catch (IOException e) {
//                e.printStackTrace();
                Log.e(TAG, "can not open bt.");
            }
            Log.d(TAG,"[BTUtil] openDevice dev:" + mPathDefault + " baudrate:"+115200);
        }
    }

    //关闭设备
    public void closeDevice() {
        if(mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
            Log.d(TAG,"[BTUtil] close");
        }
    }

    //读取数据
    public int read(byte[] buffer, int offset, int byteCount) {
        int ret = -1;
        if(mInputStream != null) {
            try {
                ret = mInputStream.read(buffer, offset, byteCount);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Log.e(TAG, "mInputStream = null,not openDevice or can not open");
        }
        return ret;
    }

    //写数据
    public void write(byte[] buffer, int offset, int count) {
        if (mOutputStream != null) {
            try {
                mOutputStream.write(buffer, offset, count);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG,"mOutputStream = null,not openDevice or can not open");
        }
    }

    public void write(String cmdStr) {
        byte[] cmd = cmdStr.getBytes();
        write(cmd, 0, cmd.length);
        Log.d(TAG, "write cmdstr:"+cmdStr);
    }

    //cmd:需要比较的指令 匹配成功返回true, 适合只需要获取状态不需要获取额外数据的指令
    public boolean getResult(String cmd) {
        byte[] buffer = new byte[20];
        int ret = read(buffer, 0, 20);
        String cmdStr = new String(buffer, 0, ret);
        Log.d(TAG, "getResult:"+cmdStr);
        if(cmdStr.equals(cmd + END)) {
            return true;
        }
        return false;
    }

    //一.ARM ---> BT : AT#[CMD]\r\n
    //1.连接最后一次连接设备,或者配对列表index;
    public void connect(String btAddr) {
        String cmdStr = HEAD + CmdConstant.CONNECT + btAddr + END;
        byte[] cmd = cmdStr.getBytes();
        write(cmd, 0, cmd.length);
        Log.d(TAG,"connect : " + cmdStr);
    }

    //BT-->ARM 获取连接结果 IB:蓝牙连接
    public boolean connectResult() {
        return getResult(CmdConstant.BT_CONNECT);
    }

    //2.断开当前设备所有连接;
    public void disconnect() {
        String cmdStr = HEAD + CmdConstant.DISCONNECT + END;
        byte[] cmd = cmdStr.getBytes();
        write(cmd, 0, cmd.length);
        Log.d(TAG,"disconnect : " + cmdStr);
    }

    //BT-->ARM IA:蓝牙断开
    public boolean disconnectResult() {
        return getResult(CmdConstant.BT_DISCONNECT);
    }

    //43.SD:搜索;
    public void search() {
        String cmdStr = HEAD + CmdConstant.SEARCH + END;
        write(cmdStr);
    }

    //44.ST:停止搜索;
    public void stopSearch() {
        String cmdStr = HEAD + CmdConstant.SEARCH_STOP + END;
        write(cmdStr);
    }

    //BT-->ARM IY:搜索结束
    public boolean stopSearchResult() {
        return getResult(CmdConstant.SEARCH_RESULT);
    }

    //BT-->ARM MD:下一曲
    public void nextMusic() {
        String cmdStr = HEAD + CmdConstant.NEXT_MUSIC + END;
        write(cmdStr);
    }

    //BT-->ARM ME:上一曲
    public void preMusic() {
        String cmdStr = HEAD + CmdConstant.PRE_MUSIC + END;
        write(cmdStr);
    }

    //BT-->ARM MA:播放、暂停音乐
    public void playOrPauseMusic() {
        String cmdStr = HEAD + CmdConstant.PLAY_OR_PAUSE_MUSIC + END;
        write(cmdStr);
    }


    //BT-->ARM MC:停止音乐
    public void stopMusic() {
        String cmdStr = HEAD + CmdConstant.STOP_MUSIC + END;
        write(cmdStr);
    }

    //！！！！ 注意：切换声音指令与实际情况不一样 CP：切换到手机 CO切换到蓝牙
    //BT-->ARM 19.CP:声音切到蓝牙;
    public void switchVoiceToBT() {
        String cmdStr = HEAD + "CO" + END;
        write(cmdStr);
    }

    //BT-->ARM 20.CN:声音切到手机
    public void switchVoiceToPhone() {
        String cmdStr = HEAD + "CP" + END;
        write(cmdStr);
    }

    //BT-->ARM 18.CO:通话声音互切;
//    public void switchVoiceToPhoneOrBT() {
//        String cmdStr = HEAD + CmdConstant.VOICE_SWITCH_PHONE_OR_BT + END;
//        write(cmdStr);
//    }

    //BT-->ARM 9.CW[NUMBER]:拨号;
    public void dail(String num) {
        String cmdStr = HEAD + CmdConstant.DIAL + num + END;
        write(cmdStr);
    }

    //BT-->ARM 11.CY:查询 HF 状态;
    public void inquireHFStatus() {
        String cmdStr = HEAD + CmdConstant.INQUIRE + END;
        write(cmdStr);
    }

    //BT-->ARM 37.PA:读取电话本;
    public void readContact() {
//        String cmdStr1 = HEAD + "MY" + END;
//        write(cmdStr1);
        String cmdStr = HEAD + CmdConstant.READ_TELE_BOOK + END;
        write(cmdStr);
    }

    private final int BUFFER_LEN = 1024;
    byte[] buffer = new byte[BUFFER_LEN];
    //读取蓝牙模块的各种数据
    public String readStatus() {
        int ret = 0;
        while (true) {
            ret += read(buffer, ret, BUFFER_LEN-ret);
            Log.d(TAG, "read ret:"+ret);
            if(ret >= 4 && buffer[ret-2] == '\r' && buffer[ret-1] == '\n') {
                break;
            }
            if (ret == 1024) {
                break;
            }
        }
        String result = new String(buffer, 0, ret);
        Log.d(TAG,"result  :"+result);
        return result;
    }
}
