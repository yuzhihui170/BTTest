
/*
 * Copyright (C) Apical
 */
package com.example.btapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

//import com.csr.BTApp.AutoConnBtService.AutoConnBtThread;
//import com.csr.BTApp.DeviceListFragment.DeviceListAdapter;
//import com.csr.BTApp.SortCursor.SortEntry;
//import com.csr.BTApp.apical.util.AlphabetScrollBar;
//import com.csr.BTApp.apical.util.PinyinUtils;
//import com.csr.BTApp.apical.util.ToastUtils;
//import com.csr.BTApp.common.CommonUtils;
//import com.csr.BTApp.common.global;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.btutil.BtService;


public class PhonebookFragment extends Fragment{
	private final static String TAG = "yzh";
    private static final int H_GET_BINDER = 0x10;

	//private TextView text_pbap_state;
	private static byte[]  lock= new byte[0];
	private EditText m_editSearchPB;
	private String m_deviceAddress;
	private ListView m_listView;
	private TextView m_listEmptyText;
	private View m_pbView;
//	private AlphabetScrollBar m_scrollBar;
	private TextView m_letterNotice;
	private ImageButton m_btnDownload, m_btnDelete;
	private ProgressDialog m_dialogLoading;
	private ProgressDialog m_PBShowLoading;
	private boolean m_CreateLoaderFlag = false ;
	private boolean m_bRemoving=false;
	private boolean m_bRemoveThenDownload=false;
	private boolean m_bDownLoaded= false;
	private boolean mShowPbFlag = false; 
	
//	private ArrayList<SortEntry> mFilterList;
//	private PbFilterAdapter PbFAdapter;
	private PBCursorAdapter m_listAdapter;
	PBLoaderListener m_pbLoader = new PBLoaderListener();
//	BluetoothPhonebookClient mPhonebookClient = new BluetoothPhonebookClient();

	private BtService.MyBinder myBinder;
	
    private static final String[] FROM = new String[] {
    	   CommonDataKinds.Phone.DISPLAY_NAME,
           CommonDataKinds.Phone.NUMBER,
    };
    private static final int[] TO = new int[] {
        android.R.id.text1,
        android.R.id.text2
    };
    
    @Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		
		if(isVisibleToUser == true){
//			Log.d(global.TAG, "[PhonebookFragment]: Show View");
			mShowPbFlag = true;
			
         	SharedPreferences sp = getActivity().getSharedPreferences("PBDownloading_Stats",  Context.MODE_PRIVATE);
    		if(sp.getBoolean("PB_DownloadingFlag", false)){
    	        m_dialogLoading = new ProgressDialog(getActivity());  
    	        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//锟斤拷锟矫凤拷锟轿诧拷谓锟斤拷锟斤拷锟�  
    	        m_dialogLoading.setTitle(R.string.main_Phonebook);//锟斤拷锟矫憋拷锟斤拷  
    	        m_dialogLoading.setIcon(R.drawable.download_pb_icon);//锟斤拷锟斤拷图锟斤拷  
    	        m_dialogLoading.setMessage(getActivity().getResources().getString(R.string.phonebook_dlcontacts));  
    	        m_dialogLoading.setCancelable(false);
                m_dialogLoading.show();  
                
//                setProgressDialogText(m_dialogLoading);
    		}else if(m_CreateLoaderFlag){
				m_PBShowLoading = new ProgressDialog(getActivity());  
				m_PBShowLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//锟斤拷锟矫凤拷锟轿诧拷谓锟斤拷锟斤拷锟�  
				m_PBShowLoading.setTitle(R.string.main_Phonebook);//锟斤拷锟矫憋拷锟斤拷  
				m_PBShowLoading.setIcon(R.drawable.loadingphonebook);//锟斤拷锟斤拷图锟斤拷  
				m_PBShowLoading.setMessage(getActivity().getResources().getString(R.string.phonebook_loading));  
				m_PBShowLoading.setCancelable(false);
				m_PBShowLoading.show();  
//	            setProgressDialogText(m_PBShowLoading);
			}

		}else{
//			ToastUtils.cancelCurrentToast();
//			Log.d(global.TAG, "[PhonebookFragment]: Hide View");
			mShowPbFlag = false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
    	Log.v(TAG, "[PhonebookFragment]: onCreateView");

		return m_pbView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(getActivity()) ;
		m_pbView = inflater.inflate(R.layout.phonebook_fragment, (ViewGroup)getActivity().findViewById(R.id.tabpage_frame),false);
//    	IntentFilter intentFilter = new IntentFilter();
//    	intentFilter.addAction(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS_CHANGED);
//    	intentFilter.addAction(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_MSG_RECEIVED);
//    	intentFilter.addAction("android.intent.action.ClearPB");
//    	intentFilter.addAction("android.intent.action.ClearLogs");
//    	getActivity().registerReceiver(mReceiver, intentFilter);

		m_dialogLoading = new ProgressDialog(getActivity());

		initUIItems();
		initPBLoader();

        mHandler.sendEmptyMessageDelayed(H_GET_BINDER, 2000);
		Log.v(TAG, "[PhonebookFragment]:onCreate");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.v(TAG, "[PhonebookFragment]:onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.v(TAG, "[PhonebookFragment]:onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "[PhonebookFragment]:onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.v(TAG, "[PhonebookFragment]:onStop");
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		m_bRemoving=false;
		if(m_dialogLoading != null){
			m_dialogLoading.dismiss();
		}
		if(getActivity() != null){
//    		getActivity().unregisterReceiver(mReceiver);
		}
    	Log.v(TAG, "[PhonebookFragment]:onDestroy");
	}

	private class PBLoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
    
	    private final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
	    		CommonDataKinds.Phone._ID,
	    		CommonDataKinds.Phone.DISPLAY_NAME,
	    		CommonDataKinds.Phone.NUMBER,
	    };
	    
