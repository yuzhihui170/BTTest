/*
 * Copyright(c) Apical
 */
package com.example.btapp.apical.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/*
 * 字母列的实现视图类
 * */
public class AlphabetScrollBar extends View {

	private Paint mPaint = new Paint();
	private String[] mAlphabet = new String[] {
		"A", "B", "C", "D", "E", "F", "G","H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
		"R", "S", "T", "U", "V", "W", "X", "Y", "Z"
	};
	private boolean mPressed;
	private int mCurPosIdx = -1;
	private int mOldPosIdx = -1;
	private OnTouchBarListener mTouchListener;
	
	public static interface OnTouchBarListener {
		void onTouch(String alphabet);
	}
	
	public AlphabetScrollBar(Context arg0, AttributeSet arg1, int arg2) {
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	public AlphabetScrollBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public AlphabetScrollBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		int singleLetterH = height/mAlphabet.length;
		
		if(mPressed) {
			//如果处于按下状态，改变背景及相应字体的颜色
			canvas.drawColor(Color.parseColor("#40000000"));
		}
		
		for(int i=0; i<mAlphabet.length; i++) {
			mPaint.setColor(Color.parseColor("#FFFFFF"));
			mPaint.setAntiAlias(true);
			mPaint.setTextSize(17);
			
			float x = width/2 - mPaint.measureText(mAlphabet[i])/2;
			float y = singleLetterH*i+singleLetterH;
			
			if(i == mCurPosIdx)
			{
				mPaint.setColor(Color.parseColor("#0000FF"));
				mPaint.setFakeBoldText(true);
			}
			canvas.drawText(mAlphabet[i], x, y, mPaint);
			mPaint.reset();
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		int action = arg0.getAction();
		switch(action) {
		case MotionEvent.ACTION_DOWN:
			mPressed = true;
			mCurPosIdx =(int)( arg0.getY()/this.getHeight() * mAlphabet.length);
			if(mTouchListener != null && mOldPosIdx!=mCurPosIdx){
				if((mCurPosIdx>=0) && (mCurPosIdx<mAlphabet.length)) {
					mTouchListener.onTouch(mAlphabet[mCurPosIdx]);
					this.invalidate();
				}
				mOldPosIdx = mCurPosIdx;
			}
			return true;
		case MotionEvent.ACTION_UP:
			mPressed = false;
			mCurPosIdx = -1;
			this.invalidate();
			return true;
		case MotionEvent.ACTION_MOVE:
			mCurPosIdx =(int)( arg0.getY()/this.getHeight() * mAlphabet.length);
			if(mTouchListener != null && mCurPosIdx!=mOldPosIdx){
				if((mCurPosIdx>=0) && (mCurPosIdx<mAlphabet.length)) {
					mTouchListener.onTouch(mAlphabet[mCurPosIdx]);
					this.invalidate();
				}
				mOldPosIdx = mCurPosIdx;
			}
			return true;
		default:
			return super.onTouchEvent(arg0);
		}
		
	}
	
	public void setOnTouchBarListener (OnTouchBarListener listener) {
		mTouchListener = listener;
	}
}
