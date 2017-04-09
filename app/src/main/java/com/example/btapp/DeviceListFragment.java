/*
 * Copyright (C) Apical
 */
package com.example.btapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.csr.BTApp.AutoConnBtService.AutoConnBtThread;
//import com.csr.BTApp.apical.util.ClsUtils;
//import com.csr.BTApp.common.BluetoothUtils;
//import com.csr.BTApp.common.CommonUtils;
//import com.csr.BTApp.common.global;
//import com.csr.bluetooth.BluetoothIntent;

import android.R.bool;
import android.app.AlertDialog;
import android.app.Fragment;
//import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothAvrcpCtl;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothHFP;
//import android.bluetooth.BluetoothPhonebookClient;
//import android.bluetooth.BluetoothPhonebookClient.BluetoothPhonebookClientIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btutil.BTUtil;

public class DeviceListFragment extends Fragment{
	private static String TAG = "yzh";
	private static final int DIALOG_STATE_PAIRED_AND_CONNECTED = 0;
	private static final int DIALOG_STATE_PAIRED_BUT_NOT_CONNECTED = 1;
	private static final int DIALOG_STATE_NO_PAIR = 2;
	private static final int DIALOG_STATE_CONNECTING = 3;
	private static final int DIALOG_STATE_PAIRING = 4;
	private static final int DIALOG_STATE_PAIRED_AND_CONNECTING = 5;
	
	private int tick = 0;
//	private EditText mEditPassword;
	private View m_devicesView;
	private static final int MSG_ADD_ITEM = 1;
	
	private DeviceListAdapter dlAdapter;
	private ArrayList<Map<String, Object>> data;
	private ListView lv;
	private ImageButton search_btn;
	private TextView m_listSerchingText;
	private TextView m_listEmptyText;

//	private BluetoothAdapter btAdapter;
//	private BluetoothDevice device;
	private ShowSearchTextThread mSearchThread;
	
	private String BondedBTAddress;
	private String selectedBTAddress;
	private String selectedState;
	private String selectedDeviceName;
	//private int selectedItemPosition;
	BTDialogFragment m_dialog;
	
	private boolean ProfileHaveHFPFlag = false;
	private boolean ProfileHaveA2DPFlag = false;
	private boolean ProfileHaveAVRCPFlag = false;
	private static byte[]  lock= new byte[0];

	private BTUtil mBTUtil;
	
	OnCallBackListener mCallback;
	
	static interface OnCallBackListener {
		void onMessageSelected(int position);
	}
    
	public void setOnCallBackListerner(OnCallBackListener listener) {
		mCallback = listener;
	}
	
    @Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
//		if(isVisibleToUser == true){
//			Log.d(global.TAG, "[DeviceListFragment]: Show View");
//		}else{
//			Log.d(global.TAG, "[DeviceListFragment]: Hide View");
//		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	selectedBTAddress = "";
    	selectedState = "";
        //Init ArrayList and ListView
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	m_devicesView = inflater.inflate(R.layout.devicelist_fragment,
    			(ViewGroup)getActivity().findViewById(R.id.tabpage_frame), false);
    	
        data = new ArrayList<Map<String,Object>>();
        dlAdapter = new DeviceListAdapter(getActivity(), data);
        lv = (ListView)m_devicesView.findViewById(R.id.contact_tile_list);
        lv.setAdapter(dlAdapter);
        lv.setOnItemClickListener(listener);
        
        m_listEmptyText = (TextView)m_devicesView.findViewById(R.id.list_empty_text);
//        lv.setEmptyView(m_listEmptyText);
        
        m_listSerchingText=(TextView)m_devicesView.findViewById(R.id.list_searching_tv);
        
