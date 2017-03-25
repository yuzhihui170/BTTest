/*
 * Copyright (C) Apical
 */
package com.example.btapp;

import java.text.SimpleDateFormat;
import java.util.Date;

//import com.csr.BTApp.PhonebookFragment.ReDownloadPBThread;
//import com.csr.BTApp.apical.util.PinyinUtils;
//import com.csr.BTApp.apical.util.ToastUtils;
//import com.csr.BTApp.common.CommonUtils;
//import com.csr.BTApp.common.global;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
//import android.bluetooth.BluetoothPbapClient;
//import android.bluetooth.BluetoothPhonebookClient;
//import android.bluetooth.BluetoothPhonebookClient.BluetoothPhonebookClientIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CallLogFragment extends Fragment {
	private ProgressDialog m_dialogLoading;
	private String m_deviceAddress;
	private ImageButton m_btnDownload;
	private ImageButton m_btnDelete;
	private ListView m_callLogLV;
	private TextView m_emptyNotice;
	private View m_callLogView;
	private CallLogCursorAdapter m_callLogAdapter;
	private CallLogLoaderListener m_loaderCallback = new CallLogLoaderListener();
//	BluetoothPhonebookClient mPhonebookClient = new BluetoothPhonebookClient();
	private static byte[]  lock= new byte[0];
	private boolean mShowCalllogFlag = false; 
	
	private ProgressDialog m_PBShowLoading;
	private boolean m_CreateLoaderFlag = false ;
	private boolean m_bRemoving=false;
	private boolean m_bRemoveThenDownload=false;
	private boolean m_bDownLoaded= false;
	
	private class CallLogCursorAdapter extends CursorAdapter {

		public CallLogCursorAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
		}

		
		public CallLogCursorAdapter(Context context, Cursor c,
				boolean autoRequery) {
			super(context, c, autoRequery);
			// TODO Auto-generated constructor stub
		}


		public CallLogCursorAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
			// TODO Auto-generated constructor stub
		}


		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			// TODO Auto-generated method stub
			if(arg2 == null)
				return;
			
			String number = arg2.getString(1);
			String name = arg2.getString(2);
			int type = arg2.getInt(3);
			String date = arg2.getString(4);
			
			ImageView TypeView = (ImageView)arg0.findViewById(R.id.calllog_type_pic);
			
			TextView nameCtrl = (TextView)arg0.findViewById(R.id.calllog_name);
			if(name == null){
				nameCtrl.setText(getActivity().getString(R.string.pb_name_unknown));
			}else{
				nameCtrl.setText(name);
			}
			
			TextView numberCtrl = (TextView)arg0.findViewById(R.id.calllog_number);
			numberCtrl.setText(number);
			
			Date date2 = new Date(Long.parseLong(date));
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String time = sfd.format(date2);
			TextView dateCtrl = (TextView)arg0.findViewById(R.id.calllog_date);
			
			String strType;
			switch(type) {
			case CallLog.Calls.INCOMING_TYPE:
				strType = getActivity().getResources().getString(R.string.call_log_dial_in);
				TypeView.setImageResource(R.drawable.calllog_incoming);
				break;
			case CallLog.Calls.MISSED_TYPE:
				strType = getActivity().getResources().getString(R.string.call_log_missed);
				TypeView.setImageResource(R.drawable.calllog_missed);
				break;
			case CallLog.Calls.OUTGOING_TYPE:
				strType = getActivity().getResources().getString(R.string.call_log_dial_out);
				TypeView.setImageResource(R.drawable.calllog_outcoming);
				break;
			default:
				strType = "";
				break;
			}
			dateCtrl.setText(time + " "+strType);
			
			ImageButton dialBtn = (ImageButton)arg0.findViewById(R.id.calllog_dial);
			dialBtn.setTag(number);
			dialBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
//					 Intent intent = new Intent(Intent.ACTION_CALL_PRIVILEGED,
//                             Uri.fromParts("tel", (String)arg0.getTag(), null));
//					 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					 getActivity().startActivity(intent);
				}
			});