		@Override
		public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
	//		return new CursorLoader(getActivity(), CommonDataKinds.Phone.CONTENT_URI,
	//				CONTACTS_SUMMARY_PROJECTION, null, null, null);
//			Log.d(global.TAG, "[PhonebookFragment]: PBLoaderListener:onCreateLoader ");
//			m_CreateLoaderFlag = true;
//			return new SortCursorLoader(getActivity(), CommonDataKinds.Phone.CONTENT_URI,
//							CONTACTS_SUMMARY_PROJECTION, null, null, null);
			return null;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {

			m_CreateLoaderFlag = false;
			if(m_PBShowLoading != null){
				m_PBShowLoading.dismiss();
			}
			
			if(arg1.getCount() == 0){
				if(m_bRemoving) { 
//					Log.v(global.TAG, "[PhonebookFragment]: Delete PB Over");
					m_bRemoving = false;
					if(!m_bRemoveThenDownload){
		        		if(m_dialogLoading != null){
		        			m_dialogLoading.dismiss();
		        		}
					}
					
				}
				m_listAdapter.swapCursor(null);
				return;
			}
			
			if(m_bDownLoaded){
				m_bDownLoaded = false;
			}
			m_listAdapter.swapCursor(arg1);
//			Log.d(global.TAG, "[PhonebookFragment]: PBLoaderListener:onLoadFinished  swapCursor  " +arg1.getCount());
		}

		@Override
		public void onLoaderReset(Loader<Cursor> arg0) {
			m_listAdapter.swapCursor(null);
//			Log.d(global.TAG, "[PhonebookFragment]: PBLoaderListener:onLoaderReset ");
		}
		
	}
	
	private class PBCursorAdapter extends CursorAdapter {

		LayoutInflater m_inflater;
		String mPreFirstLetter = "";
		int viewPos = -1;
		
		public PBCursorAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			m_inflater = LayoutInflater.from(context);
		}

		public PBCursorAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
			m_inflater = LayoutInflater.from(context);
		}
		
		public PBCursorAdapter(Context context, Cursor c) {
			super(context, c);
			m_inflater = LayoutInflater.from(context);
		}

		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			viewPos = arg0;
			return super.getView(arg0, arg1, arg2);
		}

		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			if(arg2 == null)
			{
				return;
			}
				
			TextView nameCtrl = (TextView)arg0.findViewById(R.id.pb_name);
			int idx_col_name = arg2.getColumnIndex( CommonDataKinds.Phone.DISPLAY_NAME);
			String strName = arg2.getString(idx_col_name);
			nameCtrl.setText(strName);
			
			TextView numberCtrl = (TextView)arg0.findViewById(R.id.pb_number);
			int idx_col_num = arg2.getColumnIndex(CommonDataKinds.Phone.NUMBER);
			String strNumber = arg2.getString(idx_col_num);
			numberCtrl.setText(strNumber);
			
			ImageButton dialBtn = (ImageButton)arg0.findViewById(R.id.pb_dial_btn);
			dialBtn.setTag(strNumber);
			dialBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