        search_btn = (ImageButton)m_devicesView.findViewById(R.id.list_search_btn);
        search_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				data.clear();
//				addPairedDevicesToListview();
//		        if (btAdapter.isDiscovering()){
//		        	btAdapter.cancelDiscovery();
//		        }
//				btAdapter.startDiscovery();
				if (!isRunning) {
					mSearchThread = new ShowSearchTextThread();
					mSearchThread.start();
				}
			}
		});
    	
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED); //Gary_debug
//        filter.addAction(BluetoothA2dpSink.ACTION_A2DP_SINK_STATUS_CHANGED);
//        filter.addAction(BluetoothHFP.ACTION_HFP_STATUS_CHANGED);
//        filter.addAction(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS_CHANGED);
//        filter.addAction(BluetoothIntent.BLUETOOTH_AVRCP_CON_IND_ACTION);
//        filter.addAction(BluetoothIntent.BLUETOOTH_AVRCP_DIS_CON_IND_ACTION);
//        filter.addAction(BluetoothDevice.ACTION_UUID);
//        filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
//        filter.addAction("android.bluetooth.device.action.ProfileHaveHFP");
//        filter.addAction("android.bluetooth.device.action.ProfileHaveA2DP");
//        filter.addAction("android.bluetooth.device.action.ProfileHaveAVRCP");
//        getActivity().registerReceiver(mReceiver, filter); 
        
//        btAdapter= BluetoothAdapter.getDefaultAdapter();
//        if(btAdapter == null) {
//        	Log.d(global.TAG, "");
//        }else {
//        	Log.d(global.TAG, "");
//        }
        
        //Add paired devices to list.
        addPairedDevicesToListview();

		mBTUtil = BTUtil.getInstance();
        mBTUtil.openDevice();
		Log.d(TAG, "[DeviceListFragment] onCreate");
    }
    
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
         	 switch (msg.what){
          	 	case 0:
          	 		m_listSerchingText.setText(R.string.DL_Searching_device);
          	 		break;
          	 }
        }
    };

	private boolean isRunning = false;
    public class ShowSearchTextThread extends Thread {
    	@Override
    	public void run() {
    		synchronized (lock) {
				isRunning = true;
//	    		CommonUtils.sleep(500);
	    		mHandler.sendEmptyMessage(0);
				if (mBTUtil != null) {
					mBTUtil.search();
				} else {
					return;
				}
				while (isRunning) {
					String result = mBTUtil.searchResult();
					if (result == null) {
//						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
//				mBTUtil.stopSearch();
				Log.d(TAG, " Search Thread exit.");
    		}
    	}

		public void stopThread() {
			isRunning = false;
		}
    }
    
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.d(TAG, "[DeviceListFragment] onCreateView");
		return m_devicesView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "[DeviceListFragment] onDestroyView");
	}

	@Override
    public void onStart(){
    	super.onStart();
    	Log.d(TAG, "[DeviceListFragment] onStart");
    }

	@Override
	public void onResume() {
		super.onResume();
//		beginDiscovery();
		Log.d(TAG, "[DeviceListFragment] onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "[DeviceListFragment] onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		mSearchThread.stopThread();
//		btAdapter.cancelDiscovery();
		//	getActivity().setTitle(getResources().getString(R.string.app_name));
		Log.d(TAG, "[DeviceListFragment] onStop");
	}
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
//    	getActivity().unregisterReceiver(mReceiver);
		mBTUtil.closeDevice();
		Log.d(TAG, "[DeviceListFragment] onDestroy");
	}

	public void beginDiscovery() {
    	  // Set filter for BroadcastReceiver.
        //Scan new devices.
//        if (btAdapter.isDiscovering()){
//        	Log.v(global.TAG, "Old process is discovering.");
//        	btAdapter.cancelDiscovery();
//        }
//        if (! btAdapter.isDiscovering()){
//        	Log.v(global.TAG, "Start discovery.");
//        	btAdapter.startDiscovery();
     //   	getActivity().setTitle(getResources().getString(R.string.app_name) + "     Scaning...");
//        }
    }

//	@Override
//    public Dialog onCreateDialog(int id, Bundle state){
//    	switch (id){
//	       
//    }
    
