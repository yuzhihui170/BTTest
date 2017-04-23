package com.example.btutil;

//蓝牙的状态变化通过广播通知界面更新
public class ActionConstant {
    //找到设备时发送的广播
    public final static String ACTION_FOUND_DEVICE = "action.find.device";
    //音乐状态发送变化时发送的广播
    public final static String ACTION_MUSIC_STATUS_CHANGE = "action.music.status.change";
    //HF状态广播
    public final static String ACTION_HF_STATUS_CHANGE = "action.hf.status.change";

}
