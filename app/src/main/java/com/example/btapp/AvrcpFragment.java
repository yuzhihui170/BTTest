package com.example.btapp;

//import com.csr.BTApp.apical.util.KeyEventReceiver;
//import com.csr.BTApp.apical.util.ToastUtils;
//import com.csr.BTApp.apical.util.XGBtCommond;
//import com.csr.BTApp.common.BluetoothUtils;
//import com.csr.BTApp.common.CommonUtils;
//import com.csr.BTApp.common.global;
//import com.csr.bluetooth.BluetoothIntent;

import android.app.Fragment;
//import android.bluetooth.BluetoothA2dpSink;
//import android.bluetooth.BluetoothAvrcpCtl;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothPhonebookClient;
//import android.bluetooth.BluetoothPhonebookClient.BluetoothPhonebookClientIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AvrcpFragment extends Fragment {

	private TextView mPlaySatas;
	private ImageButton mPlayPre;
	private ImageButton mPlayPause;
	private ImageButton mStop;
	private ImageButton mPlayNext;
	private ImageButton mPause;
	private ImageButton mAudioSet;
	private View mMainView;
	private AvrcpBtnListener mBtnListener;
	private boolean mShowArcvpFlag = false;
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if(isVisibleToUser == true){
//			Log.d(global.TAG, "[AvrcpFragment]: Show View");
			mShowArcvpFlag = true;
			UpdatePlayStats();
		}else{
//			Log.d(global.TAG, "[AvrcpFragment]: Hide View");
			mShowArcvpFlag = false;
//			ToastUtils.cancelCurrentToast();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		mMainView = inflater.inflate(R.layout.avrcp_fragment, (ViewGroup)getActivity().findViewById(R.id.tabpage_frame), false);
		mPlayPre = (ImageButton)mMainView.findViewById(R.id.avrcp_play_pre);
		mPlayPause = (ImageButton)mMainView.findViewById(R.id.avrcp_play_pause);
		mStop = (ImageButton)mMainView.findViewById(R.id.avrcp_stop);
		mPlayNext = (ImageButton)mMainView.findViewById(R.id.avrcp_play_next);
		mPause = (ImageButton)mMainView.findViewById(R.id.avrcp_pause);
		mAudioSet = (ImageButton)mMainView.findViewById(R.id.audio_set);
		
		mBtnListener = new AvrcpBtnListener();
		mPlayPre.setOnClickListener(mBtnListener);
		mPlayPause.setOnClickListener(mBtnListener);
		mStop.setOnClickListener(mBtnListener);
		mPlayNext.setOnClickListener(mBtnListener);
		mPause.setOnClickListener(mBtnListener);
		mAudioSet.setOnClickListener(mBtnListener);
		
		mPlaySatas = (TextView)mMainView.findViewById(R.id.audio_playstats);
		mPlaySatas.setText(R.string.avrcp_noplaying);
		
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothIntent.BLUETOOTH_AVRCP_PLAY_BACK_STATUS_IND_ACTION);
//        filter.addAction(BluetoothA2dpSink.ACTION_A2DP_SINK_STATUS_CHANGED);
//        getActivity().registerReceiver(mReceiver, filter);
        
	}
	

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(BluetoothIntent.BLUETOOTH_AVRCP_PLAY_BACK_STATUS_IND_ACTION)) {
//            	int playBackStatus = intent.getIntExtra(BluetoothIntent.AVRCP_PLAYBACK_STATUS, -1);
//        		
//                	if(playBackStatus == BluetoothAvrcpCtl.CSR_BT_AVRCP_PLAYBACK_STATUS_ERROR){
//                	}else if(playBackStatus == BluetoothAvrcpCtl.CSR_BT_AVRCP_PLAYBACK_STATUS_PAUSED){
//                		Log.d(global.TAG, "[AvrcpFragment]: avrcp pause");
//                		mPlaySatas.setText(R.string.avrcp_noplaying);
//                		
//                	}else if(playBackStatus == BluetoothAvrcpCtl.CSR_BT_AVRCP_PLAYBACK_STATUS_PLAYING){
//                		Log.d(global.TAG, "[AvrcpFragment]: avrcp playing");
//                    	mPlaySatas.setText(R.string.avrcp_playing);
//                    		
//                	}else if(playBackStatus == BluetoothAvrcpCtl.CSR_BT_AVRCP_PLAYBACK_STATUS_STOPPED){
//                	}else if(playBackStatus == BluetoothAvrcpCtl.CSR_BT_AVRCP_PLAYBACK_STATUS_FWD_SEEK){
//                	}else if(playBackStatus == BluetoothAvrcpCtl.CSR_BT_AVRCP_PLAYBACK_STATUS_REV_SEEK){
//                	}
//            }else if(action.equals(BluetoothA2dpSink.ACTION_A2DP_SINK_STATUS_CHANGED)) {
//        		if(mShowArcvpFlag){
//        			String AvrcpAddress = global.ConnectedDevices.get("AVRCP");
//        			String A2dpAddress = global.ConnectedDevices.get("A2DP");
//        			if(AvrcpAddress != null &&!AvrcpAddress.isEmpty()
//        					&& A2dpAddress != null &&!A2dpAddress.isEmpty()) {
//        			}else{
//        				if(mPlaySatas != null){
//        					mPlaySatas.setText(R.string.avrcp_noplaying);
//        				}
//        				if(getActivity() != null){
//        					ToastUtils.showMessage(getActivity(), getActivity().getResources().getString(R.string.avrcp_a2dp_unconnected));
//        				}else{
//        				}
//        			}
//        		}
//        	}
        }
    };
    
	public void UpdatePlayStats(){
//		String AvrcpAddress = global.ConnectedDevices.get("AVRCP");
//		String A2dpAddress = global.ConnectedDevices.get("A2DP");
//		if(AvrcpAddress != null &&!AvrcpAddress.isEmpty()
//				&& A2dpAddress != null &&!A2dpAddress.isEmpty()) {
//        	SharedPreferences sp = getActivity().getSharedPreferences("BTMusic_Stats",  Context.MODE_PRIVATE);
//    		if(!sp.getBoolean("Music_PlayFlag", false)){
//    			global.bu.avrcpPlay();
//			}
//		}else{
//			if(mPlaySatas != null){
//				mPlaySatas.setText(R.string.avrcp_noplaying);
//			}
//			if(getActivity() != null){
//				ToastUtils.showMessage(getActivity(), getActivity().getResources().getString(R.string.avrcp_a2dp_unconnected));
//			}else{
//			}
//		}
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
//		Log.v(global.TAG, "[AvrcpFragment]:onStop()");
		super.onStop();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
//		Log.v(global.TAG, "[AvrcpFragment]:onStart()");
		super.onStart();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
//		Log.v(global.TAG, "[AvrcpFragment]:onResume()");
		super.onResume();
		
	}

	@Override
	public void onDestroy() {
//		Log.v(global.TAG, "[AvrcpFragment]:onDestroy()");
		// TODO Auto-generated method stub
		super.onDestroy();
		pauseMusic();
//		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public void onPause() {
//		Log.v(global.TAG, "[AvrcpFragment]:onPause()");
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return mMainView;
	}
	
	private class AvrcpBtnListener implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			int id = arg0.getId();
			if (id == R.id.avrcp_play_next) {
				playNext();
			} else if (id == R.id.avrcp_play_pause) {
        		playMusic();
			} else if (id == R.id.avrcp_play_pre) {
				playPre();
			} else if (id == R.id.avrcp_stop) {
				stopMusic();
			} else if (id == R.id.avrcp_pause) {
        		pauseMusic();
			} else if(id == R.id.audio_set){
				callAudioSet();
			}
		}
		 
	}
	
	private void callAudioSet() {
		try {
			Intent intent = new Intent();
			intent.setClassName("com.android.settings", "com.android.settings.apical.AudioSettings");
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(getActivity(), "Call Audio Settings: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void playMusic() {
    	SharedPreferences sp = getActivity().getSharedPreferences("BTMusic_Stats",  Context.MODE_PRIVATE);
		if(!sp.getBoolean("Music_PlayFlag", false)){
//			String AvrcpAddress = global.ConnectedDevices.get("AVRCP");
//			String A2dpAddress = global.ConnectedDevices.get("A2DP");
//			if(AvrcpAddress != null &&!AvrcpAddress.isEmpty()
//					&& A2dpAddress != null &&!A2dpAddress.isEmpty()) {
//				global.bu.avrcpPlay();
//			}
//			else{
//				if(getActivity() != null){
//					ToastUtils.showMessage(getActivity(), getActivity().getResources().getString(R.string.avrcp_a2dp_unconnected));
//				}else{
//					Log.d(global.TAG, "[AvrcpFragment]: r == null");
//				}
//			}
		}
	}
	
	private void pauseMusic() {
    	SharedPreferences sp = getActivity().getSharedPreferences("BTMusic_Stats",  Context.MODE_PRIVATE);
//		if(sp.getBoolean("Music_PlayFlag", false)){
//			String AvrcpAddress = global.ConnectedDevices.get("AVRCP");
//			String A2dpAddress = global.ConnectedDevices.get("A2DP");
//			if(AvrcpAddress != null &&!AvrcpAddress.isEmpty()
//					&& A2dpAddress != null &&!A2dpAddress.isEmpty()) {
//				global.bu.avrcpPause();
//			}else{
//			}
//		}
	}
	
	private void playNext() {
    	SharedPreferences sp = getActivity().getSharedPreferences("BTMusic_Stats",  Context.MODE_PRIVATE);
//		if(sp.getBoolean("Music_PlayFlag", false)){
//			String AvrcpAddress = global.ConnectedDevices.get("AVRCP");
//			String A2dpAddress = global.ConnectedDevices.get("A2DP");
//			if(AvrcpAddress != null &&!AvrcpAddress.isEmpty()
//					&& A2dpAddress != null &&!A2dpAddress.isEmpty()) {
//				global.bu.avrcpForward();
//			}else{
//			}
//		}
	}
	
	private void playPre() {
    	SharedPreferences sp = getActivity().getSharedPreferences("BTMusic_Stats",  Context.MODE_PRIVATE);
//		String AvrcpAddress = global.ConnectedDevices.get("AVRCP");
//		String A2dpAddress = global.ConnectedDevices.get("A2DP");
//		if(AvrcpAddress != null &&!AvrcpAddress.isEmpty()
//				&& A2dpAddress != null &&!A2dpAddress.isEmpty()) {
//			global.bu.avrcpBackward();
//		}else{
//		}
	}
	
	private void stopMusic() {
//		String AvrcpAddress = global.ConnectedDevices.get("AVRCP");
//		String A2dpAddress = global.ConnectedDevices.get("A2DP");
//		if(AvrcpAddress != null &&!AvrcpAddress.isEmpty()
//				&& A2dpAddress != null &&!A2dpAddress.isEmpty()) {
//			global.bu.avrcpStop();
//		}else{
//		}
	}
}
