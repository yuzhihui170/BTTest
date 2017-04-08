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
                e.printStackTrace();
                Log.e(TAG, "can not open bt.");
            }
        }
        Log.d(TAG,"[BTUtil] openDevice dev:" + mPathDefault + " baudrate:"+115200);
    }

    //关闭设备
    public void closeDevice() {
        if(mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        Log.d(TAG,"[BTUtil] close");
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
        if(mOutputStream != null) {
            try {
                mOutputStream.write(buffer, offset, count);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
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
    public void connect(int index) {
        String cmdStr = HEAD + CmdConstant.CONNECT + index + END;
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

    //BT-->ARM 获取搜索结果 43.IX[addr:12][name]
    public String searchResult() {
        byte[] buffer = new byte[100];
        int ret = read(buffer, 0, 100);
        if(ret < 0) {
            return null;
        }
        String result = new String(buffer, 0, ret);
        if(result.substring(0, 1).equals(CmdConstant.SEARCH_RESULT)) {
            Log.d(TAG,"result:"+result);
            return result;
        }
        return null;
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
}