//    @Override
//    protected void onPrepareDialog(int id, Dialog dialog) {
//        Log.d(global.TAG, "Enter onPrepareDialog");
//        switch (id) {
//	        case DIALOG_PBAP_AUTHENTICATION: {
//	            mEditPassword.setText("");
//	            return;
//	        }
//        }
//    }


    private void refreshName(BluetoothDevice device, String name){ 
    	if (device == null)
            return;
		
        int position = -1;
        for (int i=0; i< data.size(); i++)
            if ( device.getAddress().equals(getAddress(data.get(i).get("state").toString())) )
                position = i;
        if (position == -1)
            return;
//        Log.v(global.TAG, "Enter refreshName: "+name);
        data.get(position).put("device", name);
        dlAdapter.notifyDataSetChanged();
    }
	
    public void refershState(String BTAddress){
    	if (BTAddress == null)
    		return;
    	
    	int position = -1;
        for (int i=0; i< data.size(); i++)
        	if ( BTAddress.equals(getAddress(data.get(i).get("state").toString())) )
        		position = i;
        if (position == -1)
        	return;
        
//		switch (global.bu.getBluetoothPairState(BTAddress)){               			
//		case BluetoothDevice.BOND_BONDING:
//    		data.get(position).put("state", 
//    				String.format(getActivity().getResources().getString(R.string.DL_Pairing)+"(%s)",
//    						BTAddress)
//    				);
//    		dlAdapter.notifyDataSetChanged();
//			break;
//		case BluetoothDevice.BOND_NONE:
//    		data.get(position).put("state", 
//    				String.format("%s(%s)",
//    						getActivity().getResources().getString(R.string.DL_PairWithThisDevice),
//    						BTAddress)
//			);
//    		data.get(position).put("profile_btn", false);
//    		dlAdapter.notifyDataSetChanged();
//			break;
//		case BluetoothDevice.BOND_BONDED:
//        	data.get(position).put("state", 
//        			String.format("%s(%s)",
//        					getActivity().getResources().getString(R.string.DL_PairedAndConnecting),
//        					BTAddress)
//        			);
//        	data.get(position).put("profile_btn", true);
//        	//Check profiles state
//        	String connectedStateStr = "";
//        	String connectingStateStr = "";
//        	switch (global.hfp.hfpGetConnectStatus(BTAddress)){
//            	case BluetoothHFP.HFP_CONNECTED:
//            		connectedStateStr = "HFP";
//            		break;
//            	case BluetoothHFP.HFP_CONNECTING:
//            		connectingStateStr = "HFP";
//            		break;
//            	case BluetoothHFP.HFP_DISCONNECTING:
//            		break;
//            	case BluetoothHFP.HFP_DISCONNECTED:
//            		break;
//        	}
//			switch (global.bu.a2dpGetConnectState(BTAddress)){
//        		case BluetoothUtils.BLUETOOTH_A2DP_SINK_CONNECTED:
//            		if (connectedStateStr.equals(""))
//            			connectedStateStr = "A2DP";
//            		else
//            			connectedStateStr += ",A2DP";
//        			break;
//        		case BluetoothUtils.BLUETOOTH_A2DP_SINK_CONNECTING:
//            		if (connectingStateStr.equals(""))
//            			connectingStateStr = "A2DP";
//            		else
//            			connectingStateStr += ",A2DP";
//        			break;
//        		case BluetoothUtils.BLUETOOTH_A2DP_SINK_DISCONNECTED:
//        			break;
//			}
//    		switch (global.bu.pbapGetConnectStatus(BTAddress)){
//	    		case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_CONNECTED:
//            		if (connectedStateStr.isEmpty())
//            			connectedStateStr = "PBAP";
//            		else
//            			connectedStateStr += ",PBAP";
//	    			break;
//	    		case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_CONNECTING:
//            		if (connectingStateStr.equals("")){
//            			connectingStateStr = "PBAP";
//            		}
//            		else
//            			connectingStateStr += ",PBAP";
//        			break;
//	    		case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_DISCONNECTED:
//	    			break;
//	    		case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_DISCONNECTING:
//	    			break;
//	    		case BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_COMMUNICATING:
//            		if (connectedStateStr.equals("")){
//            			connectedStateStr = "PBAP";
//            		}
//            		else
//            			connectedStateStr += ",PBAP";
//	    			break;
//    		}
//    		
//    		switch (global.bu.avrcpGetAvrcpStatus(BTAddress)){
//    		case BluetoothAvrcpCtl.CSR_BT_AVRCP_CONNECTED:
//        		if (connectedStateStr.isEmpty())
//        			connectedStateStr = "AVRCP";
//        		else
//        			connectedStateStr += ",AVRCP";
//    			break;
//    		case BluetoothAvrcpCtl.CSR_BT_AVRCP_DISCONNECTED:
//    			break;
//    		}
//    		
//        	SharedPreferences sp = getActivity().getSharedPreferences("Connect_Stats",  Context.MODE_PRIVATE);
//    		if(!sp.getBoolean("ConnectFlag", true)){
//    			Log.v(global.TAG, "Why is not come here");
//        		data.get(position).put("state", 
//        				String.format(getActivity().getResources().getString(R.string.DL_PairedButNotConnected)+ "(%s)",
//        						BTAddress)
//        				);
//        		dlAdapter.notifyDataSetChanged();
//    		}else 
//    			if (! connectingStateStr.equals("")){
//        		data.get(position).put("state", 
//        				String.format(getActivity().getResources().getString(R.string.DL_Connecting) + "(%s)",
//        						connectingStateStr,
//        						BTAddress)
//        		);
//            	dlAdapter.notifyDataSetChanged();
//    		}else if (connectedStateStr.equals("")){
//	        		data.get(position).put("state", 
//	        				String.format(getActivity().getResources().getString(R.string.DL_PairedAndConnecting) + "(%s)",
//	        						BTAddress)
//	        		);
//	            	dlAdapter.notifyDataSetChanged();
//	    	}else{
//    			if(global.hfp.hfpGetConnectStatus(BTAddress) == BluetoothHFP.HFP_DISCONNECTED
//    					/*&& ProfileHaveHFPFlag*/){
//    				connectedStateStr = connectedStateStr + getActivity().getResources().getString(R.string.connecting_HFP);
//    			}
//    			if ( global.bu.a2dpGetConnectState(BTAddress) == BluetoothUtils.BLUETOOTH_A2DP_SINK_DISCONNECTED 
//    					/*&& ProfileHaveA2DPFlag*/){
//    				connectedStateStr = connectedStateStr + getActivity().getResources().getString(R.string.connecting_A2DP);
//    			}
//    	    	if(global.bu.avrcpGetAvrcpStatus(BTAddress) == BluetoothAvrcpCtl.CSR_BT_AVRCP_DISCONNECTED 
//    	    			/*&& ProfileHaveAVRCPFlag*/){
//    	    		connectedStateStr = connectedStateStr + getActivity().getResources().getString(R.string.connecting_AVRCP);
//    	    	}
//    			
//    			String strState = String.format(getActivity().getResources().getString(R.string.DL_PairedAndConnected) +"(%s)",
//						connectedStateStr,
//						BTAddress);
//        		data.get(position).put("state", 
//        				strState);
//        		
//        		//锟斤拷锟斤拷arraylist锟斤拷2元锟截碉拷位锟斤拷,锟斤拷锟斤拷锟斤拷锟接碉拷锟借备锟矫讹拷锟斤拷示
//        		if(0 != position){
//        			Collections.swap(data, 0, position);
//        		}
//        		
//            	dlAdapter.notifyDataSetChanged();
//	    	}
//
//        	break;
//		case BluetoothDevice.BOND_SUCCESS:
//			break;
//		}
		
		mCallback.onMessageSelected(0);
    }
	
    public void connectAllProfile(String BTAddress){
//    	Log.v(global.TAG, "DeviceListActivity:connectAllProfile:Start");
//    	
//    	HashMap<String, Boolean> profilesInDB =  global.dbAdapter.userGetProfiles(BTAddress);
//
//        if (profilesInDB.size() == 0) {
//        	Log.v(global.TAG, "DeviceListActivity:connectAllProfile:0000000000000000");
//            CommonUtils.scanProfile2db(BTAddress);
//        } else {
//        }
//    	
//    	Log.v(global.TAG, "profilesInDB Count:"+ String.valueOf(profilesInDB.size()));
//    	
//    	if (profilesInDB.containsKey("AVRCP")){
//    		if ((Boolean)profilesInDB.get("AVRCP")){
////    			if(global.bu.avrcpGetAvrcpStatus(BondedBTAddress) != BluetoothAvrcpCtl.CSR_BT_AVRCP_CONNECTED ){
////    			global.bu.avrcpDisconnectAll();
////		    	global.bu.avrcpConnect(BTAddress);
////		    	CommonUtils.sleep(300);
////    			}
//    		}else{
//    			global.bu.avrcpDisconnectAll();
//    		}
//    	}
//    	
//    	if (profilesInDB.containsKey("HFP") )
//    		if ((Boolean)profilesInDB.get("HFP")){
////		    	if ( global.hfp.hfpGetConnectStatus(BTAddress) != BluetoothHFP.HFP_CONNECTED){
////		    		global.hfp.hfpDisconnectAll();
////		    		global.hfp.hfpConnect(BTAddress);
////		    		CommonUtils.sleep(300);
////		    	}
//    		}else{
//    			global.hfp.hfpDisconnect(BTAddress);
//    		}	
//    	
//    	if (profilesInDB.containsKey("A2DP"))
//    		if ((Boolean)profilesInDB.get("A2DP")){
//		    	if ( global.bu.a2dpGetConnectState(BTAddress) != BluetoothUtils.BLUETOOTH_A2DP_SINK_CONNECTED  ){
//		    		global.bu.a2dpDisconnectAll();
//		    		global.bu.a2dpConnect(BTAddress);
//		    		CommonUtils.sleep(300);
//		    	}
//    		}else{
//    			global.bu.a2dpDisconnect(BTAddress);
//    		}
//    	
//    	if (profilesInDB.containsKey("PBAP")){
//    		if ((Boolean)profilesInDB.get("PBAP")){
////    			if ( global.bu.pbapGetConnectStatus(BTAddress) != BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_CONNECTED ){
////    				global.bu.pbapDisconnectAll();
////    				if (global.dbAdapter.getPBAPConnectMode()){
////    					Log.v(global.TAG, "PBAP 3333333333333333");
////	        			global.bu.pbapConnect(BTAddress);
////	        			CommonUtils.sleep(300);
////	        		}
////	        			
////                }
//    		}else{
//    			global.bu.pbapDisconnect(BTAddress);
//    		}
//    	}
//
//    	refershState(BTAddress);
//    	Log.v(global.TAG, "DeviceListActivity:connectAllProfile:End");
    }
    
    private int checkState(String state){
    	Pattern pattern;
    	Matcher matcher;
    	pattern = Pattern.compile(getActivity().getResources().getString(R.string.DL_PairWithThisDevice));
    	matcher = pattern.matcher(state);
    	if (matcher.find()){
    		return DIALOG_STATE_NO_PAIR;
    	}
    	
    	pattern = Pattern.compile(getActivity().getResources().getString(R.string.DL_Pairing));
    	matcher = pattern.matcher(state);
    	if (matcher.find()){
    		return DIALOG_STATE_PAIRING;
    	}
    	
    	pattern = Pattern.compile(getActivity().getResources().getString(R.string.DL_PairedButNotConnected));
    	matcher = pattern.matcher(state);
    	if (matcher.find()){
    		return DIALOG_STATE_PAIRED_BUT_NOT_CONNECTED;
    	}
    	
    	pattern = Pattern.compile(getActivity().getResources().getString(R.string.DL_PairedAndConnecting));
    	matcher = pattern.matcher(state);
    	if (matcher.find()){
    		return DIALOG_STATE_PAIRED_AND_CONNECTING;
    	}
    	
    	String s = String.format(getActivity().getResources().getString(R.string.DL_PairedAndConnected), "(.+?)");
    	pattern = Pattern.compile(s);
    	matcher = pattern.matcher(state);
    	if (matcher.find()){
    		return DIALOG_STATE_PAIRED_AND_CONNECTED;
    	}
    	
    	s = String.format(getActivity().getResources().getString(R.string.DL_Connecting), "(.+?)");
    	pattern = Pattern.compile(s);
    	matcher = pattern.matcher(state);
    	if (matcher.find()){
    		return DIALOG_STATE_CONNECTING;
    	}
    	return -1;
    }
    
    private void addPairedDevicesToListview(){
//        Set<BluetoothDevice> devices= btAdapter.getBondedDevices();
////        Log.d(global.TAG, "How many devices  (" + devices.size() + ").");
//        if(devices.size()>0) {
//            for(Iterator<BluetoothDevice> iterator=devices.iterator();iterator.hasNext();){
//            	device=iterator.next();
//            	addItem(device.getName(),"("+device.getAddress()+")", true);
//            	refershState(device.getAddress());
//            }
//        }
        dlAdapter.notifyDataSetChanged();
    }
    
    
    
    private String getAddress(String BTAddress){
    	String result = "";
    	Pattern pattern = Pattern.compile("\\((.+?)\\)");
    	Matcher matcher = pattern.matcher(BTAddress);
    	if (matcher.find()){
    		result = matcher.group(1);
    	}
    	return result;
    }
    
    private void addItem(String device,String status, boolean profileBtn){
//    	Log.v(global.TAG, "DeviceListActivity:addItem:Start:"+device+" "+status);s
    	Map<String,Object> item = new HashMap<String,Object>();
    	item.put("device", device);
    	item.put("state", status);
    	item.put("profile_btn", profileBtn);
    	data.add(item);
    }
    
    private void savebondedAdd(String BTAddress){
    	SharedPreferences sp = getActivity().getSharedPreferences("bondedAddress", Context.MODE_PRIVATE);
    	Editor editor = sp.edit();
    	editor.putString("STRING_KEY",BTAddress);
    	editor.commit();
    	
    	SharedPreferences Lastsp = getActivity().getSharedPreferences("LastAddress",  getActivity().MODE_PRIVATE);
		String LastAddress = Lastsp.getString("STRING_KEY", "");
			 
		 if(LastAddress.equals(BTAddress) || LastAddress == ""){
//			 Log.v(global.TAG, "DeviceListFragment :The Same Pair Address");
		 }else{
				Intent clearintent = new Intent("android.intent.action.ClearPB");
				getActivity().sendBroadcast(clearintent);
		 }
    	
    	//锟斤拷锟斤拷锟较达拷匹锟斤拷锟斤拷只锟斤拷锟斤拷锟斤拷锟街�
		Lastsp = getActivity().getSharedPreferences("LastAddress", Context.MODE_PRIVATE);
		Editor Lasteditor = Lastsp.edit();
		Lasteditor.putString("STRING_KEY",BTAddress);
		Lasteditor.commit();
    	        
//        if (btAdapter.isDiscovering()){
//        	btAdapter.cancelDiscovery();
//        }
        
//        openProfilelist(BTAddress);
        
//		 Intent intent = new Intent();
//		 intent.putExtra("BTAddress", BTAddress);
//		 intent.setClass(getActivity(), AutoConnBtService.class);
//		 getActivity().startService(intent);
    }

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
//                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    Log.v(global.TAG, "Found Device:" + device.getName() +"  "+ device.getAddress());
                    //Ignore exist devices.
                    for (int i=0; i<data.size(); i++){
                        String addr = getAddress(data.get(i).get("state").toString());
//                        if (device.getAddress().equals(addr))
//                            return;
                    }
                    //Add item
