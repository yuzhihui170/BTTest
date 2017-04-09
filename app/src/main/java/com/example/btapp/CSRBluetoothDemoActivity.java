package com.example.btapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

//import com.csr.BTApp.PhonebookFragment.DownloadPBTask;
//import com.csr.BTApp.apical.util.*;
//
//import com.csr.BTApp.common.BluetoothUtils;
//import com.csr.BTApp.common.CSRDatabaseAdapter;
//import com.csr.BTApp.common.CommonUtils;
//import com.csr.BTApp.common.global;
//import com.csr.bluetooth.BluetoothIntent;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
//import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothAvrcpCtl;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothHFP;
//import android.bluetooth.BluetoothPhonebookClient;
//import android.bluetooth.BluetoothPhonebookClient.BluetoothPhonebookClientIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CSRBluetoothDemoActivity extends Activity {
	//private ToggleButton Btn_bluetooth_onoff;
	private TabFragment m_tabFragment;
	private ViewPager m_vpPageContainer;
	private FragmentManager m_fm;
	private ArrayList<Fragment> m_fragmentList;
	private SettingFragment m_settingFragment;
	private DeviceListFragment m_deviceListFragment;
	private PhonebookFragment m_pbFragment;
	private CallLogFragment m_callLogFragment;
	private DialpadFragment m_dialpadFragment;
	private AvrcpFragment m_avrcpFragment;
	
	private final int TAB_INDEX0 = 0;
	private final int TAB_INDEX1 = 1;
	private final int TAB_INDEX2 = 2;
	private final int TAB_INDEX3 = 3;
	private final int TAB_INDEX4 = 4;
	private final int TAB_INDEX5 = 5;
	private final int TAB_COUNT = 6;

	private int pbTypeCounter; 
	private int pbItemCounter;
	long TestTimestamp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        
        TestTimestamp = System.currentTimeMillis();
       
        // Set filter for BroadcastReceiver.
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        filter.addAction(BluetoothA2dpSink.ACTION_A2DP_SINK_STATUS_CHANGED);
//        filter.addAction(BluetoothHFP.ACTION_HFP_STATUS_CHANGED);
//		filter.addAction(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS_CHANGED);
//		filter.addAction(BluetoothIntent.BLUETOOTH_AVRCP_CON_IND_ACTION);
//		filter.addAction(BluetoothIntent.BLUETOOTH_AVRCP_DIS_CON_IND_ACTION);
//		filter.addAction(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_MSG_RECEIVED);
//		filter.addAction("android.intent.action.Nobondeddevice");
//        registerReceiver(mReceiver, filter); 

//        global.initProcess(this);
//        XGBtCommond.InitCommond(this);
        setContentView(R.layout.main);
        
        switchOnBT();
    	initTab();
    	initViewPager();
     }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		String able= getResources().getConfiguration().locale.getCountry(); 
