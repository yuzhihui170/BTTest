package com.example.phoneUtil;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactsUtil {
    private Context mContext;

    public ContactsUtil(Context context) {
        this.mContext = context;
    }

    //向系统插入联系人
    public void insert(String name, String number) {
        //1、向rawUri中插入一条空的数据
        ContentValues values = new ContentValues();
        ContentResolver resolver = mContext.getContentResolver();
        Uri insert = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        //2、获取raw_id
        long _id= ContentUris.parseId(insert);

        //3、向data表中添加数据,向data表中插入数据是分条添加，因为调用一次insert只能向数据库中添加一个字段的数据
        ContentValues valuesName = new ContentValues();
        //3.1、向哪一条联系人中添加数据
        valuesName.put(ContactsContract.Data.RAW_CONTACT_ID, _id);
        //3.2、添加名字
        valuesName.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        //3.3、指定类型
        valuesName.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        resolver.insert(ContactsContract.Data.CONTENT_URI, valuesName);
        //4添加电话
        ContentValues valuesNumber = new ContentValues();
        //4.1、指定_id
        valuesNumber.put(ContactsContract.Data.RAW_CONTACT_ID, _id);
        //4.2、添加电话号码
        valuesNumber.put(ContactsContract.CommonDataKinds.Phone.NUMBER, number);
        //4.3、指定数据类型
        valuesNumber.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        resolver.insert(ContactsContract.Data.CONTENT_URI, valuesNumber);
    }

}
