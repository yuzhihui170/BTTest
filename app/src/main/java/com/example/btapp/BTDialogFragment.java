package com.example.btapp;

import java.util.HashMap;

//import com.csr.BTApp.apical.util.ClsUtils;
//import com.csr.BTApp.apical.util.ToastUtils;
//import com.csr.BTApp.common.BluetoothUtils;
//import com.csr.BTApp.common.CommonUtils;
//import com.csr.BTApp.common.global;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothAvrcpCtl;
import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothHFP;
//import android.bluetooth.BluetoothPhonebookClient;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Gallery.LayoutParams;

public class BTDialogFragment extends DialogFragment {
	public static final int DIALOG_PAIRED_AND_CONNECTED = 0;
	public static final int DIALOG_PAIRED_BUT_NOT_CONNECTED = 1;
	public static final int DIALOG_NO_PAIR = 2;
	public static final int DIALOG_PBAP_AUTHENTICATION = 5;
	String selectedBTAddress;
	String selectedBTName;
	String BondedAddress;
	private BluetoothAdapter btAdapter;
	
	public static BTDialogFragment newInstance(int dialogType, String btAddress, String btName) {
		BTDialogFragment dialog = new BTDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("DialogType", dialogType);
		bundle.putString("BTAddress", btAddress);
		bundle.putString("BTName", btName);
		dialog.setArguments(bundle);
		return dialog;
	}

	public void openProfilelist(String BTAddress){
		Intent intent = new Intent();
		intent.putExtra("BTAddress", BTAddress);
//		intent.setClass(getActivity(), ProfileList.class);
		getActivity().startActivityForResult(intent, 0);
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		int dialogType = getArguments().getInt("DialogType");
		selectedBTAddress = getArguments().getString("BTAddress");
		selectedBTName = getArguments().getString("BTName");
		switch(dialogType) {
		case DIALOG_PBAP_AUTHENTICATION: {
			Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			final View viewAuthen = inflater.inflate(
					R.layout.bluetooth_pbapc_device_authentication, null);
			final EditText editPassword = (EditText) viewAuthen
					.findViewById(R.id.bt_pbapc_device_setting_password);
			editPassword.setText("");
			builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.bluetooth_pbapc_device_authentication);
			builder.setView(viewAuthen);
			builder.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
//							global.bu.pbapConnectAuthMode(selectedBTAddress,
//									"", editPassword.getText().toString());
						}
					});
			builder.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					
				}
			});
			return builder.create();
		}
		case DIALOG_PAIRED_AND_CONNECTED:
			String[] menu = new String[] { getActivity().getResources().getString(R.string.DL_UnPair),
					getActivity().getResources().getString(R.string.DL_UnConnect)};
			Builder b1 = new AlertDialog.Builder(getActivity());
			b1.setTitle(selectedBTName);
			b1.setIcon(R.drawable.dialog_icon); 
			b1.setItems(menu, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					Log.v(global.TAG,
//							"DeviceListActivity:Dialog:PairedAndConnected:Start");
					
//					if(which == 0){
//						global.bu.avrcpDisconnectAll();
//						global.bu.a2dpDisconnect(selectedBTAddress);
//						global.hfp.hfpDisconnectAll();
//						global.bu.pbapDisconnect(selectedBTAddress);
//						CommonUtils.sleep(300);
//						global.bu.unpair(selectedBTAddress);
//						
//						//断开匹配 清空bondedAddress里的值
//				    	SharedPreferences sp = getActivity().getSharedPreferences("bondedAddress", Context.MODE_PRIVATE);
//				    	
//				        //存入数据
//				    	Editor editor = sp.edit();
//				    	editor.putString("STRING_KEY","");
//				    	editor.commit();
//				    	
//						Intent intent = new Intent("android.intent.action.Nobondeddevice");
//						getActivity().sendBroadcast(intent);
//					}else if(which == 1){
//		            	SharedPreferences sp = getActivity().getSharedPreferences("Connect_Stats", Context.MODE_PRIVATE);
//		            	Editor editor = sp.edit();
//		            	editor.putBoolean("ConnectFlag", false);
//		            	editor.commit();
//		            	
//						Intent intent = new Intent("android.intent.action.Disconnectdevice");
//						getActivity().sendBroadcast(intent);
//						
//						global.bu.avrcpDisconnectAll();
//						global.bu.a2dpDisconnect(selectedBTAddress);
//						global.hfp.hfpDisconnectAll();
//						global.bu.pbapDisconnect(selectedBTAddress);
//						CommonUtils.sleep(300);
//					}
//
//					Log.v(global.TAG,
//							"DeviceListActivity:Dialog:PairedAndConnected:End");
				}
			});
			return b1.create();
		case DIALOG_PAIRED_BUT_NOT_CONNECTED:
			String[] menu1 = new String[] { getActivity().getResources().getString(R.string.DL_UnPair),
					getActivity().getResources().getString(R.string.DL_Connect)};
			Builder b2 = new AlertDialog.Builder(getActivity());
			b2.setTitle(selectedBTName);
			b2.setIcon(R.drawable.dialog_icon); 
			b2.setItems(menu1, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0){
						// Unpair
//						global.bu.unpair(selectedBTAddress);
						
						//断开匹配 清空bondedAddress里的值
				    	SharedPreferences sp = getActivity().getSharedPreferences("bondedAddress", Context.MODE_PRIVATE);
				    	
				        //存入数据
				    	Editor editor = sp.edit();
				    	editor.putString("STRING_KEY","");
				    	editor.commit();
				    	
						Intent intent = new Intent("android.intent.action.Nobondeddevice");
						getActivity().sendBroadcast(intent);

					}else if(which == 1){
						// Connect
		            	SharedPreferences sp = getActivity().getSharedPreferences("Connect_Stats", Context.MODE_PRIVATE);
		            	Editor editor = sp.edit();
		            	editor.putBoolean("ConnectFlag", true);
		            	editor.commit();
		            	
						Intent intent = new Intent("android.intent.action.Connectdevice");
						getActivity().sendBroadcast(intent);
					}
//					Log.v(global.TAG,"DeviceListActivity:Dialog:PairedButNotConnected:End");
				}
			});
			
			return b2.create();
		case DIALOG_NO_PAIR:
			String[] menu2 = new String[] { getActivity().getResources().getString(R.string.DL_Pair)};
			AlertDialog  b3 = new AlertDialog.Builder(getActivity()).
			setTitle(selectedBTName).
			setIcon(R.drawable.dialog_icon).
			setItems(menu2, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
   				 SharedPreferences sp = getActivity().getSharedPreferences("bondedAddress",  getActivity().MODE_PRIVATE);
   				 BondedAddress = sp.getString("STRING_KEY", "");
				
//            		if(BluetoothDevice.BOND_BONDED == global.bu.getBluetoothPairState(BondedAddress)){
//           			ToastUtils.showMessage(getActivity(), getActivity().getResources().getString(R.string.please_disconnect_device));
//    				}else{
//    					global.bu.pair(selectedBTAddress);
//            		}
            	
//					ClsUtils.pair(selectedBTAddress, "000000"); 
				}
			}).create();
			
			return b3;
		}
		return null;
	}

	
}