//					 Intent intent = new Intent(Intent.ACTION_CALL_PRIVILEGED,
//                             Uri.fromParts("tel", (String)arg0.getTag(), null));
//					 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					 getActivity().startActivity(intent);
				}
			});
			
			TextView letterTag = (TextView)arg0.findViewById(R.id.pb_item_LetterTag);
//			String firstLetter = PinyinUtils.getPingYin(strName.trim().substring(0, 1));
//			firstLetter = firstLetter.substring(0,1).toUpperCase();
			
			arg2.moveToPrevious();
			String strNamePre = arg2.getString(idx_col_name);
//			String firstLetterPre = PinyinUtils.getPingYin(strNamePre.trim().substring(0,1));
//			firstLetterPre = firstLetterPre.substring(0, 1).toUpperCase();
			//锟饺斤拷一锟斤拷锟斤拷锟斤拷锟角凤拷锟斤拷同
//			if(firstLetter.equals(firstLetterPre)){
//				letterTag.setVisibility(View.GONE);
//			}
//			else {
//				letterTag.setVisibility(View.VISIBLE);
//				letterTag.setText(firstLetter);
//			}
//			if(viewPos == 0) { //锟斤拷0锟斤拷Item锟斤拷letterTag始锟斤拷要锟斤拷示
//				letterTag.setVisibility(View.VISIBLE);
//				letterTag.setText(firstLetter);
//			}
//			Log.d(global.TAG, "[PhonebookFragment]: bindView"+strNumber+" Namelength:"+ strName.length()
//					+ "FisrtSpell:"+ PinyinUtils.getPingYin(strName));
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {

			if(m_bRemoving){
//			Log.d(global.TAG, "[PhonebookFragment]: newView m_bRemoving = ture");
			m_listAdapter.swapCursor(null);
		}
			
//			Log.d(global.TAG, "[PhonebookFragment]: newView");
			return m_inflater.inflate(R.layout.pb_list_item, arg2,false);
		}
		
	}
	
	//ScrollBar锟侥硷拷锟斤拷锟斤拷