//                    addItem(device.getName(),getActivity().getResources().getString(R.string.DL_PairWithThisDevice)+"("+device.getAddress()+")", false);
                    dlAdapter.notifyDataSetChanged();
            }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
//    	    	Log.v(global.TAG, "Search Finished");
    	    	m_listSerchingText.setText(" ");
    	  //  	getActivity().setTitle(getResources().getString(R.string.app_name));
            }else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
//            	Log.v(global.TAG, "ACTION_BOND_STATE_CHANGED Finished");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                refershState(device.getAddress());
                switch (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)){               			
                    case BluetoothDevice.BOND_BONDING:
                        break;
                    case BluetoothDevice.BOND_NONE:
                    	
//						Intent Broadintent = new Intent("android.intent.action.Nobondeddevice");
//						getActivity().sendBroadcast(Broadintent);
                    	
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        //if (device.getAddress().equals(selectedBTAddress))
                    	
       				 SharedPreferences sp = getActivity().getSharedPreferences("bondedAddress",  getActivity().MODE_PRIVATE);
       				 String bondedAddress = sp.getString("STRING_KEY", "");
       				 
//       				 if((bondedAddress == "" && bondedAddress!= null)
//       						 || bondedAddress.equals(device.getAddress())
//       						 || BluetoothDevice.BOND_NONE == global.bu.getBluetoothPairState(bondedAddress)){
//       					Log.d(global.TAG, "[DeviceListFragment] Just One bondedAddress");
//       					savebondedAdd(device.getAddress());
//       				 }else{
//       					Log.d(global.TAG, "[DeviceListFragment] Already Have One bondedAddress:"+bondedAddress);
//       					global.bu.unpair(device.getAddress());
////       					new UnpairMoreBondedDeviceThread(device.getAddress()).start();
//       				 }
                    	
                        break;
//                    case BluetoothDevice.BOND_SUCCESS:
//                        break;
                }
            }else if (action.equals(BluetoothDevice.ACTION_NAME_CHANGED)) { //Gary_debug
//                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
//                Log.v(global.TAG, "ACTION_NAME_CHANGED -- BTAddress:"+device.getAddress()+"name: "+name);
//                refreshName(device, name);
            }/*else if(action.equals(BluetoothA2dpSink.ACTION_A2DP_SINK_STATUS_CHANGED)) {
            	Log.v(global.TAG, "BLUETOOTH_A2DP_SINK_CONNECTION_ADDRESS");
        		 int state = intent.getIntExtra(
                 		BluetoothA2dpSink.EXTRA_A2DP_SINK_STATUS,
                         BluetoothUtils.BLUETOOTH_A2DP_SINK_DISCONNECTED);
                 String BTAddress = intent.getStringExtra(BluetoothA2dpSink.EXTRA_A2DP_SINK_ADDRESS);
     			 refershState(BTAddress);
        	}else if(action.equals(BluetoothHFP.ACTION_HFP_STATUS_CHANGED)) {
        		Log.v(global.TAG, "BLUETOOTH_HFP_CONNECTION_STATUS_CHANGED_HFP");
            	int state = intent.getIntExtra(BluetoothHFP.EXTRA_HFP_STATUS,BluetoothHFP.HFP_DISCONNECTED);
                String BTAddress = intent.getStringExtra(BluetoothHFP.EXTRA_HFP_DEVICE_ADDRESS);
                refershState(BTAddress);

            }else if(action.equals(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS_CHANGED)) {
            	 Log.v(global.TAG, "BLUETOOTH_PBAPC_CONNECTION_STATUS_CHANGEDPHONEBOOK");
            	 String BTAddress = intent.getStringExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_ADDRESS);
                 refershState(BTAddress);
            }else if(action.equals(BluetoothIntent.BLUETOOTH_AVRCP_CON_IND_ACTION)) {
            	 Log.v(global.TAG, "BLUETOOTH_AVRCP_CON_IND_ACTION AVRCP_CON");
            	  String BTAddress = intent.getStringExtra(BluetoothIntent.AVRCP_CON_ADDRESS);
                  refershState(BTAddress);
            }else if(action.equals(BluetoothIntent.BLUETOOTH_AVRCP_DIS_CON_IND_ACTION)){
            	Log.v(global.TAG, "BLUETOOTH_AVRCP_DIS_CON_IND_ACTION AVRCP_DIS" );
            	 String BTAddress = intent.getStringExtra(BluetoothIntent.AVRCP_CON_ADDRESS);
                 refershState(BTAddress);     
                
            }*/else if (action.equals(BluetoothDevice.ACTION_UUID)) {
//                Log.v(global.TAG, "BluetoothDevice.ACTION_UUID");
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    ParcelUuid[] pu = global.bu.mAdapter.getRemoteDevice(device.getAddress()).getUuids();
//                    for (int i = 0; i < pu.length; i++) {
//                        Log.v(global.TAG, "UUID -- " + pu[i].toString());
//                        CommonUtils.saveProfile2db(device.getAddress(), pu[i].toString());
//                    }
                } catch (Exception e) {
                    }                    
                }else if(action.equals("android.bluetooth.device.action.PAIRING_REQUEST")){
            }else if(action.equals("android.intent.action.ProfileHaveHFP")){
//          	  Log.v(global.TAG, "Have Receive android.intent.action.ProfileHaveHFP");
          	  ProfileHaveHFPFlag = true;
            }else if(action.equals("android.intent.action.ProfileHaveA2DP")){
//        	  Log.v(global.TAG, "Have Receive android.intent.action.ProfileHaveA2DP");
        	  ProfileHaveA2DPFlag = true;
            }else if(action.equals("android.intent.action.ProfileHaveAVRCP")){
//	      	  Log.v(global.TAG, "Have Receive android.intent.action.ProfileHaveAVRCP");
	      	  ProfileHaveAVRCPFlag = true;
            }
        }
    };
    
    public class UnpairMoreBondedDeviceThread extends Thread {
    	private String address="";
    	
    	public UnpairMoreBondedDeviceThread(String str) {
    		address = str;
    	}

    	@Override
    	public void run() {
//			CommonUtils.sleep(5000);
//			Log.d(global.TAG, "[DeviceListFragment] UnpairMoreBondedAdd:" + address);
//			global.bu.unpair(address);
    	}
    }
    
    OnItemClickListener listener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
