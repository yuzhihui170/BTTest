/*
 * Copyright (C) Apical
 */
package com.example.btapp;

//import com.csr.BTApp.common.CommonUtils;
//import com.csr.BTApp.common.global;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.app.Fragment;
import android.content.res.Resources;

public class SettingFragment extends Fragment{
	private CheckBox cb_discoverable;
	private EditText edit_devicename;
	private View m_settingsView;
	
	private Handler handler;
	private DiscoverableThread discoverableThread;
	private final int  DEFAULT_DISCOVERABLE_TIMEOUT = 120;
	private long startTimestamp = 0;
	private boolean m_IsRunning = false;
	private static byte[]  lock= new byte[0];
	
    @Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		
//		if(isVisibleToUser == true){
//			Log.d(global.TAG, "[SettingFragment]: Show View");
//		}else{
//			Log.d(global.TAG, "[SettingFragment]: Hide View");
//		}
	}
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);   	

         handler = new Handler() {
            @Override
            public void handleMessage(Message msg){
//             	 switch (msg.what){
//              	 	case 0:
//        				if (global.bu.getDiscoverableTimeout() <= 0){
//        					Log.d(global.TAG, "DiscoverableThread -- Timeout:" + global.bu.getDiscoverableTimeout());
//        					cb_discoverable.setChecked(false);
//        				}
//        				if (global.bu.getDiscoverable()){
//        					if(isAdded()){
//        						cb_discoverable.setText(getActivity().getResources().getString(R.string.setting_Discoverable) +"("+formatTimeRemaining(global.bu.getDiscoverableTimeout())+")");
//        					}
//            				Log.d(global.TAG, "DiscoverableThread -- Timeout:" + global.bu.getDiscoverableTimeout());
//            				global.bu.setDiscoverableTimeout(global.bu.getDiscoverableTimeout() - 1);
//        				}else{
//        					if(isAdded()){
//        						cb_discoverable.setText(getActivity().getResources().getString(R.string.setting_Discoverable));
//        					}
//        				}
//              	 		break;
//              	 }
            }
        };
        
//        Log.d(global.TAG, "[SettingFragment] OnCreate");
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        m_settingsView = inflater.inflate(R.layout.setting_fragment, (ViewGroup)getActivity().findViewById(R.id.tabpage_frame), false);
      	initComponents();

    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//    	Log.d(global.TAG, "[SettingFragment] onCreateView");

    	return m_settingsView;
	}

    
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
//		Log.d(global.TAG, "[SettingFragment] onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
//		Log.d(global.TAG, "[SettingFragment] onResume");
		super.onResume();
		
		long currentTimestamp = System.currentTimeMillis();
//		Log.d(global.TAG, "currentTimestamp:"+ currentTimestamp);
//    	if (global.bu.getDiscoverable()) {
//	    	if (currentTimestamp < startTimestamp + DEFAULT_DISCOVERABLE_TIMEOUT){
//	    		global.bu.setDiscoverableTimeout( (int)(currentTimestamp - startTimestamp));
//	    	} 	
//    	}
    	
//    	discoverableThread = new DiscoverableThread(handler);
//		discoverableThread.start();
//		discoverableThread.isRunning = true;
    	
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
//		Log.d(global.TAG, "[SettingFragment] onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
//		discoverableThread.stopThread();
//		Log.d(global.TAG, "[SettingFragment] onStop");
		super.onStop();
	}

	private String formatTimeRemaining(int timeout) {
        StringBuilder sb = new StringBuilder(6);    // "mmm:ss"
        int min = timeout / 60;
        sb.append(min).append(':');
        int sec = timeout - (min * 60);
        if (sec < 10) {
            sb.append('0');
        }
        sb.append(sec);
        return sb.toString();
    }
    
    @Override
    public void onDestroy(){
    	//discoverableThread.isRunning = false;
//    	Log.d(global.TAG, "[SettingFragment] onDestroy");
    	m_IsRunning = false;
    	super.onDestroy();
    }
    
	private void initComponents(){
    	cb_discoverable = (CheckBox)m_settingsView.findViewById(R.id.setting_discoverable);
//    	cb_discoverable.setChecked(global.bu.getDiscoverable());
    	cb_discoverable.setOnCheckedChangeListener(new CheckedChangeEvent());
    	
    	edit_devicename = (EditText)m_settingsView.findViewById(R.id.edit_devicename);
    	
//    	edit_devicename.setText(global.bu.getName());
	}
	
    class CheckedChangeEvent implements CheckBox.OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton cb, boolean b) {
			if (cb == cb_discoverable){
				if (b){
					startTimestamp = System.currentTimeMillis();
//					Log.d(global.TAG, "Begin Thread");
					
			      	discoverableThread = new DiscoverableThread(handler);
			      	discoverableThread.start();
			      	m_IsRunning = true;
					
//					global.bu.setDiscoverable(DEFAULT_DISCOVERABLE_TIMEOUT);
				}else{
//					Log.d(global.TAG, "Stop Thread");
					m_IsRunning = false;
					if(getActivity() != null){
						cb_discoverable.setText(getActivity().getResources().getString(R.string.setting_Discoverable));
					}
					
//					global.bu.setDiscoverable(0);
				}
			}
		}
    }
	
    public class DiscoverableThread extends Thread implements Runnable {
    	private Handler mHandler;
    	public boolean isRunning = false;
    	
    	public DiscoverableThread(Handler handler) {
    		mHandler = handler;
    	}

    	@Override
    	public void run() {
    		while (m_IsRunning){
    			synchronized (lock) {
        			try{
        				mHandler.sendEmptyMessage(0);
        			}catch (Exception e){
//        				Log.d(global.TAG, "SettingFragment Thread exception");
        			}
//        			CommonUtils.sleep(1000);
				}
    		}
    	}
    	
    	public void stopThread() {   
    		isRunning = false;
    	}
	
    }
}
