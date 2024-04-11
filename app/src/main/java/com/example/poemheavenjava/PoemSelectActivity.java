package com.example.poemheavenjava;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.example.poemheavenjava.entity.Poem;
import com.example.poemheavenjava.utils.ToastUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class PoemSelectActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "lily";
    private GridLayout gridlayout;
    private MyApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poem_select);
        ToastUtil.showIntro(this, 1);
        gridlayout = findViewById(R.id.gridlayout);
        //设置toolbar标题为“诗歌选择”，字体为楷体
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setText(R.string.poem_select);
//        Typeface mtypeface=Typeface.createFromAsset(getAssets(),"font/handwriting.ttf");
//        tv_toolbar_title.setTypeface(mtypeface);
        //获得Application的唯一实例
        mApp = MyApplication.getInstance();
        //请求古诗词数据，并展示
        showPoem();
        //设置监听事件
        findViewById(R.id.btn_next).setOnClickListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showPoem() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
        //清楚下面所有子视图
        gridlayout.removeAllViews();
        //请求所有古诗词数据，这里先用本地代替
        ArrayList<Poem> poem_list = Poem.getDefaultList();
        ArrayList<View> v_list = new ArrayList<View>();
        for(Poem poem : poem_list){
            //获取布局文件poem.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.poem, null);
            //把每个view添加到v_list中，方便设置点击事件
            v_list.add(view);
            //获取控件
            TextView tv_poem_title = view.findViewById(R.id.tv_poem_title);
            TextView tv_poem_wirter = view.findViewById(R.id.tv_poem_wirter);
            TextView tv_poem_content = view.findViewById(R.id.tv_poem_content);
            Button btn_poem_select = view.findViewById(R.id.btn_poem_select);
            //为控件设置值
            tv_poem_title.setText(poem.getTitle());
            tv_poem_wirter.setText("【"+poem.getDynasty()+"】"+poem.getWriter());
            String content = poem.getContent().replace("。", "。\r\n");
            tv_poem_content.setText(content);
            //按钮设置监听事件
            btn_poem_select.setOnClickListener(v -> {
                mApp.setPoem(poem);
                for(View vview : v_list){
                    vview.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                }
                view.setBackgroundColor(getResources().getColor(R.color.grey, null));
            });
            //把每一首古诗词的视图添加到网格布局中
            gridlayout.addView(view, params);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_next){
            if(mApp.getPoem() == null) {
                ToastUtil.show(this, "请选择一首诗歌");
                return;
            }
            Intent intent = new Intent(this, BackgroOptionSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}