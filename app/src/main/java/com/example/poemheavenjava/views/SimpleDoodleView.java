package com.example.poemheavenjava.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.poemheavenjava.MyApplication;
import com.example.poemheavenjava.entity.Item;
import com.example.poemheavenjava.utils.TouchGestureDetector;

import java.util.ArrayList;
import java.util.List;

public class SimpleDoodleView extends View {

    private final static String TAG = "lily";

    private MyApplication mApp = MyApplication.getInstance();
    private int cu_item_id = 0;

    private Paint mPaint = new Paint();

    public void setmPathList(List<Path> mPathList) {
        this.mPathList = mPathList;
    }

    private List<Path> mPathList = new ArrayList<>(); // 保存涂鸦轨迹的集合
    private TouchGestureDetector mTouchGestureDetector; // 触摸手势监听
    private float mLastX, mLastY;
    private Path mCurrentPath; // 当前的涂鸦轨迹

    public SimpleDoodleView(Context context) {
        super(context);
        // 设置画笔
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        // 由手势识别器处理手势
        mTouchGestureDetector = new TouchGestureDetector(getContext(), new TouchGestureDetector.OnTouchGestureListener() {

            @Override
            public void onScrollBegin(MotionEvent e) { // 滑动开始
                mCurrentPath = new Path(); // 新的涂鸦
                mPathList.add(mCurrentPath); // 添加的集合中
                Log.d(TAG, "onScrollBegin"+mPathList.toString());
                mCurrentPath.moveTo(e.getX(), e.getY());
                mLastX = e.getX();
                mLastY = e.getY();
                invalidate(); // 刷新
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { // 滑动中
                Log.d(TAG, "onScroll");
                mCurrentPath.quadTo(
                        mLastX,
                        mLastY,
                        (e2.getX() + mLastX) / 2,
                        (e2.getY() + mLastY) / 2); // 使用贝塞尔曲线 让涂鸦轨迹更圆滑
                mLastX = e2.getX();
                mLastY = e2.getY();
                invalidate(); // 刷新
                return true;
            }

            @Override
            public void onScrollEnd(MotionEvent e) { // 滑动结束
                mCurrentPath.quadTo(
                        mLastX,
                        mLastY,
                        (e.getX() + mLastX) / 2,
                        (e.getY() + mLastY) / 2); // 使用贝塞尔曲线 让涂鸦轨迹更圆滑
                Log.d(TAG, "onScrollEnd");
                mCurrentPath = null; // 轨迹结束
                invalidate(); // 刷新
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent");
        boolean consumed = mTouchGestureDetector.onTouchEvent(event); // 由手势识别器处理手势
        if (!consumed) {
            return super.dispatchTouchEvent(event);
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Path path : mPathList) { // 绘制涂鸦轨迹
            canvas.drawPath(path, mPaint);
            Log.d(TAG, "onDraw");
        }
    }

    //保存画的路径
    public void savePath(Item item) {
        if (item != null && mPathList.size() > 0) {
            //Log.d(TAG, mCurrentPath.toString());
            item.setActionPath(mPathList.get(mPathList.size()-1), getContext());
            Log.d(TAG, mPathList.get(mPathList.size()-1).toString());
        }else Log.d(TAG, "null");
    }

    //删除最后一个路径
    public void removeLastPath() {
        if (mApp.getLastItem() != null && mApp.getLastItem().getActionPath() != null) {
            mPathList.remove(mPathList.size() - 1);
            postInvalidate();
        }
    }

}
