package com.mutitouchevent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 该demo实现的是，如果两个手指同时触摸view，然后突然有一个手指up了，不要让view瞬间移动到另一手指处。而是将滑动动作传递下去，传给仍然move的手指。
 */
public class MutiTouch_1_JieLiXing extends View {
    private static final float IMAGE_WIDTH = Utils.dpToPixel(200);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Bitmap mBitmap;

    //偏移量
    private float offsetX;
    private float offsetY;
    float downX = 0;
    float downY = 0;

    float originOffsetX = 0;
    float originOffsetY = 0;

    public MutiTouch_1_JieLiXing(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBitmap = Utils.getAvatar(getResources(), (int) IMAGE_WIDTH);

    }

    //当前处理事件的手指，pointerID，
    private int mTrackingPointerId1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mTrackingPointerId1 = event.getPointerId(0);
                downX = event.getX();
                downY = event.getY();
                //保存上次移动到的位置
                originOffsetX = offsetX;
                originOffsetY = offsetY;
                break;
            case MotionEvent.ACTION_MOVE:
                //获取当前手指的index
                int pointerIndex = event.findPointerIndex(mTrackingPointerId1);
                offsetX = originOffsetX + event.getX(pointerIndex) - downX;
                offsetY = originOffsetY + event.getY(pointerIndex) - downY;
                invalidate();
                break;
                //多点触摸按下，其他手指按下,那么直接把事件传递给这个手指来处理
            case MotionEvent.ACTION_POINTER_DOWN:
                //获取新的手指index
                int actionIndex = event.getActionIndex();
                //根据index获取该手指的Id
                mTrackingPointerId1 = event.getPointerId(actionIndex);
                downX = event.getX(actionIndex);
                downY = event.getY(actionIndex);
                originOffsetX = offsetX;
                originOffsetY = offsetY;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //获取当前活跃的index
                actionIndex = event.getActionIndex();
                //通过index获取对应的pointerId
                int pointerId = event.getPointerId(actionIndex);
                //如果活跃的id正好是当前处理事件的id
                //如果是活跃的id，那么就把事件传递给最后一个pointer。这个规则只是随心想的，换个规则也没问题。
                if (pointerId == mTrackingPointerId1) {
                    //如果要传递给的这个pointer恰好就是最好一个，那么就传给倒数第二个
                    int newIndex;
                    if (actionIndex == event.getPointerCount() - 1) {
                        newIndex = event.getPointerCount() - 2;
                    } else {
                        newIndex = event.getPointerCount() - 1;
                    }
                    //传递事件给这个pointer
                    mTrackingPointerId1 = event.getPointerId(newIndex);
                    downX = event.getX(actionIndex);
                    downY = event.getY(actionIndex);
                    originOffsetX = offsetX;
                    originOffsetY = offsetY;
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, offsetX, offsetY, mPaint);
    }
}