//		Log.d(global.TAG, "[CSRBluetoothDemoActivity]:onConfigurationChanged:"+able);;
		
		Resources res = getResources(); 
		Configuration config = res.getConfiguration(); 
		config.locale = Locale.SIMPLIFIED_CHINESE;
		DisplayMetrics dm = res.getDisplayMetrics(); 
		res.updateConfiguration(config, dm); 
	}

	@Override
    public void onDestroy(){
//    	unregisterReceiver(mReceiver);
    	super.onDestroy();
//    	Log.d(global.TAG, "[CSRBluetoothDemoActivity]:onDestroy");
//    	AppRunState.SetAppDestory();
    }
    
    @Override
    public void onStart(){
    	super.onStart();
//    	Log.v(global.TAG, "[CSRBluetoothDemoActivity]:onStart:START");
    }
    
    @Override
    public void onResume(){
    	super.onResume();
//    	Log.v(global.TAG, "[CSRBluetoothDemoActivity]:onResume:START+"+(System.currentTimeMillis() - TestTimestamp) );
    //	refershPairedDeviceList();
    //	refershComponentsState();
//    	AppRunState.SetAppRun();
    }
    
    
    @Override
	protected void onPause() {
		super.onPause();
//		AppRunState.SetAppStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	//	super.onSaveInstanceState(outState);
	}

    private void switchOnBT() {
    	BluetoothAdapter btAdapter= BluetoothAdapter.getDefaultAdapter();
//    	Log.v(global.TAG, "[CSRBluetoothDemoActivity]:Device name:"+btAdapter.getName()
//    			+"Device Add:"+ btAdapter.getAddress());
    
    	boolean btState = btAdapter.getState() == BluetoothAdapter.STATE_ON;
     	if (!btState) {
     		btAdapter.enable();
//     		for (int i=0; i < 20; i++){
//     			CommonUtils.sleep(500);
//         		if (btAdapter.getState() != BluetoothAdapter.STATE_TURNING_OFF
//         				&&btAdapter.getState() != BluetoothAdapter.STATE_TURNING_ON)
//         			break;
//     		}
     	}
    }

    private void initViewPager(){
    	m_settingFragment = new SettingFragment();
    	m_deviceListFragment = new DeviceListFragment();
    	m_pbFragment = new PhonebookFragment();
    	m_callLogFragment = new CallLogFragment();
    	m_dialpadFragment = new DialpadFragment();
    	m_avrcpFragment = new AvrcpFragment();
    	
    	m_fragmentList = new ArrayList<Fragment>();
    	
    	m_fragmentList.add(m_dialpadFragment);
    	m_fragmentList.add(m_pbFragment);
    	m_fragmentList.add(m_callLogFragment);
    	m_fragmentList.add(m_avrcpFragment);
    	m_fragmentList.add(m_deviceListFragment);
    	m_fragmentList.add(m_settingFragment);
    	 
    	m_vpPageContainer = (ViewPager)findViewById(R.id.tabpage_frame);
    	m_vpPageContainer.setAdapter(new MyViewpagerAdapter(m_fm, m_fragmentList));
    	m_vpPageContainer.setOnPageChangeListener(new MyViewPagerOnChangeListener());
    	m_vpPageContainer.setCurrentItem(TAB_INDEX0);
    	m_tabFragment.setBtnOnSel(TAB_INDEX0);
    	
    	m_deviceListFragment.setOnCallBackListerner(new OnListCallBackListener());
    }
    
    private class OnListCallBackListener implements DeviceListFragment.OnCallBackListener {

		@Override
		public void onMessageSelected(int position) {
			// TODO Auto-generated method stub
			if(position == 0){
				
			}
		}
    
    }

    private void initTab(){
    	m_fm = getFragmentManager(); 
    	m_tabFragment = (TabFragment)m_fm.findFragmentById(R.id.tab_view);
    	m_tabFragment.setOnTabClickListerner(new TabOnClickListener());
    }
    
    private class TabOnClickListener implements TabFragment.OnTabClickListener {

		@Override
		public void onTabClick(int tabIndex) {
			if(m_vpPageContainer.getCurrentItem() == m_tabFragment.getCurTabPos())
				return;
			
			switch(tabIndex) {
			case TAB_INDEX0:
				m_vpPageContainer.setCurrentItem(TAB_INDEX0);
				break;
			case TAB_INDEX1:
				m_vpPageContainer.setCurrentItem(TAB_INDEX1);
				break;
			case TAB_INDEX2:
				m_vpPageContainer.setCurrentItem(TAB_INDEX2);
				break;
			case TAB_INDEX3:
				m_vpPageContainer.setCurrentItem(TAB_INDEX3);
				break;
			case TAB_INDEX4:
				m_vpPageContainer.setCurrentItem(TAB_INDEX4);
				break;
			case TAB_INDEX5:
				m_vpPageContainer.setCurrentItem(TAB_INDEX5);
				break;
			}
		}
    }
    
    private class MyViewPagerOnChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			switch(arg0) {
			case TAB_INDEX0:
				m_tabFragment.setBtnOnSel(TAB_INDEX0);
				break;
			case TAB_INDEX1:
				m_tabFragment.setBtnOnSel(TAB_INDEX1);
				break;
			case TAB_INDEX2:
				m_tabFragment.setBtnOnSel(TAB_INDEX2);
				break;
			case TAB_INDEX3:
				m_tabFragment.setBtnOnSel(TAB_INDEX3);
				break;
			case TAB_INDEX4:
				m_tabFragment.setBtnOnSel(TAB_INDEX4);
				break;
			case TAB_INDEX5:
				m_tabFragment.setBtnOnSel(TAB_INDEX5);
				break;
			}
		}
    	
    }
    
    public boolean getAirplaneMode(Context context){  
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),  
                              Settings.System.AIRPLANE_MODE_ON, 0) ;  
        return (isAirplaneMode == 1)?true:false;  
    }  
    
    private void refershPairedDeviceList(){
//    	Log.v(global.TAG, "CSRBluetoothDemo:refershPairedDevices:START");
    	BluetoothAdapter btAdapter= BluetoothAdapter.getDefaultAdapter();
    	BluetoothDevice device;
    	
        Set<BluetoothDevice> devices= btAdapter.getBondedDevices();
        if(devices.size()>0) {
            for(Iterator<BluetoothDevice> iterator=devices.iterator();iterator.hasNext();){ 
            	device=iterator.next();
            	
            	Map<String,Object> item = new HashMap<String,Object>();
            	item.put("device", device.getName());
            	item.put("btaddress", device.getAddress());
            }
        }
//        Log.v(global.TAG, "CSRBluetoothDemo:refershPairedDevices:END");
    }
    
    class MyViewpagerAdapter extends FragmentPagerAdapter{
    	private ArrayList<Fragment> m_fragments;
		public MyViewpagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public MyViewpagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
			super(fm);
			m_fragments = fragments;
		}
		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case TAB_INDEX0:
				return m_fragments.get(TAB_INDEX0);
			case TAB_INDEX1:
				return m_fragments.get(TAB_INDEX1);
			case TAB_INDEX2:
				return m_fragments.get(TAB_INDEX2);
			case TAB_INDEX3:
				return m_fragments.get(TAB_INDEX3);
			case TAB_INDEX4:
				return m_fragments.get(TAB_INDEX4);
			case TAB_INDEX5:
				return m_fragments.get(TAB_INDEX5);
			}
			return null;
		}

		@Override
		public int getCount() {
			return m_fragments.size();
		}
    	
    }
    
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
		public void onReceive(Context context, Intent intent) {
//        	String action = intent.getAction();
//        	if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//        	}else if(action.equals(BluetoothA2dpSink.ACTION_A2DP_SINK_STATUS_CHANGED)) {
//        		Log.d(global.TAG, "[CSRBluetoothDemoActivity]:Receive ACTION_A2DP_SINK_STATUS_CHANGED");
//        		m_tabFragment.refreshComponentsState();
//        	}else if(action.equals(BluetoothHFP.ACTION_HFP_STATUS_CHANGED)) {
//        		Log.d(global.TAG, "[CSRBluetoothDemoActivity]:Receive ACTION_HFP_STATUS_CHANGED");   		
//        		m_tabFragment.refreshComponentsState();   
//        	}else if(action.equals(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS_CHANGED)) {
//        		Log.d(global.TAG, "[CSRBluetoothDemoActivity]:Receive BLUETOOTH_PBAPC_CONNECTION_STATUS_CHANGED");
//        		m_tabFragment.refreshComponentsState();
//        		 int state = intent.getIntExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS, 0);
//                 switch(state) {
//                 case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_COMMUNICATING:
//                	 break;
//                 case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_CONNECTED:
//                	 break;
//                 case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_DISCONNECTED:
//                	 break;
//                 }
//        	}else if (action.equals(BluetoothIntent.BLUETOOTH_AVRCP_CON_IND_ACTION)) {
//        		m_tabFragment.refreshComponentsState();
//                  
//        	}else if (action.equals(BluetoothIntent.BLUETOOTH_AVRCP_DIS_CON_IND_ACTION)) {
//        		m_tabFragment.refreshComponentsState();
//        	   
//        	}else if(action.equals(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_MSG_RECEIVED)) {
//        		int message = intent.getIntExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_MSG, 0);
//        		switch(message) {
//        		case BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_PULL_PB_CFM://锟斤拷锟斤拷锟斤拷某一锟斤拷PB
//        			Log.v(global.TAG, "CSR_ANDROID_PAC_PULL_PB_CFM:"+"锟斤拷锟斤拷锟斤拷某一锟斤拷PB" +pbTypeCounter++);
//        			break;
//        		case BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_PULL_PB_IND://锟揭碉拷锟界话锟斤拷目通知
//        			Log.v(global.TAG, "CSR_ANDROID_PAC_PULL_PB_IND:"+"锟揭碉拷锟界话锟斤拷目"+pbItemCounter++);
//        			break;
//        		}
//        		int result = intent.getIntExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_MSG_RESULT, 0);
//        		switch(result) {
//        		case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_RESULT_CODE_SUCCESS:
//        			Log.v(global.TAG, "BLUETOOTH_PBAPC_RESULT_CODE_SUCCESS");
//        			break;
//        		case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_RESULT_CODE_FAILURE:
//        			Log.v(global.TAG, "BLUETOOTH_PBAPC_RESULT_CODE_FAILURE");
//        			break;
//        		}
//        	}
        }
    };

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
//		Log.v(global.TAG, "CSRBluetoothDemoActivity:onActivityResult");
		switch(resultCode) {
//		case ProfileList.RESULT_DONE:
//			m_deviceListFragment.connectAllProfile(data.getStringExtra("BTAddress"));
//			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int arg0, KeyEvent arg1) {
			if (arg0 == KeyEvent.KEYCODE_BACK){
//				Log.v(global.TAG, "CSRBluetoothDemoActivity:KeyEvent.KEYCODE_BACK");
			}else if(arg0 == KeyEvent.KEYCODE_HOME){
//				Log.v(global.TAG, "CSRBluetoothDemoActivity:KeyEvent.KEYCODE_HOME");
			}
		return super.onKeyDown(arg0, arg1);
	}
    
}