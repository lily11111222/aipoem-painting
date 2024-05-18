package com.example.poemheavenjava.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.poemheavenjava.MyApplication;
import com.example.poemheavenjava.entity.ItemMove;
import com.example.poemheavenjava.entity.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BackgroundView extends RelativeLayout {
    private final static String TAG = "lily";
    private MyApplication mApp = MyApplication.getInstance();
    //用以画出路径
    private List<Path> actionPaths = new ArrayList<>();
    private Paint mPaint = new Paint();
    private Boolean staticFlag = true;
    private List<ImageView> item_views = new ArrayList<>();
    //ActionView的序号
    private int iv_index;
    private int iv_size;
    private List<ItemMove> itemMoves = new ArrayList<>();
    private AnimatorSet animatorSet = new AnimatorSet();

    public BackgroundView(Context context) {
        super(context);
        //设置可执行ondraw方法
        setWillNotDraw(false);
        // 设置画笔
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        iv_index = -1;
        animatorSet.setDuration(5000);

        staticFlag = true;
    }

    public List<Path> getActionPaths() {
        return actionPaths;
    }

    public void addActionPath(Path actionPath) {
        this.actionPaths.add(actionPath);
//        staticFlag = true;
        //当调用该函数即调用onDraw
        Log.d(TAG, "添加了一个路径给list");
        postInvalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        Log.d(TAG, "画");
        if(staticFlag == true && actionPaths != null) {
            //是静态背景，未播放则显示出路径
            for (Path path : actionPaths) { // 绘制涂鸦轨迹
                canvas.drawPath(path, mPaint);
                Log.d(TAG, "画了一个路径");
            }
        }else if (staticFlag == false){
            //是静态背景，未播放则显示出路径
//            for (Path path : actionPaths) { // 绘制涂鸦轨迹
//                canvas.drawPath(path, mPaint);
//                Log.d(TAG, "画了一个路径");
//            }
            if (iv_size > 0 && itemMoves.get(iv_size-1) != null) {
                for (int i = 0; i < iv_size; i++) {
                    ItemMove itemMove = itemMoves.get(i);
                    //是动态背景，要播放，item沿着动画走
                    //初始位置
                    float[] pos0 = new float[2];
                    float[] pos= new float[2];
                    itemMove.getPathMeasure().getPosTan(0, pos0, null);
                    itemMove.getPathMeasure().getPosTan(itemMove.getmLength() * itemMove.getmAnimatorValue(), pos, null);
                    //float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
                    //canvas.rotate(degrees, pos[0], pos[1]);
                    //canvas.drawBitmap(bitmap, pos[0] - bitmap.getWidth()/2, pos[1] - bitmap.getHeight(), mPaint);
                    itemMove.getEndMatrix().set(itemMove.getStartMatrix());
                    itemMove.getEndMatrix().postTranslate(pos[0] - pos0[0], pos[1] - pos0[1]);
                    item_views.get(i).setImageMatrix(itemMove.getEndMatrix());
                    //canvas.restore();
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
    }
    public void addActionView(View view) {
        item_views.add((ImageView) view);
    }

    //更换当前背景是处于静态还是处于播放状态
    public void switchStatic(Boolean staticFlag) {
        this.staticFlag = staticFlag;
        //当调用该函数时，ondraw被调用
        postInvalidate();
    }

    //是某一个Item动起来
    public void animatorBegin(int i) {
        //iv_index代表第几个ActionView
        iv_index++;
        //i对应第i个item要运动，也是item_views中的第i个
        //绑定PathMeasure
        Item item = mApp.getItems().get(i);
        ItemMove itemMove = new ItemMove(item.getActionPath(), item_views.get(i));

        //设置值动画
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setFloatValues(0, 1);
        valueAnimator.setDuration(5000);
        // 添加listener，每次valueAnimator值变了，就调用这个函数，
        // 也就是不断设置actionView的mAnimatorValue这个值，结合其路径长度不断设置其位置就能让他动起来
        valueAnimator.addUpdateListener(animation -> {
            itemMove.setmAnimatorValue((float) animation.getAnimatedValue());
            postInvalidate();
        });
        itemMoves.add(itemMove);
        //线性插值
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(1);
        valueAnimator.start();
    }

    //ValueAnimator动画设置
    public void setAnimator(List<Item> items) {
        iv_size = item_views.size();
        Collection<Animator> collection = new ArrayList<>();
        for (int i = 0; i < iv_size; i++) {
            Item item = items.get(i);
            ItemMove itemMove = new ItemMove(item.getActionPath(), item_views.get(i));
            //设置值动画
            ValueAnimator valueAnimator = new ValueAnimator();
            valueAnimator.setFloatValues(0, 1);
//            valueAnimator.setDuration(5000);
            // 添加listener，每次valueAnimator值变了，就调用这个函数，
            // 也就是不断设置actionView的mAnimatorValue这个值，结合其路径长度不断设置其位置就能让他动起来
            valueAnimator.addUpdateListener(animation -> {
                itemMove.setmAnimatorValue((float) animation.getAnimatedValue());
                postInvalidate();
            });
            itemMoves.add(itemMove);
            //线性插值
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.setRepeatCount(0);
//            valueAnimator.start();
            long time = item.getStartTime().getMinute() * 60 + item.getStartTime().getSecond();
            valueAnimator.setStartDelay(time * 1000);
            collection.add(valueAnimator);
        }
//        ToastUtil.show(getContext(), "play!");
        animatorSet.playTogether(collection);
        animatorSet.start();
    }


}
