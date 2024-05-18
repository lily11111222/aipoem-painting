package com.example.poemheavenjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.poemheavenjava.entity.Item;
import com.example.poemheavenjava.utils.PcmToWavUtil;
import com.example.poemheavenjava.utils.ToastUtil;
import com.example.poemheavenjava.views.BackgroundView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class DubActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "lily";
    private MyApplication mApp;
    private RelativeLayout rl_bg;
    private BackgroundView bgv;
    private LinearLayout.LayoutParams layoutParams;
    private TextView tv_ani_time;
    private List<ImageView> item_views = new ArrayList<>();
    private ArrayList<GifImageView> gif_v_list = new ArrayList<GifImageView>();
    //将要运动的item
    private List<Item> actionItems = new ArrayList<>();
    //录音按钮
    private Button btn_dub, btn_play;
    private Handler handler;
    private Integer time_min, time_sec;
    Thread timeThread;


    private AudioRecord audioRecord;
    // 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
    public static final int SAMPLE_RATE_INHZ = 44100;

    // 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;

    // 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * 录音的工作线程
     */
    private Thread recordingAudioThread;
    private boolean isRecording = false;//mark if is recording
    String wavFilePath;
    String audioCacheFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dub);

        ToastUtil.showIntro(this, 5);
        //获取MyApplication唯一实例
        mApp = MyApplication.getInstance();
        //获取relativeLayout
        rl_bg = findViewById(R.id.rl_bg);
        btn_dub = findViewById(R.id.btn_dub);
        btn_play = findViewById(R.id.btn_play);
        tv_ani_time = findViewById(R.id.tv_ani_time);
        tv_ani_time.setText("0:00");

        btn_play.setEnabled(false);

        //设置显示播放时间
        handler = new Handler(Looper.getMainLooper()){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                //正常操作
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        int nowMin = msg.getData().getInt("nowMin");
                        int nowSec = msg.getData().getInt("nowSec");
                        //处理消息，每秒钟改一次显示时间
                        tv_ani_time.setText(nowMin + ":" + nowSec);
                        break;
                    case 2:
                        stopRecordAudio();
                        saveToWav();
                        if (!isRecording) btn_play.setEnabled(true);
                        try {
                            refresh();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                }
            }
        };

        //设置导航栏点击返回事件
        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        TextView tv_toolbar_poem = findViewById(R.id.tv_toolbar_poem);
        //设置toolbar标题诗歌
        tv_toolbar_poem.setText(mApp.getPoem().getTitle());
        //设置toolbar标题为“录音”，字体为楷体
        tv_toolbar_title.setText(R.string.dub);
        try {
            setLayout();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        btn_dub.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);

        // 获取动画播放时长
        time_min = mApp.getTime().getMinute();
        time_sec = mApp.getTime().getSecond();
    }

    private void refresh() throws IOException {
        rl_bg.removeView(bgv);
        actionItems = new ArrayList<>();
        //设置画上Path的画布
        bgv = null;
        bgv = new BackgroundView(this);
        item_views = new ArrayList<>();
        //添加items
        for (Item item : mApp.getItems()) {
            //Log.d(TAG, item.getItem_id() + "，位置：" + item.getMatrix());
            //layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
                //把每个item的view放进数组，方便后面运动
                item_views.add(iv);
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
                //把每个item的view放进数组，方便后面运动
                item_views.add(iv);
            }  else {
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
        }
        rl_bg.addView(bgv, layoutParams);
    }

    private void setLayout() throws IOException {
//        Typeface mtypeface = Typeface.createFromAsset(getAssets(), "font/handwriting.ttf");
//        tv_toolbar_title.setTypeface(mtypeface);
//        tv_toolbar_poem.setTypeface(mtypeface);
        //设置背景为上一个Activity选中的背景
        ((ImageView) findViewById(R.id.bg)).setImageDrawable(mApp.getBackground());
        //设置画上Path的画布
        bgv = new BackgroundView(this);
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
                //把每个item的view放进数组，方便后面运动
                item_views.add(iv);
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
        }
        rl_bg.addView(bgv, layoutParams);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_next){
            Intent intent = new Intent(this, DubActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if(v.getId() == R.id.btn_dub) {
            // 切换一下动态页面
            bgv.switchStatic(false);
            //开始一个计时器
            timeThread = new Thread() {
                @Override
                public void run() {
                    //当前显示的分钟和秒数
                    int nowMin = -1;
                    int nowSec = -1;
                    while (nowMin < time_min) {
                        nowMin++;
                        int sec = 60;
                        if (nowMin == time_min - 1) sec = time_sec;
                        while (nowSec < time_sec) {
                            //每一秒钟
                            nowSec++;
                            //发送给处理器去处理,用处理器handler,创建一个空消息对象
                            Message message = handler.obtainMessage(1);
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
                    //发送给处理器去处理,用处理器handler,创建一个空消息对象
                    Message message = handler.obtainMessage(2);
                    //把消息发送给处理器
                    handler.sendMessage(message);

                }
            };
            //调用播放动画函数
            bgv.setAnimator(actionItems);
            timeThread.start();
            startRecordAudio();
//            bgv.setAnimator(actionItems);
//            timeThread.start();
//            try {
//                Thread.sleep((time_min * 60 + time_sec) * 1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

            // 没用
            //播放完毕，返回静态
            if (!timeThread.isAlive()) {
                stopRecordAudio();
                saveToWav();
                if (!isRecording) btn_play.setEnabled(true);
            }
        } else if(v.getId() == R.id.btn_play) {
            // 切换一下动态页面
            bgv.switchStatic(false);
            //开始一个计时器
            Thread newTimeThread = new Thread() {
                @Override
                public void run() {
                    //当前显示的分钟和秒数
                    int nowMin = -1;
                    int nowSec = -1;
                    while (nowMin < time_min) {
                        nowMin++;
                        int sec = 60;
                        if (nowMin == time_min - 1) sec = time_sec;
                        while (nowSec < time_sec) {
                            //每一秒钟
                            nowSec++;
                            //发送给处理器去处理,用处理器handler,创建一个空消息对象
                            Message message = handler.obtainMessage(1);
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
            //调用播放动画函数
            bgv.setAnimator(actionItems);
            newTimeThread.start();
            playWavWithMediaPlayer(wavFilePath, mp -> {
                ToastUtil.show(this, "播放结束");
            });
        }
    }

    /**
     * 开始录音，返回临时缓存文件（.pcm）的文件路径
     */
    protected String startRecordAudio() {
        audioCacheFilePath = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + "jerboa_audio_cache.pcm";

        // 先检查一波权限
        // 检查权限是否已授予
        int PermissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if(PermissionState == PackageManager.PERMISSION_GRANTED){
            ToastUtil.show(this, "RECORD_AUDIO已经授予");
        }else if(PermissionState == PackageManager.PERMISSION_DENIED){
            ToastUtil.show(this, "RECORD_AUDIO未授予");
            ActivityCompat.requestPermissions(DubActivity.this, new String[]{ Manifest.permission. RECORD_AUDIO }, 1);
        }

        try{
            // 获取最小录音缓存大小，
            int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
            this.audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

            // 开始录音
            this.isRecording = true;
            audioRecord.startRecording();

            // 创建数据流，将缓存导入数据流
            recordingAudioThread = new Thread(() -> {
                File file = new File(audioCacheFilePath);
                Log.i(TAG, "audio cache pcm file path:" + audioCacheFilePath);

                /*
                 *  以防万一，看一下这个文件是不是存在，如果存在的话，先删除掉
                 */
                if (file.exists()) {
                    file.delete();
                }

                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, "临时缓存文件未找到");
                }
                if (fos == null) {
                    return;
                }

                byte[] data = new byte[minBufferSize];
                int read;
                if (fos != null) {
                    while (isRecording && !recordingAudioThread.isInterrupted()) {
                        read = audioRecord.read(data, 0, minBufferSize);
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                fos.write(data);
                                Log.i("audioRecordTest", "写录音数据->" + read);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                try {
                    // 关闭数据流
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            recordingAudioThread.start();
        }
        catch(IllegalStateException e){
            Log.w(TAG,"需要获取录音权限！");
            this.checkIfNeedRequestRunningPermission();
        }
        catch(SecurityException e){
            Log.w(TAG,"需要获取录音权限！");
            this.checkIfNeedRequestRunningPermission();
        }

        return audioCacheFilePath;
    }

    /**
     * 停止录音
     */
    protected void stopRecordAudio(){
        try {
            this.isRecording = false;
            if (this.audioRecord != null) {
                this.audioRecord.stop();
                this.audioRecord.release();
                this.audioRecord = null;
                this.recordingAudioThread.interrupt();
                this.recordingAudioThread = null;
            }
        }
        catch (Exception e){
            Log.w(TAG,e.getLocalizedMessage());
        }
    }

    /**
     * 录音保存成wav文件
     */
    protected void saveToWav() {
        //wav文件的路径放在系统的音频目录下
        wavFilePath = this.getExternalFilesDir(Environment.DIRECTORY_PODCASTS) + "/wav_" + System.currentTimeMillis() + ".wav";
        Log.d(TAG, wavFilePath);
        PcmToWavUtil ptwUtil = new PcmToWavUtil();
        ptwUtil.pcmToWav(audioCacheFilePath, wavFilePath,true);
    }

    /**
     * 使用MediaPlayer播放文件，并且指定一个当播放完成后会触发的监听器
     * @param filePath
     * @param onCompletionListener
     */
    protected void playWavWithMediaPlayer(String filePath, MediaPlayer.OnCompletionListener onCompletionListener){
        //File wavFile = new File(filePath);
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch(Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * 动态获取运行时权限
     */
    protected void checkIfNeedRequestRunningPermission() {
        ToastUtil.show(this, "请允许权限哦");
        ActivityCompat.requestPermissions(DubActivity.this, new String[]{ Manifest.permission. RECORD_AUDIO }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startRecordAudio();
                } else {
                    ToastUtil.show(this, "拒绝麦克风权限将无法使用");
                }
                break;

            default:
        }
    }
}