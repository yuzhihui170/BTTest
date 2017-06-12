/*
 * Copyright (C) Apical
 */
package com.example.btapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.csr.BTApp.AutoConnBtService.AutoConnBtThread;
//import com.csr.BTApp.apical.util.ClsUtils;
//import com.csr.BTApp.common.BluetoothUtils;
//import com.csr.BTApp.common.CommonUtils;
//import com.csr.BTApp.common.global;
//import com.csr.bluetooth.BluetoothIntent;

import android.app.Fragment;
//import android.bluetooth.BluetoothA2dpSink;
//import android.bluetooth.BluetoothAvrcpCtl;
//import android.bluetooth.BluetoothHFP;
//import android.bluetooth.BluetoothPhonebookClient;
//import android.bluetooth.BluetoothPhonebookClient.BluetoothPhonebookClientIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.btutil.ActionConstant;
import com.example.btutil.BTUtil;
import com.example.btutil.BtService;

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

	private BtService.MyBinder myBinder;
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
                myBinder.search();
                mHandler.sendEmptyMessage(0);
                dlAdapter.notifyDataSetChanged();
			}
		});
    	
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActionConstant.ACTION_FOUND_DEVICE);
        getActivity().registerReceiver(mReceiver, filter);
        
		myBinder = ((BluetoothActivity)getActivity()).getMyBinder();
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
		Log.d(TAG, "[DeviceListFragment] onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
        myBinder.stopSearch();
		Log.d(TAG, "[DeviceListFragment] onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "[DeviceListFragment] onStop");
	}
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	getActivity().unregisterReceiver(mReceiver);
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
    
    private void addPairedDevicesToListview() {
//            	"0cedd46659275takee 1"
//            	addItem(device.getName(),"("+device.getAddress()+")", true);
        addItem("takee 1","("+"0cedd46659275"+")", true);
//        refershState();
    }

    private String getAddress(String BTAddress) {
    	String result = "";
    	Pattern pattern = Pattern.compile("\\((.+?)\\)");
    	Matcher matcher = pattern.matcher(BTAddress);
    	if (matcher.find()) {
    		result = matcher.group(1);
    	}
    	return result;
    }
    
    private void addItem(String device, String status, boolean profileBtn){
    	Map<String, Object> item = new HashMap<String, Object>();
        item.put("device", device);
        item.put("state", status);
        item.put("profile_btn", profileBtn);
        data.add(item);
//    	Log.v(TAG, "DeviceListActivity:addItem:Start:"+device+" "+status);
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
    	
		Lastsp = getActivity().getSharedPreferences("LastAddress", Context.MODE_PRIVATE);
		Editor Lasteditor = Lastsp.edit();
		Lasteditor.putString("STRING_KEY",BTAddress);
		Lasteditor.commit();
    	        
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
            if (action.equals(ActionConstant.ACTION_FOUND_DEVICE)) {
                String deviceAddr = intent.getStringExtra("deviceAddr");
                String deviceName = intent.getStringExtra("deviceName");
                addItem(deviceName,"("+deviceAddr+")", true);
                dlAdapter.notifyDataSetChanged();
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
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
            //获取蓝牙的地址
			selectedState = data.get(position).get("state").toString();
			selectedBTAddress = getAddress(selectedState);

			 if(data.get(position).get("device") != null){
				 selectedDeviceName = data.get(position).get("device").toString();
			 }else{
				 selectedDeviceName = getActivity().getResources().getString(R.string.pb_name_unknown);
			 }
            myBinder.connect(selectedBTAddress);
			
//			BTDialogFragment dialog;
//			int state = checkState(selectedState);
//			if(state == DIALOG_STATE_NO_PAIR){
//				dialog = BTDialogFragment.newInstance(BTDialogFragment.DIALOG_NO_PAIR, selectedBTAddress,selectedDeviceName);
//				dialog.show(getFragmentManager(), "");
//			}else if(state == DIALOG_STATE_PAIRED_BUT_NOT_CONNECTED){
//				dialog = BTDialogFragment.newInstance(BTDialogFragment.DIALOG_PAIRED_BUT_NOT_CONNECTED, selectedBTAddress,selectedDeviceName);
//				dialog.show(getFragmentManager(), "");
//			}else if (state == DIALOG_STATE_PAIRED_AND_CONNECTED
//					|| state == DIALOG_STATE_CONNECTING
//					|| state == DIALOG_STATE_PAIRED_AND_CONNECTING){
//				dialog = BTDialogFragment.newInstance(BTDialogFragment.DIALOG_PAIRED_AND_CONNECTED, selectedBTAddress,selectedDeviceName);
//				dialog.show(getFragmentManager(), "");
//			}
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



