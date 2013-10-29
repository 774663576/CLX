package com.example.clx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class UserInfo extends ViewGroup {

	public UserInfo(Context context) {
		super(context);
	}

	public UserInfo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public UserInfo(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			int height = child.getMeasuredHeight();
			int width = child.getMeasuredWidth();
			child.layout(0, 0, width, height);
		}
	}

	public void setView(View view) {
		removeAllViews();
		addView(view);
	}

	/**
	 * 当控件的父元素正要放置该控件时调�?父元素会问子控件�?��问题，�? 你想要用多大地方啊？”，然后传入两个参数—�?
	 * widthMeasureSpec和heightMeasureSpec. 这两个参数指明控件可获得的空间以及关于这个空间描述的元数�?
	 * 更好的方法是你传递View的高度和宽度到setMeasuredDimension方法� ?这样可以直接告诉父控件，�?��多大地方放置子控�?
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
		for (int i = 0; i < getChildCount(); i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}
