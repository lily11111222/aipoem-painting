package com.example.poemheavenjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.poemheavenjava.entity.Item;
import com.example.poemheavenjava.utils.DimenUtils;
import com.example.poemheavenjava.utils.TimeUtil;
import com.example.poemheavenjava.utils.ToastUtil;
import com.example.poemheavenjava.views.BackgroundView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SetActionTimeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "lily";
    private MyApplication mApp;
    private RelativeLayout rl_bg;
    private BackgroundView bgv;
    private LinearLayout.LayoutParams layoutParams;
    private EditText et_time_min;
    private EditText et_time_sec;
    private LinearLayout linearLayout;
    private Button btn_play;
    private TextView tv_ani_time;
    private List<ImageView> item_views = new ArrayList<>();
    private ArrayList<GifImageView> gif_v_list = new ArrayList<GifImageView>();
    //将要运动的item
    private List<Item> actionItems = new ArrayList<>();
    private Boolean playFlag = false;
    private ArrayList<View> action_time_views;
    private Handler handler;
    private Integer time_sec = -1;
    private Integer time_min = -1;
    // 动画播放的总时长
    Integer total_time = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_action_time);
        ToastUtil.showIntro(this, 4);
        //获取MyApplication唯一实例
        mApp = MyApplication.getInstance();
        //获取relativeLayout
        rl_bg = findViewById(R.id.rl_bg);
        et_time_min = findViewById(R.id.et_time_min);
        et_time_sec = findViewById(R.id.et_time_sec);
        btn_play = findViewById(R.id.btn_play);
        linearLayout = findViewById(R.id.linear_layout);
        tv_ani_time = findViewById(R.id.tv_ani_time);
        tv_ani_time.setText("0:00");

        //设置显示播放时间
        handler = new Handler(Looper.getMainLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                //正常操作
                super.handleMessage(msg);
                int nowMin = msg.getData().getInt("nowMin");
                int nowSec = msg.getData().getInt("nowSec");
                //处理消息，每秒钟改一次显示时间
                tv_ani_time.setText(nowMin + ":" + nowSec);
//                for (int i=0; i<mApp.getItems().size(); i++) {
//                    Item item = mApp.getItems().get(i);
//                    mPathMeasure = new PathMeasure(item.getActionPath(), false);
//                    mLength = mPathMeasure.getLength();
//                    mPathMeasure.getPosTan(mLength * mAnimatorValue, pos, tan);
//                    if (item.getStartTime().getMinute() == nowMin && item.getStartTime().getSecond() == nowSec) {
//                        //如果到item的开始时间了，就开始运动
//                        bgv.animatorBegin(i);
//                    }
//                }
            }
        };

        //设置导航栏点击返回事件
        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());
        try {
            setLayout();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        btn_play.setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    private void setLayout() throws IOException {
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        TextView tv_toolbar_poem = findViewById(R.id.tv_toolbar_poem);
        //设置toolbar标题诗歌
        tv_toolbar_poem.setText(mApp.getPoem().getTitle());
        //设置toolbar标题为“素材选择”，字体为楷体
        tv_toolbar_title.setText(R.string.action_set);
//        Typeface mtypeface = Typeface.createFromAsset(getAssets(), "font/handwriting.ttf");
//        tv_toolbar_title.setTypeface(mtypeface);
//        tv_toolbar_poem.setTypeface(mtypeface);
        //设置背景为上一个Activity选中的背景
        ((ImageView) findViewById(R.id.bg)).setImageDrawable(mApp.getBackground());
        //设置画上Path的画布
        bgv = new BackgroundView(this);
        //左边每个item设置时间的view
        action_time_views = new ArrayList<View>();
        //添加items
        for (Item item : mApp.getItems()) {
            //Log.d(TAG, item.getItem_id() + "，位置：" + item.getMatrix());
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ImageView iv;
            if (item.getAnimType() == Item.animTypes.HANDDRAW) {
                //创建一个新的ImageView
                //添加上每个Item
                iv = new ImageView(this);
                // 设置bitmap
                iv.setImageDrawable(item.getHandDrawable());
                //设置缩放类型
                iv.setScaleType(ImageView.ScaleType.MATRIX);
                //设置位置和大小
                iv.setImageMatrix(item.getMatrix());
                //若是动画，播放
                if (item.getAnimType() == Item.animTypes.ANIMATION) {
                    // Get the Drawable, which has been compiled to an AnimationDrawable object.
                    AnimationDrawable frameAnimation = (AnimationDrawable) iv.getDrawable();
                    // Start the animation (looped playback by default).
                    frameAnimation.start();
                }
                //把每个item的view放进数组，方便后面运动
                item_views.add(iv);
            } else if (item.getAnimType() == Item.animTypes.GIF) {
                iv = new GifImageView(this);
                GifDrawable gifDrawable = new GifDrawable(getResources(), item.getItem_id());
                iv.setImageDrawable(gifDrawable);
                iv.setId(item.getItem_id());
                gif_v_list.add((GifImageView) iv);
                //设置缩放类型
                iv.setScaleType(ImageView.ScaleType.MATRIX);
                //设置位置和大小
                iv.setImageMatrix(item.getMatrix());
                gifDrawable.start();
            } else if (item.getAnimType() == Item.animTypes.GENEGIF) {
                //创建一个新的ImageView
                //添加上每个Item
                iv = new GifImageView(this);
                // 设置Drawable
                iv.setImageDrawable(item.getGifDrawable());
                gif_v_list.add((GifImageView) iv);
                //设置缩放类型
                iv.setScaleType(ImageView.ScaleType.MATRIX);
                //设置位置和大小
                iv.setImageMatrix(item.getMatrix());
            } else {
                //创建一个新的ImageView
                //添加上每个Item
                iv = new ImageView(this);
                // 设置id
                iv.setId(item.getItem_id());
                //设置图片
                iv.setImageResource(item.getItem_id());
                //设置缩放类型
                iv.setScaleType(ImageView.ScaleType.MATRIX);
                //设置位置和大小
                iv.setImageMatrix(item.getMatrix());
                //若是动画，播放
                if (item.getAnimType() == Item.animTypes.ANIMATION) {
                    // Get the Drawable, which has been compiled to an AnimationDrawable object.
                    AnimationDrawable frameAnimation = (AnimationDrawable) iv.getDrawable();
                    // Start the animation (looped playback by default).
                    frameAnimation.start();
                }
                //把每个item的view放进数组，方便后面运动
                item_views.add(iv);
            }
            bgv.addView(iv, layoutParams);

            //添加每个item的运动路径
            //如果有运动路径，借助BackgroundView
            if (item.getActionPath() != null) {
                Log.d(TAG, "添加一个path");
                bgv.addActionPath(item.getActionPath());
                bgv.addActionView(iv);
                actionItems.add(item);
            }
            ViewGroup.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimenUtils.dp2pxInt(50));
            //左边添加设置开始运动时间的view
            //获取布局文件action_time.xml的根视图
            View action_time_view = LayoutInflater.from(this).inflate(R.layout.action_time, null);
            //把每个view添加到v_list中，方便设置点击事件
            action_time_views.add(action_time_view);
            //获取控件
            TextView tv_item = action_time_view.findViewById(R.id.tv_item);
            Button btn_action_time = action_time_view.findViewById(R.id.btn_action_time);
            ImageButton btn_action_time_icon = action_time_view.findViewById(R.id.btn_action_time_icon);
            //设置值
            tv_item.setText(item.getItem_name());
            //设置Button和ImageButton的点击事件
            btn_action_time.setOnClickListener(v -> {
                Log.d(TAG, "点击");
                // 获取动画总时长
                if (total_time <= 0) {
                    String time__min_str = String.valueOf(et_time_min.getText());
                    String time__sec_str = String.valueOf(et_time_sec.getText());
                    if (time__min_str.equals("0") && time__sec_str.equals("0")) {
                        ToastUtil.show(this, "请先输入动画的总时长！");
                        return;
                    }
                    time_min = Integer.valueOf(time__min_str);
                    time_sec = Integer.valueOf(time__sec_str);
                    total_time = time_min * 60 + time_sec;
                    // 将动画总时长存到mApp中
                    TimeUtil time = new TimeUtil(time_min, time_sec);
                    mApp.setTime(time);
                }
                //获取布局文件action_time_dialog.xml的根视图
                View dialog_view = LayoutInflater.from(this).inflate(R.layout.action_time_dialog, null);
                //设置分和秒选择器的最大最小值
                NumberPicker minute_picker = (NumberPicker) dialog_view.findViewById(R.id.minute_picker);
                minute_picker.setMaxValue(time_min > 0 ? time_min : 0);
                minute_picker.setMinValue(0);
                NumberPicker second_picker = (NumberPicker) dialog_view.findViewById(R.id.second_picker);
                second_picker.setMaxValue(59);
                if (minute_picker.getValue() == minute_picker.getMaxValue()) second_picker.setMaxValue(time_sec > 0 ? time_sec : 0);
                second_picker.setMinValue(0);
                new AlertDialog.Builder(this).setView(dialog_view).setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    btn_action_time.setText(minute_picker.getValue() + "分" + second_picker.getValue() + "秒");
                    item.setStartTime(minute_picker.getValue(), second_picker.getValue());
                }).setNegativeButton(android.R.string.cancel, null).show();
            });
            btn_action_time_icon.setOnClickListener(v -> {
                //跟上面一模一样的
                Log.d(TAG, "点击");
                // 获取动画总时长
                if (total_time <= 0) {
                    String time__min_str = String.valueOf(et_time_min.getText());
                    String time__sec_str = String.valueOf(et_time_sec.getText());
                    if (time__min_str.equals("0") && time__sec_str.equals("0")) {
                        ToastUtil.show(this, "请先输入动画的总时长！");
                        return;
                    }
                    time_min = Integer.valueOf(time__min_str);
                    time_sec = Integer.valueOf(time__sec_str);
                    total_time = time_min * 60 + time_sec;
                    // 将动画总时长存到mApp中
                    TimeUtil time = new TimeUtil(time_min, time_sec);
                    mApp.setTime(time);
                }
                //获取布局文件action_time_dialog.xml的根视图
                View dialog_view = LayoutInflater.from(this).inflate(R.layout.action_time_dialog, null);
                //设置分和秒选择器的最大最小值
                NumberPicker minute_picker = (NumberPicker) dialog_view.findViewById(R.id.minute_picker);
                minute_picker.setMaxValue(time_min > 0 ? time_min : 0);
                minute_picker.setMinValue(0);
                NumberPicker second_picker = (NumberPicker) dialog_view.findViewById(R.id.second_picker);
                second_picker.setMaxValue(59);
                if (minute_picker.getValue() == minute_picker.getMaxValue()) second_picker.setMaxValue(time_sec > 0 ? time_sec : 0);
                second_picker.setMinValue(0);
                new AlertDialog.Builder(this).setView(dialog_view).setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    btn_action_time.setText(minute_picker.getValue() + "分" + second_picker.getValue() + "秒");
                    item.setStartTime(minute_picker.getValue(), second_picker.getValue());
                }).setNegativeButton(android.R.string.cancel, null).show();
            });
            if (item.getActionPath() != null)
                linearLayout.addView(action_time_view, params2);
        }
        rl_bg.addView(bgv, layoutParams);
    }

    private void play() {
        //换relativeLayout上为动态背景
        playFlag = true;
        switchRelativeLayout();
    }

    private void switchRelativeLayout() {
        if (playFlag == false) {
            //未播放，可设置各个action_time_view的值
            for(View view : action_time_views){
                Button btn_action_time = view.findViewById(R.id.btn_action_time);
                ImageButton btn_action_time_icon = view.findViewById(R.id.btn_action_time_icon);
                btn_action_time.setEnabled(true);
                btn_action_time_icon.setEnabled(true);
            }
            //背景上是静态
            bgv.switchStatic(true);
        }else {
            //播放中，不可设置各个action_time_view的值
            for(View view : action_time_views){
                Button btn_action_time = view.findViewById(R.id.btn_action_time);
                ImageButton btn_action_time_icon = view.findViewById(R.id.btn_action_time_icon);
                btn_action_time.setEnabled(false);
                btn_action_time_icon.setEnabled(false);
            }
            //背景上是BackGroundView
            bgv.switchStatic(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_play) {
            ToastUtil.show(this, "请注意，我们要开始播放了，未设置开始运动时间的item将在一开始就运动哦");
            //做好准备等每秒钟给的时间消息并处理
            play();
            //调用播放动画函数
            bgv.setAnimator(actionItems);
            //开始一个计时器
            Thread thread = new Thread() {
                @Override
                public void run() {
                    //当前显示的分钟和秒数
                    int nowMin = -1;
                    int nowSec = -1;
                    while (nowMin < time_min) {
                        nowMin++;
                        int sec = 60;
                        if (nowMin == time_min) sec = time_sec;
                        while (nowSec < sec) {
                            //每一秒钟
                            nowSec++;
                            //发送给处理器去处理,用处理器handler,创建一个空消息对象
                            Message message = handler.obtainMessage();
                            //把值赋给message
                            Bundle bundle = new Bundle();
                            bundle.putInt("nowMin", nowMin);
                            bundle.putInt("nowSec", nowSec);
                            message.setData(bundle);
                            //把消息发送给处理器
                            handler.sendMessage(message);
                            try {
                                //延时是一秒
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            thread.start();
            //播放完毕，返回静态
            if (!thread.isAlive()) {
                ToastUtil.show(this, "播放完成啦！");
                playFlag = false;
                switchRelativeLayout();
            }

        } else if(v.getId() == R.id.btn_next){
            Intent intent = new Intent(this, DubActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}