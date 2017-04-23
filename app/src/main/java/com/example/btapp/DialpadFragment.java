/*
 * 拨号界面
 */
package com.example.btapp;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.btutil.ActionConstant;
import com.example.btutil.BtService;

public class DialpadFragment extends Fragment {
	private static final String TAG = "yzh";
    private static final int H_GET_BINDER = 0x10;
    private static final int H_GET_CONNECT_STATUS = 0x11;

	View m_DialpadView;
	EditText m_editNumber;
	TextView m_BondedText;
	Handler longclickhandler=new Handler();
	
	private boolean mShowDialPadFlag = false;

	private BtService.MyBinder myBinder;
	
	public void UpdateDeviceStats(boolean show, String showContect) {
		if (show) {
            if (m_BondedText != null) {
//                m_BondedText.setText(R.string.please_conn_device);
                m_BondedText.setText(showContect);
            }
        } else {

	    }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});

		m_BondedText = (TextView)m_DialpadView.findViewById(R.id.BondedDeice);
		//InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.hideSoftInputFromWindow(m_editNumber.getWindowToken(), 0);
		m_editNumber.setInputType(InputType.TYPE_NULL);
		setupKeypad(m_DialpadView);
		
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActionConstant.ACTION_HF_STATUS_CHANGE);
        getActivity().registerReceiver(mReceiver, filter);

        mHandler.sendEmptyMessageDelayed(H_GET_BINDER, 2000);
	}
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
			if(action.equals(ActionConstant.ACTION_HF_STATUS_CHANGE)) {
                String hfStatus = intent.getStringExtra("hfStatus");
                if(hfStatus.equals("MG3")) { //hfStatus==3时表示
                    UpdateDeviceStats(true, "蓝牙设备连接");

                } else if (hfStatus.equals("MG1")) {
                    UpdateDeviceStats(true, "蓝牙设备未连接");
                }
            }
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(TAG, "[DialpadFragment]:onCreateView");
		return m_DialpadView;
	}

	@Override
	public void onResume() {
		super.onResume();
        Log.v(TAG, "[DialpadFragment]:onResume");
    }

	@Override
	public void onPause() {
		super.onPause();
        Log.v(TAG, "[DialpadFragment]:onPause()");
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mReceiver);
        Log.v(TAG, "[DialpadFragment]:onDestroy()");
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
			return false;
		}
	};
	
	Runnable runnable=new Runnable() {
	    @Override
	    public void run() {
	    	keyPressed(KeyEvent.KEYCODE_DEL);
	    	longclickhandler.postDelayed(this, 200);
	    }
	};
	
	private class KeyBtnListener implements ImageButton.OnClickListener {

		@Override
		public void onClick(View arg0) {
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
			} else if (id == R.id.dial_backspace_btn) { //删除按钮
				longclickhandler.removeCallbacks(runnable);
				keyPressed(KeyEvent.KEYCODE_DEL);
			} else if (id == R.id.dial_btnDial) {  //拨号按钮
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
             Log.d(TAG, "number:" + number);
             if (myBinder != null) {
                 //拨号
                 myBinder.dial(number);

             } else {
                 Log.e(TAG, "myBinder is null.");
             }
             //本来应该弹出拨号界面 现在这里没有！！！
//			 Intent  intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel://" + number));
//			 final Intent intent = newDialNumberIntent(number);
//			 startActivity(intent);
		 }
	 }



	private Intent newDialNumberIntent(String number) {
//	        final Intent intent = new Intent(Intent.ACTION_CALL_PRIVILEGED,
//	                                         Uri.fromParts("tel", number, null));
//	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        return null;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case H_GET_BINDER:
                    myBinder = ((CSRBluetoothDemoActivity)getActivity()).getMyBinder();
                    if (myBinder == null) {
                        UpdateDeviceStats(true, "不能获取蓝牙服务");
                    } else {
                        UpdateDeviceStats(true, "获取蓝牙服务");
                        mHandler.sendEmptyMessage(H_GET_CONNECT_STATUS);
                    }
                    break;

                case H_GET_CONNECT_STATUS:
                    myBinder.inquireHFStatus();
                    break;
            }
        }
    };

}