//			Log.d(global.TAG, "[CallLogFragment]: bindView PhoneNum:"+number);
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			if(m_bRemoving){
//				Log.d(global.TAG, "[CallLogFragment]: newView m_bRemoving == ture");
				m_callLogAdapter.swapCursor(null);
			}
				
			LayoutInflater inflater = LayoutInflater.from(arg0);
//			Log.d(global.TAG, "[CallLogFragment]: newView");
			return inflater.inflate(R.layout.calllog_list_item, arg2, false);
		}
		
	}
	
	private class CallLogLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {

		private final String[] CALLLOG_PROJECTION = new String[]{
				CallLog.Calls._ID,
				CallLog.Calls.NUMBER,
				CallLog.Calls.CACHED_NAME,
				CallLog.Calls.TYPE, 
				CallLog.Calls.DATE,
		};

		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			// TODO Auto-generated method stub
			m_CreateLoaderFlag = true;
			CursorLoader cursor  =  new CursorLoader(getActivity(), CallLog.Calls.CONTENT_URI, 
					CALLLOG_PROJECTION, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
//			Log.d(global.TAG, "[CallLogFragment]: onCreateLoader");
			return cursor;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
			// TODO Auto-generated method stub
			
			m_CreateLoaderFlag = false;
			if(m_PBShowLoading != null){
				m_PBShowLoading.dismiss();
			}
			
			if(arg1.getCount() == 0){
				if(m_bRemoving) { 
//					Log.v(global.TAG, "[CallLogFragment]: Delete CallLog Over");
					m_bRemoving = false;
					if(!m_bRemoveThenDownload){
	                   	 if(m_dialogLoading != null){
	                		 m_dialogLoading.dismiss();
	                	 }
					}
				}
				m_callLogAdapter.swapCursor(null);
				return;
			}
			
			if(m_bDownLoaded){
				m_bDownLoaded = false;
			}
			m_callLogAdapter.swapCursor(arg1);
//			Log.d(global.TAG, "[CallLogFragment]: onLoadFinished swapCursor  " +arg1.getCount());
			
		//	int cnt = m_callLogLV.getCount();
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			// TODO Auto-generated method stub
			m_callLogAdapter.swapCursor(null);
//			Log.d(global.TAG, "[CallLogFragment]: onLoaderReset");
		}
		
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if(isVisibleToUser == true){
//			Log.d(global.TAG, "[CallLogFragment]: Show View");
			mShowCalllogFlag = true; 
			
			SharedPreferences sp = getActivity().getSharedPreferences("CalllogDownloading_Stats",  Context.MODE_PRIVATE);
    		if(sp.getBoolean("Calllog_DownloadingFlag", false)){
    	        m_dialogLoading = new ProgressDialog(getActivity());  
    	        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//锟斤拷锟矫凤拷锟轿诧拷谓锟斤拷锟斤拷锟�  
    	        m_dialogLoading.setTitle(R.string.main_callrecord);//锟斤拷锟矫憋拷锟斤拷  
    	        m_dialogLoading.setIcon(R.drawable.download_pb_icon);//锟斤拷锟斤拷图锟斤拷  
    	        m_dialogLoading.setMessage(getActivity().getResources().getString(R.string.phonebook_dlcalllog));  
    	        m_dialogLoading.setCancelable(false);
                m_dialogLoading.show();  
                
                setProgressDialogText(m_dialogLoading);
    		}else if(m_CreateLoaderFlag){
				m_PBShowLoading = new ProgressDialog(getActivity());  
				m_PBShowLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//锟斤拷锟矫凤拷锟轿诧拷谓锟斤拷锟斤拷锟�  
				m_PBShowLoading.setTitle(R.string.main_callrecord);//锟斤拷锟矫憋拷锟斤拷  
				m_PBShowLoading.setIcon(R.drawable.loadingcalllog);//锟斤拷锟斤拷图锟斤拷  
				m_PBShowLoading.setMessage(getActivity().getResources().getString(R.string.calllog_loading));  
				m_PBShowLoading.setCancelable(false);
				m_PBShowLoading.show();  
	            setProgressDialogText(m_PBShowLoading);
			}
			
		}else{
//			ToastUtils.cancelCurrentToast();
//			Log.d(global.TAG, "[CallLogFragment]: Hide View");
			mShowCalllogFlag = false; 
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		m_callLogView = inflater.inflate(R.layout.calllog_fragment,  (ViewGroup)getActivity().findViewById(R.id.tabpage_frame), false);
		m_callLogLV = (ListView)m_callLogView.findViewById(R.id.contact_tile_list);
		m_callLogLV.setVerticalScrollBarEnabled(true);
		
		m_callLogAdapter = new CallLogCursorAdapter(getActivity(), null);
		m_callLogLV.setAdapter(m_callLogAdapter);
		
		m_emptyNotice = (TextView)m_callLogView.findViewById(R.id.list_empty_text);
	//	m_callLogLV.setEmptyView(m_emptyNotice);
		
		m_btnDownload = (ImageButton)m_callLogView.findViewById(R.id.calllog_download);
		m_btnDownload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				downloadCallLog();
			}
		});
		
		m_btnDelete = (ImageButton)m_callLogView.findViewById(R.id.calllog_delete);
		m_btnDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(m_callLogAdapter.getCount() !=0 ) {
					deleteCallLog();
//					Log.d(global.TAG, "[PhonebookFragment]:Enter m_btnDelete.onClick()");
				}
			}
		});
		
