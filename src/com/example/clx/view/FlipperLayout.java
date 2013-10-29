package com.example.clx.view;

import com.example.clx.util.Logger;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 自己重写的ViewGroup,用与滑动切换界面使用,
 * 
 * 
 */
public class FlipperLayout extends ViewGroup {

	private Scroller mScroller;
	/**
	 * Android里Scroller类是为了实现View平滑滚动的一个Helper类。通常在自定义的View时使用，
	 * 在View中定义一个私有成员mScroller = new
	 * Scroller(context)。设置mScroller滚动的位置时，并不会导致View的滚动
	 * ，通常是用mScroller记录/计算View滚动的位置，再重写View的computeScroll()，完成实际的滚动
	 */
	private VelocityTracker mVelocityTracker;
	/**
	 * 用来追踪触摸事件（flinging事件和其他手势事件）的速率。用obtain()函数来获得类的实例，用addMovement(
	 * MotionEvent)函数将motion
	 * event加入到VelocityTracker类实例中，当你使用到速率时，使用computeCurrentVelocity
	 * (int)初始化速率的单位，并获得当前的事件的速率，然后使用getXVelocity() 或getXVelocity()获得横向和竖向的速率。
	 */
	private int mWidth;

	public static final int SCREEN_STATE_CLOSE = 0;
	public static final int SCREEN_STATE_OPEN = 1;
	public static final int TOUCH_STATE_RESTART = 0;
	public static final int TOUCH_STATE_SCROLLING = 1;
	public static final int SCROLL_STATE_NO_ALLOW = 0;
	public static final int SCROLL_STATE_ALLOW = 1;
	private int mScreenState = 0;
	private int mTouchState = 0;
	private int mScrollState = 0;
	private int mVelocityValue = 0;
	private boolean mOnClick = false;
	private onUgcDismissListener mOnUgcDismissListener;
	private onUgcShowListener mOnUgcShowListener;

