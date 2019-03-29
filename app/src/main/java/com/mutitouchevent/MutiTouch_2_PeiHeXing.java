package com.mutitouchevent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 多手指同时滑动，把多手指的算出平局值来处理滑动。
 */
public class MutiTouch_2_PeiHeXing extends View {
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

    public MutiTouch_2_PeiHeXing(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBitmap = Utils.getAvatar(getResources(), (int) IMAGE_WIDTH);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //计算每次事件,都重新计算总体移动距离
        float sumX = 0;
        float sumY = 0;
        //获取手指总个数
        int pointerCount = event.getPointerCount();
        //判断当前是否为抬起事件，如果是抬起，就不算它了。
        boolean isPointerUp = event.getActionMasked() == MotionEvent.ACTION_POINTER_UP;
        //遍历所有的手指
        for (int i = 0; i < pointerCount; i++) {
            //遍历到的这个手指是当前活跃的手指，而且它还是抬起事件，那么就不记录这个了。
            if (i == event.getActionIndex() && isPointerUp) {
                continue;
            }
            sumX += event.getX(i);
            sumY += event.getY(i);
        }
        //如果有抬起事件就把基数减一
        if (isPointerUp) {
            pointerCount -= 1;
        }

        //多手指的中心点
        float focusX = sumX / pointerCount;
        float focusY = sumY / pointerCount;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //多点触摸按下，
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                downX = focusX;
                downY = focusY;
                originOffsetX = offsetX;
                originOffsetY = offsetY;
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = originOffsetX + focusX - downX;
                offsetY = originOffsetY + focusY - downY;
                invalidate();
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