//    	IntentFilter intentFilter = new IntentFilter();
//    	intentFilter.addAction(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS_CHANGED);
//    	intentFilter.addAction("android.intent.action.ClearPB");
//    	intentFilter.addAction("android.intent.action.ClearLogs");
//    	getActivity().registerReceiver(mReceiver, intentFilter);
		
		initCallLogLoader();
	}
	
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			/*if(action.equals(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS_CHANGED)){
				Log.v(global.TAG, "[CallLogFragment]:BLUETOOTH_PBAPC_CONNECTION_STATUS_CHANGEDPHONEBOOK -- BTAddress:");
				 int state = arg1.getIntExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS, 0);
				 if(state == BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_COMMUNICATING){//锟斤拷锟斤拷锟斤拷锟斤拷通話锟斤拷录
                	 if(mShowCalllogFlag){
                		Log.v(global.TAG, "[CallLogFragment]: Downloading CallLog");
                     	SharedPreferences sp = arg0.getSharedPreferences("CalllogDownloading_Stats", Context.MODE_PRIVATE);
                    	Editor editor = sp.edit();
                    	editor.putBoolean("Calllog_DownloadingFlag", true);
                    	editor.commit();
                	 }
				 }else if(state == BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_CONNECTED){//锟斤拷锟斤拷通話锟斤拷录锟斤拷锟�
                	 if(mShowCalllogFlag){
                    	 if(m_dialogLoading != null){
                    		 m_dialogLoading.dismiss();
                    	 }
                    	 m_bDownLoaded=true;
                    	 m_bRemoveThenDownload = false;
                	 }
                	 
				 }else if(state == BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_DISCONNECTED){
                	 if(mShowCalllogFlag){
                		Log.v(global.TAG, "[CallLogFragment]: BLUETOOTH_PHONEBOOK_DISCONNECTED");
     	            	SharedPreferences sp = arg0.getSharedPreferences("CalllogDownloading_Stats",  Context.MODE_PRIVATE);
    	        		if(sp.getBoolean("Calllog_DownloadingFlag", false)){
    	        			
                         	Editor editor = sp.edit();
                         	editor.putBoolean("Calllog_DownloadingFlag", false);
                         	editor.commit();
    	        			
	                		if(m_dialogLoading != null){
	                			m_dialogLoading.dismiss();
	                		}
	                		if(getActivity() != null){
	                			ToastUtils.showMessage(getActivity(), getActivity().getResources().getString(R.string.str_no_pbap));
	                		}
    	        		}
                	 }
				 }
			}else */if(action.equals("android.intent.action.ClearPB")){
//				Log.v(global.TAG, "[CallLogFragment]: android.intent.action.ClearCallLog");
				
				m_bRemoving = true;
//		    	Intent clearPhonebookIntent = new Intent();
//		        clearPhonebookIntent.setClass(getActivity(), BluetoothPbapcClearCallLogService.class);
//		        getActivity().startService(clearPhonebookIntent);
			}
			else if(action.equals("android.intent.action.ClearLogs")){
//				Log.v(global.TAG, "[CallLogFragment]: android.intent.action.ClearLogs");
				if(m_callLogAdapter.getCount() !=0 ) {
					m_bRemoving = true;
//			    	Intent clearPhonebookIntent = new Intent();
//			        clearPhonebookIntent.setClass(getActivity(), BluetoothPbapcClearCallLogService.class);
//			        getActivity().startService(clearPhonebookIntent);
			        
			        m_callLogAdapter.swapCursor(null);
				}
		        
			}
		}
    	
    };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return m_callLogView;
	}

	@Override
	public void onPause() {
//		Log.v(global.TAG, "[CallLogFragment]:onPause()");
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStart() {
//		Log.v(global.TAG, "[CallLogFragment]:onStart()");
		// TODO Auto-generated method stub
		super.onStart();
	//	initCallLogLoader();
	}
	
	
	@Override
	public void onResume() {
//		Log.v(global.TAG, "[CallLogFragment]:onResume()");
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		Log.d(global.TAG, "[CallLogFragment]:onDestroy");
		m_bRemoving=false;
		if(m_dialogLoading != null){
			m_dialogLoading.dismiss();
		}
    	if(getActivity() != null){
//    		getActivity().unregisterReceiver(mReceiver);
    	}
	}

	
	private void initCallLogLoader() {
		getLoaderManager().initLoader(0, null, m_loaderCallback);
	}
	
	public void deleteCallLog() {
		AlertDialog  deleteDialog = new AlertDialog.Builder(getActivity()).
		setTitle(R.string.ready_delete_calllog).
		setIcon(R.drawable.delete_pb_icon).
		setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
		        m_dialogLoading = new ProgressDialog(getActivity());  
		        
		        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//锟斤拷锟矫凤拷锟轿诧拷谓锟斤拷锟斤拷锟�  
		        m_dialogLoading.setTitle(R.string.main_callrecord);//锟斤拷锟矫憋拷锟斤拷  
		        m_dialogLoading.setIcon(R.drawable.delete_pb_icon);//锟斤拷锟斤拷图锟斤拷  
		        m_dialogLoading.setMessage(getActivity().getResources().getString(R.string.calllog_clean));  
		        m_dialogLoading.setCancelable(false);
                m_dialogLoading.show(); 
                
                setProgressDialogText(m_dialogLoading);
                
		    	m_bRemoving = true;
//		    	Intent clearPhonebookIntent = new Intent();
//		        clearPhonebookIntent.setClass(getActivity(), BluetoothPbapcClearCallLogService.class);
//		        getActivity().startService(clearPhonebookIntent);
		        m_callLogAdapter.swapCursor(null);
			}
		}).
		setNegativeButton(android.R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		}).create();
		deleteDialog.show();
	}
	
	public void downloadCallLog() {
//    	m_deviceAddress = global.ConnectedDevices.get("PBAP");
//    	int currentStatus = mPhonebookClient.getConnectionStatus(m_deviceAddress);
//    	if (currentStatus != BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_DISCONNECTED) {
//    		
//    		if(m_callLogAdapter.getCount() !=0) {
//    			AlertDialog  DownloadDialog = new AlertDialog.Builder(getActivity()).
//    					setTitle(R.string.ready_download_calllog).
//    					setIcon(R.drawable.download_pb_icon).
//    					setPositiveButton(android.R.string.ok, new OnClickListener() {
//    						
//    						@Override
//    						public void onClick(DialogInterface arg0, int arg1) {
//    							// TODO Auto-generated method stub
//    			    	        m_dialogLoading = new ProgressDialog(getActivity());  
//    			    	        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//锟斤拷锟矫凤拷锟轿诧拷谓锟斤拷锟斤拷锟�  
//    			    	        m_dialogLoading.setTitle(R.string.main_callrecord);//锟斤拷锟矫憋拷锟斤拷  
//    			    	        m_dialogLoading.setIcon(R.drawable.download_pb_icon);//锟斤拷锟斤拷图锟斤拷  
//    			    	        m_dialogLoading.setMessage(getActivity().getResources().getString(R.string.phonebook_dlcalllog));  
//    			    	        m_dialogLoading.setCancelable(false);
//    			                m_dialogLoading.show();  
//    			                
//    			                setProgressDialogText(m_dialogLoading);
//    			                
//    	                     	SharedPreferences sp = getActivity().getSharedPreferences("CalllogDownloading_Stats", Context.MODE_PRIVATE);
//    	                    	Editor editor = sp.edit();
//    	                    	editor.putBoolean("Calllog_DownloadingFlag", true);
//    	                    	editor.commit();
//    			        		
//    			                //锟饺刪锟斤拷通話記锟�
//    					    	m_bRemoving = true;
//    					    	m_bRemoveThenDownload = true;
//    					    	Intent clearPhonebookIntent = new Intent();
//    					        clearPhonebookIntent.setClass(getActivity(), BluetoothPbapcClearCallLogService.class);
//    					        getActivity().startService(clearPhonebookIntent);
//    					        m_callLogAdapter.swapCursor(null);
//    					        
//    					        new ReDownloadCallLogThread().start();
//    						}
//    					}).
//    					setNegativeButton(android.R.string.cancel, new OnClickListener() {
//    						
//    						@Override
//    						public void onClick(DialogInterface arg0, int arg1) {
//    							// TODO Auto-generated method stub
//    							
//    						}
//    					}).create();
//    					DownloadDialog.show();
//    		}else{
//    	        m_dialogLoading = new ProgressDialog(getActivity());  
//    	        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//锟斤拷锟矫凤拷锟轿诧拷谓锟斤拷锟斤拷锟�  
//    	        m_dialogLoading.setTitle(R.string.main_callrecord);//锟斤拷锟矫憋拷锟斤拷  
//    	        m_dialogLoading.setIcon(R.drawable.download_pb_icon);//锟斤拷锟斤拷图锟斤拷  
//    	        m_dialogLoading.setMessage(getActivity().getResources().getString(R.string.phonebook_dlcalllog));  
//    	        m_dialogLoading.setCancelable(false);
//                m_dialogLoading.show();  
//                
//                setProgressDialogText(m_dialogLoading);
//        		
//        		global.bu.pbapDownloadIO(m_deviceAddress);
//    		}
//    	}else{
//    		ToastUtils.showMessage(getActivity(), getActivity().getResources().getString(R.string.str_no_pbap));
//    	}
	}
	
    public class ReDownloadCallLogThread extends Thread {

    	@Override
	    	public void run() {
	    		while (true){
	    			synchronized (lock) {
//	    				if(!m_bRemoving){
//	    			    	m_deviceAddress = global.ConnectedDevices.get("PBAP");
//	    			    	int currentStatus = mPhonebookClient.getConnectionStatus(m_deviceAddress);
//	    			    	if (currentStatus != BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_DISCONNECTED) {
//	    			    		global.bu.pbapDownloadIO(m_deviceAddress);
//	    			    	}else{
//	    		        		if(m_dialogLoading != null){
//	    		        			m_dialogLoading.dismiss();
//	    		        		}
//	    			    		ToastUtils.showMessage(getActivity(), getActivity().getResources().getString(R.string.str_no_pbap));
//	    			    	}
//	    					
//	    					break;
//	    				}
//	    				Log.d(global.TAG, "[CallLogFragment]: Wait ReDownloadCallLogThread");
//	    				CommonUtils.sleep(1000);
	    		}
	    	}
	    }
    }
	
    public void setProgressDialogText(ProgressDialog mprogressDialog)
    {
       View v = mprogressDialog.getWindow().getDecorView();   //取锟斤拷dialog锟斤拷锟斤拷锟斤拷view
        setDialogText(v);   //锟斤拷view锟斤拷锟斤拷锟斤拷锟斤拷
    }

  public void setDialogText(View v) { 
       if (v instanceof ViewGroup) { 
         ViewGroup parent = (ViewGroup) v; 
         int count = parent.getChildCount(); 
         for (int i = 0; i < count; i++) { 
           View child = parent.getChildAt(i); 
           setDialogText(child); 
         } 
       } else if (v instanceof TextView) { 
         ((TextView) v).setTextSize(25); //锟斤拷锟斤拷锟斤拷锟叫碉拷view锟斤拷锟揭碉拷textview锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟叫�
       } 
     }
}
