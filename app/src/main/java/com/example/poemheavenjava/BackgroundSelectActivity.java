package com.example.poemheavenjava;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.poemheavenjava.entity.Poem;
import com.example.poemheavenjava.utils.DimenUtils;
import com.example.poemheavenjava.utils.ToastUtil;
import com.google.android.material.navigation.NavigationView;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBindView;

public class BackgroundSelectActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "lily";
    MyApplication mApp;
    Poem poem;
    private NavigationView nav_view;
    private DrawerLayout drawer_layout;
    private ImageButton btn_nva;
    private TextView tv_toolbar_title;
    private TextView tv_toolbar_poem;
    private ImageView bg;
    private Toolbar toolbar;
    private Button btn_tip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_select);
        //获取MyApplication唯一实例
        mApp = MyApplication.getInstance();
        poem = mApp.getPoem();
        //获取控件
        drawer_layout = findViewById(R.id.drawerLayout);
        btn_nva = findViewById(R.id.btn_nva);
        btn_tip = findViewById(R.id.btn_tip);
        //左侧隐藏的NavigationView布局
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        tv_toolbar_poem = findViewById(R.id.tv_toolbar_poem);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        bg = findViewById(R.id.bg);
        //设置toolbar标题诗歌
        tv_toolbar_poem.setText(poem.getTitle());
        //设置toolbar标题为“背景选择”，字体为楷体
        tv_toolbar_title.setText(R.string.backgroud_select);
//        Typeface mtypeface=Typeface.createFromAsset(getAssets(),"font/handwriting.ttf");
//        tv_toolbar_title.setTypeface(mtypeface);
//        tv_toolbar_poem.setTypeface(mtypeface);
        //设置各种点击事件
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        btn_nva.setOnClickListener(this);
        nav_view.setNavigationItemSelectedListener(this);//左侧nva菜单的Item点击事件钮监听
        btn_tip.setOnClickListener(this);

        //点击选择背景
        findViewById(R.id.bg_1).setOnClickListener(this);
        findViewById(R.id.bg_2).setOnClickListener(this);
        findViewById(R.id.bg_3).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_nva) {
            drawer_layout.openDrawer(GravityCompat.START);//设置左边菜单栏显示出来
        }else if(v.getId() == R.id.btn_next){
            if(mApp.getBackground() == null) {
                ToastUtil.show(this, "请选择一个背景");
                return ;
            }
            Intent intent = new Intent(this, ItemSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (v.getId() == R.id.btn_tip) {
            CustomDialog.build().setCustomView(new OnBindView<CustomDialog>(R.layout.tip) {
                @Override
                public void onBind(CustomDialog dialog, View v) {
                    ((TextView) v.findViewById(R.id.tv_tip)).setText(poem.getBg_tip());
                    ((TextView) v.findViewById(R.id.tv_poem_title)).setText(poem.getTitle());
                    ((TextView) v.findViewById(R.id.tv_poem_wirter)).setText("【"+poem.getDynasty()+"】"+poem.getWriter());
                    String content = poem.getContent().replace("。", "。\r\n");
                    ((TextView) v.findViewById(R.id.tv_poem_content)).setText(content);
                    Button btn_ok = v.findViewById(R.id.btn_tip_ok);
                    btn_ok.setOnClickListener(v1 -> {
                        dialog.dismiss();
                    });
                }
            })
//                    .setBackgroundColor(getResources().getColor(R.color.shallow_black, null))
                    .setMaskColor(Color.parseColor("#00000000"))
                    // 沉浸式非安全区隔离模式，避免底下空出一块
                    .setAutoUnsafePlacePadding(false)
                    .show();
        }else{
            mApp.setBackground(((ImageButton) v).getDrawable());
//            SharedClass.setBg_drawable(((ImageButton) v).getDrawable());
            bg.setImageDrawable(mApp.getBackground());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item1){
            return false;
        } else if(item.getItemId() == R.id.menu_item2){
            if(mApp.getBackground() == null) {
                ToastUtil.show(this, "请选择一个背景");
                return false;
            }
            Intent intent = new Intent();
            intent.setClass(this, ItemSelectActivity.class);
            startActivity(intent);
        }
        return false;
    }
}