	public FlipperLayout(Context context) {
		super(context);
		mScroller = new Scroller(context);
		mWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				200, getResources().getDisplayMetrics());

	}

	public FlipperLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlipperLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			int height = child.getMeasuredHeight();
			int width = child.getMeasuredWidth();
			child.layout(0, 0, width, height);
		}
	}

	/**
	 * 当控件的父元素正要放置该控件时调用.父元素会问子控件一个问题，“你想要用多大地方啊？”，然后传入两个参数——
	 * widthMeasureSpec和heightMeasureSpec. 这两个参数指明控件可获得的空间以及关于这个空间描述的元数据.
	 * 更好的方法是你传递View的高度和宽度到setMeasuredDimension方法里,这样可以直接告诉父控件，需要多大地方放置子控件
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

	public boolean dispatchTouchEvent(MotionEvent ev) {
		obtainVelocityTracker(ev);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_RESTART
					: TOUCH_STATE_SCROLLING;
			if (mTouchState == TOUCH_STATE_RESTART) {
				int x = (int) ev.getX();
				int screenWidth = getWidth();
				if (x <= mWidth && mScreenState == SCREEN_STATE_CLOSE
						&& mTouchState == TOUCH_STATE_RESTART
						|| x >= screenWidth - mWidth
						&& mScreenState == SCREEN_STATE_OPEN
						&& mTouchState == TOUCH_STATE_RESTART) {
					if (mScreenState == SCREEN_STATE_OPEN) {
						mOnClick = true;
					}
					mScrollState = SCROLL_STATE_ALLOW;
				} else {
					mOnClick = false;
					mScrollState = SCROLL_STATE_NO_ALLOW;
				}
			} else {
				return false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			mVelocityTracker.computeCurrentVelocity(1000,
					ViewConfiguration.getMaximumFlingVelocity());
			if (mScrollState == SCROLL_STATE_ALLOW
					&& getWidth() - (int) ev.getX() < mWidth) {
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			releaseVelocityTracker();
			if (mOnClick) {
				mOnClick = false;
				mScreenState = SCREEN_STATE_CLOSE;
				mScroller.startScroll(getChildAt(1).getScrollX(), 0,
						-getChildAt(1).getScrollX(), 0, 800);
				invalidate();
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		obtainVelocityTracker(ev);
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_RESTART
					: TOUCH_STATE_SCROLLING;
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				return false;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			mOnClick = false;
			mVelocityTracker.computeCurrentVelocity(1000,
					ViewConfiguration.getMaximumFlingVelocity());
			if (mScrollState == SCROLL_STATE_ALLOW
					&& Math.abs(mVelocityTracker.getXVelocity()) > 200) {
				return true;
			}
			break;

		case MotionEvent.ACTION_UP:
			releaseVelocityTracker();
			if (mScrollState == SCROLL_STATE_ALLOW
					&& mScreenState == SCREEN_STATE_OPEN) {
				return true;
			}
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	public boolean onTouchEvent(MotionEvent event) {
		obtainVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_RESTART
					: TOUCH_STATE_SCROLLING;
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				return false;
			}
			break;

		case MotionEvent.ACTION_MOVE:
			mVelocityTracker.computeCurrentVelocity(1000,
					ViewConfiguration.getMaximumFlingVelocity());
			mVelocityValue = (int) mVelocityTracker.getXVelocity();
			getChildAt(1).scrollTo(-(int) event.getX(), 0);
			break;

		case MotionEvent.ACTION_UP:
			if (mScrollState == SCROLL_STATE_ALLOW) {
				if (mVelocityValue > 2000) {
					mScreenState = SCREEN_STATE_OPEN;
					mScroller
							.startScroll(
									getChildAt(1).getScrollX(),
									0,
									-(getWidth()
											- Math.abs(getChildAt(1)
													.getScrollX()) -

									mWidth), 0, 250);
					invalidate();

				} else if (mVelocityValue < -2000) {
					mScreenState = SCREEN_STATE_CLOSE;
					mScroller.startScroll(getChildAt(1).getScrollX(), 0,
							-getChildAt(1).getScrollX(), 0, 250);
					invalidate();
				} else if (event.getX() < getWidth() / 2) {
					mScreenState = SCREEN_STATE_CLOSE;
					mScroller.startScroll(getChildAt(1).getScrollX(), 0,
							-getChildAt(1).getScrollX(), 0, 800);
					invalidate();
				} else {
					mScreenState = SCREEN_STATE_OPEN;
					mScroller
							.startScroll(
									getChildAt(1).getScrollX(),
									0,
									-(getWidth()
											- Math.abs(getChildAt(1)
													.getScrollX()) -

									mWidth), 0, 800);
					invalidate();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	public void open() {
		mTouchState = mScroller.isFinished() ? TOUCH_STATE_RESTART
				: TOUCH_STATE_SCROLLING;
		if (mTouchState == TOUCH_STATE_RESTART) {
			mScreenState = SCREEN_STATE_OPEN;
			mScroller.startScroll(getChildAt(1).getScrollX(), 0, -(getWidth()
					- Math.abs(getChildAt(1).getScrollX()) -

			mWidth), 0, 800);
			invalidate();
		}
	}

	public void close(View view) {
		mScreenState = SCREEN_STATE_CLOSE;
		mScroller.startScroll(getChildAt(1).getScrollX(), 0, -getChildAt(1)
				.getScrollX(), 0, 800);
		invalidate();
		setContentView(view);
	}

	/**
	 * computeScroll：主要功能是计算拖动的位移量、更新背景、设置要显示的屏幕(setCurrentScreen(mCurrentScreen
	 * );)。
	 * 
	 * 重写computeScroll()的原因
	 * 
	 * 调用startScroll()是不会有滚动效果的，只有在computeScroll()获取滚动情况，做出滚动的响应
	 * computeScroll在父控件执行drawChild时，会调用这个方法
	 */
	public void computeScroll() {
		super.computeScroll();
		if (mScroller.computeScrollOffset()) {
			getChildAt(1).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
			Logger.debug(this, "-------------");
		} else {
			if (mScreenState == SCREEN_STATE_OPEN) {
				if (mOnUgcDismissListener != null) {
					mOnUgcDismissListener.dismiss();
				}
			} else if (mScreenState == SCREEN_STATE_CLOSE) {
				if (mOnUgcShowListener != null) {
					mOnUgcShowListener.show();
				}
			}
		}
	}

	private void obtainVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	private void releaseVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	public int getScreenState() {
		return mScreenState;
	}

	public void setContentView(View view) {
		removeViewAt(1);
		addView(view, 1, getLayoutParams());
	}

	public interface OnOpenListener {
		public abstract void open();
	}

	public interface OnCloseListener {
		public abstract void close();
	}

	public interface onUgcDismissListener {
		public abstract void dismiss();
	}

	public interface onUgcShowListener {
		public abstract void show();
	}

	public void setOnUgcDismissListener(
			onUgcDismissListener onUgcDismissListener) {
		mOnUgcDismissListener = onUgcDismissListener;
	}

	public void setOnUgcShowListener(onUgcShowListener onUgcShowListener) {
		mOnUgcShowListener = onUgcShowListener;
	}
}
