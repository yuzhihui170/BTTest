/*
 * Copyright (C) Apical
 */
package com.example.btapp;

//import com.csr.BTApp.common.global;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

//Author: YoungE
public class TabFragment extends Fragment {

	private ImageButton Btn_DeviceList;
	private ImageButton Btn_MusicAvrcp;
	private ImageButton Btn_Phonebook;
	private ImageButton Btn_Phone;
	private ImageButton Btn_CallRecord;
	private ImageButton Btn_setting;
	private ImageButton m_btnWhichSel; //���洦��ѡ��״̬�İ�ť
	private View m_tabview;
	private OnTabClickListener m_tabClickListener;
	private int m_curTabIdx =  -1;
	
	private SettingFragment m_settingFrag = new SettingFragment();
	
	static interface OnTabClickListener {
		void onTabClick(int tabIndex);
	}
	
	public void setOnTabClickListerner(OnTabClickListener listener) {
		m_tabClickListener = listener;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return super.onCreateView(inflater, container, savedInstanceState);
		m_tabview = inflater.inflate(R.layout.tab_fragment, container, false);
		initComponents();
		return m_tabview;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshComponentsState();
	}

	private void initComponents(){
        
		Btn_CallRecord = (ImageButton) m_tabview.findViewById(R.id.Btn_CallRecord);
		Btn_CallRecord.setOnClickListener(new ClickEvent());   
        
        Btn_DeviceList = (ImageButton) m_tabview.findViewById(R.id.Btn_DeviceList);
        Btn_DeviceList.setOnClickListener(new ClickEvent()); 
        
        Btn_MusicAvrcp = (ImageButton) m_tabview.findViewById(R.id.Btn_MusicAvrcp);
        Btn_MusicAvrcp.setOnClickListener(new ClickEvent()); 
        
        Btn_Phone = (ImageButton) m_tabview.findViewById(R.id.Btn_Phone);
        Btn_Phone.setOnClickListener(new ClickEvent());
        
        Btn_Phonebook = (ImageButton) m_tabview.findViewById(R.id.Btn_Phonebook);
        Btn_Phonebook.setOnClickListener(new ClickEvent()); 
        
        Btn_setting = (ImageButton) m_tabview.findViewById(R.id.Btn_setting);
        Btn_setting.setOnClickListener(new ClickEvent()); 
    }
	
	public void refreshComponentsState(){
//    	global.refershConnectedDevices();
    }
	
	/**
	 * ���ð�ť�Ƿ�ѡ��
	 * @param idxBtn:��ʾҪѡ�е����ĸ�Btn
	 */
	public void setBtnOnSel(int idxBtn) {
		if(m_btnWhichSel != null)
			m_btnWhichSel.setSelected(false);
		switch(idxBtn) {
		case 0:
			Btn_Phone.setSelected(true);
			m_btnWhichSel = Btn_Phone;
			m_curTabIdx = 0;
			break;
		case 1:
			Btn_Phonebook.setSelected(true);
			m_btnWhichSel = Btn_Phonebook;
			m_curTabIdx = 1;
			break;
		case 2:
			Btn_CallRecord.setSelected(true);
			m_btnWhichSel = Btn_CallRecord;
			m_curTabIdx = 2;
			break;
		case 3:
			Btn_MusicAvrcp.setSelected(true);
			m_btnWhichSel = Btn_MusicAvrcp;
			m_curTabIdx = 3;
			break;
		case 4:
			Btn_DeviceList.setSelected(true);
			m_btnWhichSel = Btn_DeviceList;
			m_curTabIdx = 4;
			break;
		case 5:
			Btn_setting.setSelected(true);
			m_btnWhichSel = Btn_setting;
			m_curTabIdx = 5;
			break;
		default:
			m_curTabIdx = -1;
			break;
		}
	}
	
	public int getCurTabPos() {
		return m_curTabIdx;
	}
	
	class ClickEvent implements View.OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
//			Log.v(global.TAG, "TabFragment:ClickEvent");
        	Intent intent = new Intent();
        	
			if(arg0 == Btn_Phone){
//				Log.d(global.TAG, "[TabFragment] OnClick:Btn_Phone");
				setBtnOnSel(0);
				if(m_tabClickListener != null)
					m_tabClickListener.onTabClick(0);
			}
			else if(arg0 == Btn_Phonebook){
//				Log.v(global.TAG, "OnClick:Btn_Phonebook");
				setBtnOnSel(1);
				if(m_tabClickListener != null)
					m_tabClickListener.onTabClick(1);
			}
			else if(arg0 == Btn_CallRecord){
				setBtnOnSel(2);
				if(m_tabClickListener != null)
					m_tabClickListener.onTabClick(2);
			}
			else if(arg0 == Btn_MusicAvrcp){
//				Log.v(global.TAG, "OnClick:Btn_MusicAvrcp");
				setBtnOnSel(3);
				if(m_tabClickListener != null)
					m_tabClickListener.onTabClick(3);
			}
			else if(arg0 == Btn_DeviceList){
//				Log.v(global.TAG, "OnClick:Btn_DeviceList");
				setBtnOnSel(4);
				if(m_tabClickListener != null)
					m_tabClickListener.onTabClick(4);
			}
			else if(arg0 == Btn_setting){
//				Log.v(global.TAG, "OnClick:Btn_setting");
				setBtnOnSel(5);
				if(m_tabClickListener != null)
					m_tabClickListener.onTabClick(5);
			}
		}
		
	}
}
