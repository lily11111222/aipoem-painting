package com.example.poemheavenjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

public class TeachActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    private TextView tv_toolbar_title;
    VideoView vv_teach;
    Button btn_play, btn_pause, btn_resume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        //设置toolbar标题为“背景选择”，字体为楷体
        tv_toolbar_title.setText(R.string.teach);
        toolbar.setNavigationOnClickListener(v -> finish());
        findViewById(R.id.btn_next).setVisibility(View.INVISIBLE);

        vv_teach = findViewById(R.id.vv_teach);
        vv_teach.setVideoURI(Uri.parse("http://47.120.60.166:7070/resources/bab5f7a8-f354-456a-a9cf-df010a3ca7c0.mp4"));
        vv_teach.start();

        btn_play = findViewById(R.id.btn_play);
        btn_pause = findViewById(R.id.btn_pause);
        btn_resume = findViewById(R.id.btn_resume);

        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_resume.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_play)
        {
            if (!vv_teach.isPlaying()) {
                vv_teach.start(); // 开始播放
            }
        }
        if(v.getId() == R.id.btn_pause)
        {
            if (vv_teach.isPlaying()) {
                vv_teach.pause(); // 暂停播放
            }
        }
        if(v.getId() == R.id.btn_resume)
        {
            if (vv_teach.isPlaying()) {
                vv_teach.resume(); // 重新播放
            }
        }
    }
}