//			Log.v(global.TAG,parent.getItemAtPosition(position).toString());
			
			//selectedItemPosition = position;
			selectedState = data.get(position).get("state").toString();
			selectedBTAddress = getAddress(selectedState);

			 if(data.get(position).get("device") != null){
				 selectedDeviceName = data.get(position).get("device").toString();
			 }else{
				 selectedDeviceName = getActivity().getResources().getString(R.string.pb_name_unknown);
			 }
			
			BTDialogFragment dialog;
			int state = checkState(selectedState);
			if(state == DIALOG_STATE_NO_PAIR){
				dialog = BTDialogFragment.newInstance(BTDialogFragment.DIALOG_NO_PAIR, selectedBTAddress,selectedDeviceName);
				dialog.show(getFragmentManager(), "");
			}else if(state == DIALOG_STATE_PAIRED_BUT_NOT_CONNECTED){
				dialog = BTDialogFragment.newInstance(BTDialogFragment.DIALOG_PAIRED_BUT_NOT_CONNECTED, selectedBTAddress,selectedDeviceName);
				dialog.show(getFragmentManager(), "");
			}else if (state == DIALOG_STATE_PAIRED_AND_CONNECTED
					|| state == DIALOG_STATE_CONNECTING
					|| state == DIALOG_STATE_PAIRED_AND_CONNECTING){
				dialog = BTDialogFragment.newInstance(BTDialogFragment.DIALOG_PAIRED_AND_CONNECTED, selectedBTAddress,selectedDeviceName);
				dialog.show(getFragmentManager(), "");
			}
		}
    };
    
    class DeviceListAdapter extends BaseAdapter{
    	private LayoutInflater mInflater;
    	private ArrayList<Map<String, Object>> data;
    	private Context context;
    	
        public DeviceListAdapter(Context context,
        		ArrayList<Map<String, Object>> data) {
    	    this.mInflater = LayoutInflater.from(context);
    	    this.data = data;
    	    this.context = context;
        }
    	
    	@Override
    	public int getCount() {
    		return data.size();
    	}

    	@Override
    	public Object getItem(int position) {
    		return data.get(position);
    	}

    	@Override
    	public long getItemId(int position) {
    		return position;
    	}
    	
        private String getAddress(String BTAddress){
        	String result = "";
        	Pattern pattern = Pattern.compile("\\((.+?)\\)");
        	Matcher matcher = pattern.matcher(BTAddress);
        	if (matcher.find()){
        		result = matcher.group(1);
        	}
        	return result;
        }

    	@Override
    	public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.devicelist_listview, null);
                   
            TextView device = (TextView) convertView.findViewById(R.id.listview_device);
            device.setText((String)data.get(position).get("device") );
    	    
            TextView state = (TextView) convertView.findViewById(R.id.listview_state);
            String temp =  (String)data.get(position).get("state");
            state.setText(temp.substring(0,temp.indexOf("(")) );
            
            final String BTAddress = getAddress((String)data.get(position).get("state"));
            ImageButton btn_profiles = (ImageButton)convertView.findViewById(R.id.btn_profiles);
            if ((Boolean)data.get(position).get("profile_btn")){
//            	btn_profiles.setVisibility(ImageButton.VISIBLE);
            	btn_profiles.setVisibility(ImageButton.INVISIBLE);
            }else{
            	btn_profiles.setVisibility(ImageButton.INVISIBLE);
            }
            btn_profiles.setOnClickListener(new View.OnClickListener() {
    			@Override
    			public void onClick(View v) {
//    				Log.e(global.TAG, "DeviceListAdapter:onClick");
//    				if (global.bu.getBluetoothPairState(BTAddress)!=BluetoothDevice.BOND_BONDED)
//    					return;
    				openProfilelist(BTAddress);
    			} 
    		});
    			
    	    return convertView;
    	}
    }
    
	public void openProfilelist(String BTAddress){
		Intent intent = new Intent();
		intent.putExtra("BTAddress", BTAddress);
//		intent.setClass(getActivity(), ProfileList.class);
		getActivity().startActivityForResult(intent, 0);
    }

}