//	private class ScrollBarListener implements AlphabetScrollBar.OnTouchBarListener {
//
//		Runnable r = new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				m_letterNotice.setVisibility(View.INVISIBLE);
//			}
//			
//		};
//		Handler handler = new Handler();
//		@Override
//		public void onTouch(String alphabet) {
//			// TODO Auto-generated method stub
//			m_letterNotice.setText(alphabet);
//			m_letterNotice.setVisibility(View.VISIBLE);
//			handler.removeCallbacks(r);
//			handler.postDelayed(r, 1500);
//			
//			Log.d(global.TAG, "[PhonebookFragment]: ScrollBarListener:onTouch");
//			SortCursor data = (SortCursor)m_listAdapter.getCursor();
//			if(data != null) {
//				int idx = data.binarySearch(alphabet);
//				if(idx != -1){
//					m_listView.setSelection(idx);
//				}else{
//				}
//					
//			}
//		}
//	}
	


	//public void 
    public void initPBLoader() {
    	 getLoaderManager().initLoader(0, null, m_pbLoader);
//    	 Log.d(global.TAG, "[PhonebookFragment]: initPBLoader ");
    }

    private void initUIItems() {
    	m_listView = (ListView)m_pbView.findViewById(R.id.pb_listvew);
    	m_listView.setVerticalScrollBarEnabled(true);
    	m_listAdapter = new PBCursorAdapter(getActivity(), null);
    	m_listView.setAdapter(m_listAdapter);
    	
    	m_listEmptyText = (TextView)m_pbView.findViewById(R.id.pb_nocontacts_notice);
    	
    	m_editSearchPB = (EditText)m_pbView.findViewById(R.id.pb_search_edit);
//    	m_editSearchPB.setVisibility(View.INVISIBLE);
    	m_editSearchPB.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				if ((m_editSearchPB.getText() != null)
//						&& !("".equals(m_editSearchPB.getText().toString().trim()))) {  
//					Log.d(global.TAG, "[PhonebookFragment]:Have Filter Search");
//					SortCursor data = (SortCursor)m_listAdapter.getCursor();
//					if(data != null) {
//						String input_info = m_editSearchPB.getText().toString();
//						mFilterList = new ArrayList<SortEntry>();
////						long TestTimestamp;
////						TestTimestamp = System.currentTimeMillis();
//						mFilterList = data.FilterSearch(input_info);
////						Log.v(global.TAG, "onTextChanged"+(System.currentTimeMillis() - TestTimestamp) );
////						if(!mFilterList.isEmpty()){
////							Log.d(global.TAG, "[PhonebookFragment]:Have Found Contacts");
////							PbFAdapter = new PbFilterAdapter(getActivity(), mFilterList);
////					    	m_listView.setAdapter(PbFAdapter);
////						}else{
////							Log.d(global.TAG, "[PhonebookFragment]:No Found Contacts");
////							PbFAdapter = new PbFilterAdapter(getActivity(), mFilterList);
////					    	m_listView.setAdapter(PbFAdapter);
////					    	m_listView.setEmptyView(m_listEmptyText);
////						}
//				        
//				    	m_scrollBar.setVisibility(View.INVISIBLE);
//					}
//	            } else{
//	            	Log.d(global.TAG, "[PhonebookFragment]:No Filter Search");
//	            	m_listView.setAdapter(m_listAdapter);
//	            	m_scrollBar.setVisibility(View.VISIBLE);
//	            }
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
    	
    	m_letterNotice = (TextView)m_pbView.findViewById(R.id.pb_letter_notice);
//    	m_scrollBar = (AlphabetScrollBar)m_pbView.findViewById(R.id.pb_scrollbar);
//    	m_scrollBar.setOnTouchBarListener(new ScrollBarListener());
    	//下载电话本按钮
    	m_btnDownload = (ImageButton)m_pbView.findViewById(R.id.pb_download_contact);
    	m_btnDelete = (ImageButton)m_pbView.findViewById(R.id.pb_delete_contact);
    	
    	m_btnDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				m_listAdapter.swapCursor(null);
				downloadPB();
			}
		});
    	
    	m_btnDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(m_listAdapter.getCount() !=0 ) {
//					deletePB();
//					Log.d(global.TAG, "[PhonebookFragment]:Enter m_btnDelete.onClick()");
				}
			}
		});
    }
    
    private void downloadPB() {
		myBinder.readContact();
    }
    
