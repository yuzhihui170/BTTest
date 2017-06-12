package com.example.bttest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.btapp.R;
import com.example.btutil.BTUtil;
import com.example.phoneUtil.ContactsUtil;

public class MainActivity extends Activity {
    private final static String TAG = "yzh";
    private Button mBtn;

    private ContactsUtil mContactsUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViewListener();

        mContactsUtil = new ContactsUtil(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void setViewListener() {
        mBtn = (Button) findViewById(R.id.btn_insert);
        mBtn.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_insert:
                    mContactsUtil.insert();
                    break;

                default:
                    break;
            }
        }
    };
}
