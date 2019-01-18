package io.pp.net_disk_demo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomSwitchButton extends View {

    private Bitmap mTrackOff;
    private Bitmap mTrackOn;
    private Bitmap mThumb;

    private int mTrackWidth;
    private int mTrackHeight;
    private int mThumbWidth;
    private int mThumbHeight;

    private int mTrackPadding;

    private int currentX;
    private boolean isTouch;
    private boolean state;
    private OnStateChangeListener onStateChangeListener;

    private Paint mShadowPaint;

    public CustomSwitchButton(Context context) {
        this(context, null);
    }

    public CustomSwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mShadowPaint = new Paint();
        mShadowPaint.setColor(0x33000000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mTrackOff != null && mTrackOn != null && mThumb != null) {
            if (state) {
                canvas.drawBitmap(mTrackOn, mTrackPadding, mTrackPadding, null);
            } else {
                canvas.drawBitmap(mTrackOff, mTrackPadding, mTrackPadding, null);
            }

            int left;
            if (isTouch) {
                left = currentX - mThumbWidth / 2;

                if (left < 0) {
                    left = 0;
                }

                if (left > mTrackWidth - mThumbWidth) {
                    left = mTrackWidth - mThumbWidth;
                }
                canvas.drawBitmap(mThumb, left + mTrackPadding, mTrackPadding, null);//Slippery rocks

                canvas.drawCircle(left + mThumbWidth / 2 + mTrackPadding, mThumbHeight / 2 + mTrackPadding, mThumbWidth / 2 + mTrackPadding, mShadowPaint);
            } else {
                left = 0;
                if (state) {
                    left = mTrackWidth - mThumbWidth;
                }

                canvas.drawBitmap(mThumb, left + mTrackPadding, mTrackPadding, null); //Slippery rocks
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                currentX = (int) event.getX();
                isTouch = true;

                break;

            case MotionEvent.ACTION_MOVE:

                currentX = (int) event.getX();
                isTouch = true;

                break;

            case MotionEvent.ACTION_UP:

                isTouch = false;
                currentX = (int) event.getX();

                state = !state;

                onStateChangeListener.onStateChange(state);

                break;
        }

        invalidate();

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(mTrackWidth + 2 * mTrackPadding,
                (mTrackHeight > mThumbHeight ? mTrackHeight : mThumbHeight) + 2 * mTrackPadding);//Set the required width and height
    }

    public void setTrackOffBitmap(int trackOffResId) {
        mTrackOff = BitmapFactory.decodeResource(getResources(), trackOffResId);

        if (mTrackOff != null) {
            mTrackWidth = mTrackWidth > mTrackOff.getWidth() ? mTrackWidth : mTrackOff.getWidth();
            mTrackHeight = mTrackHeight > mTrackOff.getHeight() ? mTrackHeight : mTrackOff.getHeight();
        }
    }

    public void setTrackOnBitmap(int trackOnResId) {
        mTrackOn = BitmapFactory.decodeResource(getResources(), trackOnResId);

        if (mTrackOn != null) {
            mTrackWidth = mTrackWidth > mTrackOn.getWidth() ? mTrackWidth : mTrackOn.getWidth();
            mTrackHeight = mTrackHeight > mTrackOn.getHeight() ? mTrackHeight : mTrackOn.getHeight();
        }
    }

    public void setThumbBitmap(int thumbResId) {
        mThumb = BitmapFactory.decodeResource(getResources(), thumbResId);

        if (mThumb != null) {
            mThumbWidth = mThumbWidth > mThumb.getWidth() ? mThumbWidth : mThumb.getWidth();
            mThumbHeight = mThumbHeight > mThumb.getHeight() ? mThumbHeight : mThumb.getHeight();

            mTrackPadding = mThumbWidth > mThumbHeight ? mThumbWidth / 10 : mThumbHeight / 10;
        }
    }

    public interface OnStateChangeListener {
        void onStateChange(boolean state);
    }

    public void setOnStateChange(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }
}