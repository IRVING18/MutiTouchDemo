package com.mutitouchevent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class MutiTouch_3_HuBuGanRao extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //存储多手指的产生的path
    SparseArray<Path> pathList;

    public MutiTouch_3_HuBuGanRao(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(Utils.dpToPixel(4));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

        pathList = new SparseArray<>();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //多手指
            case MotionEvent.ACTION_POINTER_DOWN:
                //获取当前活动的index
                int actionIndex = event.getActionIndex();
                //根据index获取pointerId
                int pointerId = event.getPointerId(actionIndex);
                //因为pointerID不会清除掉，会存下来，等待复用。而我们用的pointerID当的数组中的位置，所以这个位置可能复用，复用的时候就不创建新的path了。
                //如果pointerId对应的Path存储过，那就直接设置
                if (pathList.get(pointerId) == null) {
                    Path path = new Path();
                    path.moveTo(event.getX(actionIndex), event.getY(actionIndex));
                    pathList.append(pointerId, path);
                } else {
                    pathList.valueAt(pointerId).moveTo(event.getX(actionIndex), event.getY(actionIndex));
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int p = event.getPointerId(i);
                    pathList.get(p).lineTo(event.getX(i), event.getY(i));
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                int p = event.getPointerId(event.getActionIndex());
                pathList.valueAt(p).reset();
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < pathList.size(); i++) {
            canvas.drawPath(pathList.valueAt(i), mPaint);
        }
    }
}
