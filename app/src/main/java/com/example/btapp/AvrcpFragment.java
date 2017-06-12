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
//import android.bluetooth.BluetoothPhonebookClient;
//import android.bluetooth.BluetoothPhonebookClient.BluetoothPhonebookClientIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.btutil.BtService;
//音乐播放界面
public class AvrcpFragment extends Fragment {
    private final static String TAG = "yzh";

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
	private Button mBtnVoiceToPhone;
    private Button mBtnVoiceToBT;
    private Button mBtnVoiceToPhoneOrBT;

	private BtService.MyBinder myBinder;
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser == true) {
//			Log.d(global.TAG, "[AvrcpFragment]: Show View");
			mShowArcvpFlag = true;
//			updatePlayStats();
		} else {
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

        mBtnVoiceToPhone = (Button)mMainView.findViewById(R.id.btn_voiceToPhone);
        mBtnVoiceToBT = (Button)mMainView.findViewById(R.id.btn_voiceToBT);

        mBtnListener = new AvrcpBtnListener();
		mPlayPre.setOnClickListener(mBtnListener);
		mPlayPause.setOnClickListener(mBtnListener);
		mStop.setOnClickListener(mBtnListener);
		mPlayNext.setOnClickListener(mBtnListener);
		mPause.setOnClickListener(mBtnListener);
		mAudioSet.setOnClickListener(mBtnListener);
        mPlaySatas = (TextView)mMainView.findViewById(R.id.audio_playstats);

        //设置声音切换按钮的监听事件
        mBtnVoiceToPhone.setOnClickListener(mBtnListener);
        mBtnVoiceToBT.setOnClickListener(mBtnListener);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothIntent.BLUETOOTH_AVRCP_PLAY_BACK_STATUS_IND_ACTION);
//        filter.addAction(BluetoothA2dpSink.ACTION_A2DP_SINK_STATUS_CHANGED);
//        getActivity().registerReceiver(mReceiver, filter);

		myBinder = ((BluetoothActivity)getActivity()).getMyBinder();
        if (myBinder.getMusicStatic()) {
            mPlaySatas.setText(R.string.avrcp_playing);

        } else {
            mPlaySatas.setText(R.string.avrcp_noplaying);
        }

        Log.v(TAG, "[AvrcpFragment]: onCreate()");
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
    
	private void updatePlayStats(String content){
        if(mPlaySatas != null){
            mPlaySatas.setText(content);
        }
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.v(TAG, "[AvrcpFragment]: onStart()");
    }

	@Override
	public void onResume() {
		super.onResume();
        updatePlayStats("蓝牙音乐");
		Log.v(TAG, "[AvrcpFragment]: onResume()");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "[AvrcpFragment]: onPause()");
    }

	@Override
	public void onStop() {
		super.onStop();
		Log.v(TAG, "[AvrcpFragment]: onStop()");
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
//		getActivity().unregisterReceiver(mReceiver);
		Log.v(TAG, "[AvrcpFragment]: onDestroy()");
    }



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "[AvrcpFragment]: onCreateView()");
		return mMainView;
	}
	
	private class AvrcpBtnListener implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			int id = arg0.getId();
			if (id == R.id.avrcp_play_next) {
				myBinder.nextMusic();

			} else if (id == R.id.avrcp_play_pause) {
        		myBinder.playOrPauseMusic();

			} else if (id == R.id.avrcp_play_pre) {
				myBinder.preMusic();

			} else if (id == R.id.avrcp_stop) {
                myBinder.stopMusic();

			} else if (id == R.id.avrcp_pause) {
                myBinder.playOrPauseMusic();

			} else if(id == R.id.audio_set){
//				callAudioSet();
			} else if (id == R.id.btn_voiceToPhone) {
                myBinder.switchVoiceToPhone();

            } else if (id == R.id.btn_voiceToBT) {
                myBinder.swtichVoiceToBT();

            }

		}
		 
	}
	
	private void callAudioSet() {
		/*try {
			Intent intent = new Intent();
			intent.setClassName("com.android.settings", "com.android.settings.apical.AudioSettings");
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(getActivity(), "Call Audio Settings: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}*/
	}

}
