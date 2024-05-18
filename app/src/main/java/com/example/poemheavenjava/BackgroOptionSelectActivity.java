package com.example.poemheavenjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.poemheavenjava.utils.ToastUtil;

public class BackgroOptionSelectActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout ll_back_db, ll_back_ai;
    Toolbar toolbar;
    private TextView tv_toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backgro_option_select);
        ll_back_ai = findViewById(R.id.ll_back_ai);
        ll_back_db = findViewById(R.id.ll_back_db);
        ll_back_db.setOnClickListener(this);
        ll_back_ai.setOnClickListener(this);

        toolbar = findViewById(R.id.toolbar);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        //设置toolbar标题为“背景选择”，字体为楷体
        tv_toolbar_title.setText(R.string.backgroud_option);
        toolbar.setNavigationOnClickListener(v -> finish());
        ToastUtil.showIntro(this, 2);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_back_ai) {
            Intent intent = new Intent(this, AIBackgroundActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (v.getId() == R.id.ll_back_db) {
            Intent intent = new Intent(this, BackgroundSelectActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}