//    public class ReDownloadPBThread extends Thread {
//
//    	@Override
//	    	public void run() {
//	    		while (true){
//	    			synchronized (lock) {
//	    				if(!m_bRemoving){
//	    			    	m_deviceAddress = global.ConnectedDevices.get("PBAP");
//	    			    	int currentStatus = mPhonebookClient.getConnectionStatus(m_deviceAddress);
//	    			    	if (currentStatus != BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_DISCONNECTED) {
//	    			    		Log.d(global.TAG, "[PhonebookFragment]: Begin ReDownloadPBThread");
//		    					global.bu.pbapDownloadPB(m_deviceAddress);
//	    			    	}else{
//	    		        		if(m_dialogLoading != null){
//	    		        			m_dialogLoading.dismiss();
//	    		        		}
//	    			    		ToastUtils.showMessage(getActivity(), getActivity().getResources().getString(R.string.str_no_pbap));
//	    			    	}
//	    					
//	    					break;
//	    				}
//	    				Log.d(global.TAG, "[PhonebookFragment]: Wait ReDownloadPBThread");
//	    				CommonUtils.sleep(1000);
//	    		}
//	    	}
//	    		Log.d(global.TAG, "[PhonebookFragment]: Quit ReDownloadPBThread");
//	    }
//    }
    
    class DownloadPBTask extends AsyncTask<String,Integer,String>{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
//			Log.d(global.TAG, "[AsyncTask]: doInBackground");
//			global.bu.pbapDownloadPB(m_deviceAddress);
			return "AsyncTask Download Over";
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
//			Log.d(global.TAG, "[AsyncTask]: onPreExecute");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
//			Log.d(global.TAG, "[AsyncTask]: onProgressUpdate");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
//			Log.d(global.TAG, "[AsyncTask]: onPostExecute:" + result);
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
//			Log.d(global.TAG, "[AsyncTask]: onCancelled");
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
    
    public void deletePB() {
		AlertDialog  deleteDialog = new AlertDialog.Builder(getActivity()).
		setTitle(R.string.ready_delete_phonebook).
		setIcon(R.drawable.delete_pb_icon).
		setPositiveButton(android.R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
		        m_dialogLoading = new ProgressDialog(getActivity());  
		        
		        m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);//锟斤拷锟矫凤拷锟轿诧拷谓锟斤拷锟斤拷锟�  
		        m_dialogLoading.setTitle(R.string.main_Phonebook);//锟斤拷锟矫憋拷锟斤拷  
		        m_dialogLoading.setIcon(R.drawable.delete_pb_icon);//锟斤拷锟斤拷图锟斤拷  
		        m_dialogLoading.setMessage(getActivity().getResources().getString(R.string.phonebook_clean));  
		        m_dialogLoading.setCancelable(false);
                m_dialogLoading.show(); 
                
                setProgressDialogText(m_dialogLoading);
                
		    	m_bRemoving = true;
//		    	Intent clearPhonebookIntent = new Intent();
//		        clearPhonebookIntent.setClass(getActivity(), BluetoothPbapcClearPhonebookService.class);
//		        getActivity().startService(clearPhonebookIntent);
		        
		        m_listAdapter.swapCursor(null);
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
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			String action = arg1.getAction();
			/*if(action.equals(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS_CHANGED)){
				Log.v(global.TAG, "[PhonebookFragment]:BLUETOOTH_PBAPC_CONNECTION_STATUS_CHANGEDPHONEBOOK -- BTAddress:");
				 int state = arg1.getIntExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_STATUS, 0);
				 if(state == BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_COMMUNICATING){//锟斤拷锟斤拷锟斤拷锟截电话锟斤拷
                	 if(mShowPbFlag){
                		 Log.v(global.TAG, "[PhonebookFragment]:BLUETOOTH_PBAPC_COMMUNICATING: Downloading PhoneBook");
                     	SharedPreferences sp = arg0.getSharedPreferences("PBDownloading_Stats", Context.MODE_PRIVATE);
                    	Editor editor = sp.edit();
                    	editor.putBoolean("PB_DownloadingFlag", true);
                    	editor.commit();
                	 }
				 }else if(state == BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_CONNECTED){//锟斤拷锟截电话锟斤拷锟斤拷锟�
                	 if(mShowPbFlag){
                		 if(m_dialogLoading != null){
                    		 m_dialogLoading.dismiss();
                    	 }
                    	 m_bDownLoaded=true;
                    	 m_bRemoveThenDownload = false;
                    	 
                	 }
				 }else if(state == BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_DISCONNECTED){
                	 if(mShowPbFlag){
                		 Log.v(global.TAG, "[PhonebookFragment]: BLUETOOTH_PHONEBOOK_DISCONNECTED");
     	            	SharedPreferences sp = arg0.getSharedPreferences("PBDownloading_Stats",  Context.MODE_PRIVATE);
    	        		if(sp.getBoolean("PB_DownloadingFlag", false)){
    	        			
                         	Editor editor = sp.edit();
                         	editor.putBoolean("PB_DownloadingFlag", false);
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
//				Log.v(global.TAG, "[PhonebookFragment]: android.intent.action.ClearPB");
				
				m_bRemoving = true;
//		    	Intent clearPhonebookIntent = new Intent();
//		        clearPhonebookIntent.setClass(getActivity(), BluetoothPbapcClearPhonebookService.class);
//		        getActivity().startService(clearPhonebookIntent);
		        
			}
			else if(action.equals("android.intent.action.ClearLogs")){
//				Log.v(global.TAG, "[PhonebookFragment]: android.intent.action.ClearLogs");
				if(m_listAdapter.getCount() !=0 ) {
					m_bRemoving = true;
//			    	Intent clearPhonebookIntent = new Intent();
//			        clearPhonebookIntent.setClass(getActivity(), BluetoothPbapcClearPhonebookService.class);
//			        getActivity().startService(clearPhonebookIntent);
			        
			        m_listAdapter.swapCursor(null);
				}
		        
			}/*else if (action.equals(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_MSG_RECEIVED)) {
                Log.d(global.TAG, "BLUETOOTH_PBAPC_MSG_RECEIVED received");
                String devAddress = arg1.getStringExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_CONNECTION_ADDRESS);
                int msg = arg1.getIntExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_MSG, BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_UNKOWN_MSG);
                int result = arg1.getIntExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_MSG_RESULT,  BluetoothPhonebookClient.BLUETOOTH_PHONEBOOK_RESULT_CODE_FAILURE);
                String data = arg1.getStringExtra(BluetoothPhonebookClientIntent.BLUETOOTH_PHONEBOOK_MSG_DATA);
                Log.d(global.TAG, "devAddress = " + devAddress + ", msg = " + msg + ", result = " + result + ", data = " + data);
                if (!devAddress.equalsIgnoreCase(devAddress)) {
                    Log.d(global.TAG, "devAddress doesn't match mDeviceAddress, leave mBroadcastReceiver.onReceive");
                    return;
                }
                switch (msg) {
                    case BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_CONNECT_CFM: {
                        if ((result & BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_SRC_SIM) != 0) {
                            Log.d(global.TAG, "SIM card supported!");
                        } else {
                            Log.d(global.TAG, "SIM card not supported!");
                            //When simcard not supported, clear the sim card folder select.
//                            mDownloadFolderId &= ~(BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_SIM1_PB |
//                                    BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_SIM1_ICH |
//                                    BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_SIM1_MCH |
//                                    BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_SIM1_OCH);
//
//                            mSearchFolderId &= ~(BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_SIM1_PB |
//                                    BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_SIM1_ICH |
//                                    BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_SIM1_MCH |
//                                    BluetoothPhonebookClient.CSR_ANDROID_PHONEBOOK_SIM1_OCH);

                        }
                    }
                    break;
                }
            }*/
		}
    	
    };
    
