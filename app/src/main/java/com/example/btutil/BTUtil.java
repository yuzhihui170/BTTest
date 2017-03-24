package com.example.btutil;

import android.util.Log;

import org.winplus.serial.utils.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    //打开设备
    public void openDevice() {
        if(null == mSerialPort) {
            try {
                mSerialPort = new SerialPort(new File(mPathDefault), mBaudrateDefault, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
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

    public void connect() {
        String cmdStr = HEAD + CONNECT + END;
        byte[] cmd = cmdStr.getBytes();
        write(cmd, 0, cmd.length);
        Log.d(TAG,"connect : " + cmdStr);
    }

    public void disconnect() {
        String cmdStr = HEAD + DISCONNECT + END;
        byte[] cmd = cmdStr.getBytes();
        write(cmd, 0, cmd.length);
        Log.d(TAG,"disconnect : " + cmdStr);
    }
}
