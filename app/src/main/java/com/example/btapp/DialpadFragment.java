/*
 * Copyright (C) Apical
 */
package com.example.btapp;

import java.util.Iterator;
import java.util.Set;

//import com.csr.BTApp.TabFragment.OnTabClickListener;
//import com.csr.BTApp.common.BluetoothUtils;
//import com.csr.BTApp.common.CommonUtils;
//import com.csr.BTApp.common.global;
//import com.csr.bluetooth.BluetoothIntent;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothAvrcpCtl;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothHFP;
//import android.bluetooth.BluetoothPhonebookClient;
//import android.bluetooth.BluetoothPhonebookClient.BluetoothPhonebookClientIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DialpadFragment extends Fragment {

	View m_DialpadView;
	EditText m_editNumber;
	TextView m_BondedText;
	Handler longclickhandler=new Handler();
	
	private BluetoothAdapter btAdapter;
	private BluetoothDevice device;
	private boolean mShowDialPadFlag = false;
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		
		if(isVisibleToUser == true){
//			Log.d(global.TAG, "[DialpadFragment]: Show View");
			mShowDialPadFlag = true;
			UpdateDeviceStats();
		}else{
//			Log.d(global.TAG, "[DialpadFragment]: Hide View");
			mShowDialPadFlag = false;
		}
	}
	
	public void UpdateDeviceStats(){
		if(mShowDialPadFlag){
			Set<BluetoothDevice> devices= btAdapter.getBondedDevices();
	        if(devices.size()>0) {
	        for(Iterator<BluetoothDevice> iterator=devices.iterator();iterator.hasNext();){ 
	        	device=iterator.next();
//	        	if(global.hfp.hfpGetConnectStatus(device.getAddress()) == BluetoothHFP.HFP_CONNECTED){
//	        			if(getActivity() != null && m_BondedText != null){
//	        		m_BondedText.setText(getActivity().getResources().getString(R.string.Had_conn_device) + device.getName());
//	        		}
//	        	}else{
//	        		if(m_BondedText != null){
//	        		m_BondedText.setText(R.string.please_conn_device);
//	        		}
//	        	}
	        }
	    }
	    else{
			if(m_BondedText != null){
			m_BondedText.setText(R.string.please_conn_device);
			}
	    }
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		m_DialpadView = inflater.inflate(R.layout.dial_fragment,
				(ViewGroup)getActivity().findViewById(R.id.tabpage_frame), false);
		m_editNumber = (EditText)m_DialpadView.findViewById(R.id.dial_number_edit);
		m_editNumber.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if ((m_editNumber.getText() != null)
						&& ("*#5220*0225#*".equals(m_editNumber.getText().toString().trim()))) {
					Intent intent = new Intent();
					intent.setClassName("com.android.settings", "com.android.settings.apical.FactorySetting");
					startActivity(intent);
					
					getActivity().finish();
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		m_BondedText = (TextView)m_DialpadView.findViewById(R.id.BondedDeice);
		btAdapter= BluetoothAdapter.getDefaultAdapter();
		//InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.hideSoftInputFromWindow(m_editNumber.getWindowToken(), 0);
		m_editNumber.setInputType(InputType.TYPE_NULL);
		setupKeypad(m_DialpadView);
		
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothHFP.ACTION_HFP_STATUS_CHANGED);
//        getActivity().registerReceiver(mReceiver, filter); 
	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
//			if(arg1.getAction().equals(BluetoothHFP.ACTION_HFP_STATUS_CHANGED)) {
//				UpdateDeviceStats();
//            }
		}
	};
	
	@Override
	public void onDestroy() {
//		Log.v(global.TAG, "[DialpadFragment]:onDestroy()");
		super.onDestroy();
//		getActivity().unregisterReceiver(mReceiver);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
//		Log.v(global.TAG, "[DialpadFragment]:onPause()");
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return m_DialpadView;
	}
	
	private void setupKeypad(View fragmentView) {
		fragmentView.findViewById(R.id.dial_btn0).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btn1).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btn2).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btn3).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btn4).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btn5).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btn6).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btn7).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btn8).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btn9).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btnAdd).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btnPound).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_btnStar).setOnClickListener(new KeyBtnListener());
		
		fragmentView.findViewById(R.id.dial_btnDial).setOnClickListener(new KeyBtnListener());
		fragmentView.findViewById(R.id.dial_backspace_btn).setOnClickListener(new KeyBtnListener());
		
		
		fragmentView.findViewById(R.id.dial_backspace_btn).setOnLongClickListener(olcl);
	}
	
	OnLongClickListener olcl = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View arg0) {
			longclickhandler.postDelayed(runnable, 200);
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	Runnable runnable=new Runnable() {
	    @Override
	    public void run() {
	        // TODO Auto-generated method stub
	    	keyPressed(KeyEvent.KEYCODE_DEL);
	    	longclickhandler.postDelayed(this, 200);
	    }
	};
	
	private class KeyBtnListener implements ImageButton.OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			int id = arg0.getId();
			if (id == R.id.dial_btn0) {
				keyPressed(KeyEvent.KEYCODE_0);
			} else if (id == R.id.dial_btn1) {
				keyPressed(KeyEvent.KEYCODE_1);
			} else if (id == R.id.dial_btn2) {
				keyPressed(KeyEvent.KEYCODE_2);
			} else if (id == R.id.dial_btn3) {
				keyPressed(KeyEvent.KEYCODE_3);
			} else if (id == R.id.dial_btn4) {
				keyPressed(KeyEvent.KEYCODE_4);
			} else if (id == R.id.dial_btn5) {
				keyPressed(KeyEvent.KEYCODE_5);
			} else if (id == R.id.dial_btn6) {
				keyPressed(KeyEvent.KEYCODE_6);
			} else if (id == R.id.dial_btn7) {
				keyPressed(KeyEvent.KEYCODE_7);
			} else if (id == R.id.dial_btn8) {
				keyPressed(KeyEvent.KEYCODE_8);
			} else if (id == R.id.dial_btn9) {
				keyPressed(KeyEvent.KEYCODE_9);
			} else if (id == R.id.dial_btnAdd) {
				keyPressed(KeyEvent.KEYCODE_PLUS);
			} else if (id == R.id.dial_btnPound) {
				keyPressed(KeyEvent.KEYCODE_POUND);
			} else if (id == R.id.dial_btnStar) {
				keyPressed(KeyEvent.KEYCODE_STAR);
			} else if (id == R.id.dial_backspace_btn) {
				longclickhandler.removeCallbacks(runnable);
				keyPressed(KeyEvent.KEYCODE_DEL);
			} else if (id == R.id.dial_btnDial) {
				dialButtonPressed();
			}
		}
	}
	
	 private void keyPressed(int keyCode) {
	        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
	        m_editNumber.onKeyDown(keyCode, event);

	    }
	 
	 private boolean isDigitsEmpty() {
	        return m_editNumber.length() == 0;
	    }
	
	 private void dialButtonPressed() {
		 if(!isDigitsEmpty()) {
			 String number = m_editNumber.getText().toString();
//			 Intent  intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel://" + number));
			 final Intent intent = newDialNumberIntent(number);
			 startActivity(intent);
		 }
	 }
	 
	 @Override
	public void onResume() {
		// TODO Auto-generated method stub
//		 Log.v(global.TAG, "[DialpadFragment]:onResume");
		super.onResume();
	}

	private Intent newDialNumberIntent(String number) {
//	        final Intent intent = new Intent(Intent.ACTION_CALL_PRIVILEGED,
//	                                         Uri.fromParts("tel", number, null));
//	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        return null;
	    }

}