//    class PbFilterAdapter extends BaseAdapter{
//    	
//    	private LayoutInflater mInflater;
////    	private ArrayList<SortCursor.SortEntry> data;
//    	private Context context;
//    	
//        public PbFilterAdapter(Context context,
//        		ArrayList<SortCursor.SortEntry> data) {
//    	    this.mInflater = LayoutInflater.from(context);
//    	    this.data = data;
//    	    this.context = context;
//        }
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return data.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			// TODO Auto-generated method stub
//			return data.get(arg0);
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//
//		@Override
//		public View getView(int arg0, View arg1, ViewGroup arg2) {
//			arg1 = mInflater.inflate(R.layout.pb_listfilter_item, null);
//			
//			//锟斤拷锟斤拷锟斤拷示
//			TextView nameCtrl = (TextView)arg1.findViewById(R.id.pb_name);			
//			String strName = data.get(arg0).mName;
//			nameCtrl.setText(strName);
//			
//			//锟界话锟斤拷锟斤拷锟斤拷示
//			TextView numberCtrl = (TextView)arg1.findViewById(R.id.pb_number);
//			String strNumber = data.get(arg0).mNum;
//			numberCtrl.setText(strNumber);
//			
//			//锟斤拷锟脚帮拷钮
//			ImageButton dialBtn = (ImageButton)arg1.findViewById(R.id.pb_dial_btn);
//			dialBtn.setTag(strNumber);
//			dialBtn.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
////					 Intent intent = new Intent(Intent.ACTION_CALL_PRIVILEGED,
////                             Uri.fromParts("tel", (String)arg0.getTag(), null));
////					 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////					 getActivity().startActivity(intent);
//				}
//			});
//			
//			return arg1;
//		}
//    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case H_GET_BINDER:
                    myBinder = ((CSRBluetoothDemoActivity)getActivity()).getMyBinder();
                    Log.d(TAG, "[PhonebookFragment]:mHandler-H_GET_BINDER ");
                    break;
            }
        }
    };
}
