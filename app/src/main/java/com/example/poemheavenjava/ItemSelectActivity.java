package com.example.poemheavenjava;

import static com.example.poemheavenjava.DrawingActivity.DRAW_ITEM;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.poemheavenjava.entity.Item;
import com.example.poemheavenjava.entity.Poem;
import com.example.poemheavenjava.utils.ToastUtil;
import com.example.poemheavenjava.views.SimpleDoodleView;
import com.google.android.material.navigation.NavigationView;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ItemSelectActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, View.OnTouchListener {
    private static final String TAG = "lily";
    private DrawerLayout drawer_layout;
    private RelativeLayout rl_bg;
    private LinearLayout linear_layout;
    private MyApplication mApp;
    private Poem poem;

    LinearLayout.LayoutParams newParams;
    //能否添加下一个item
    private Boolean flag = true;
    Button btn_setroute;
    Button btn_static;
    private SimpleDoodleView sdv;
    private ImageView rv_delete;
//    private Button btn_delete;
    private Button btn_tip;

    private Spinner spinner_type;
    private Spinner spinner_static;
    ArrayList<ImageView> v_list = new ArrayList<ImageView>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_select);
        Log.d(TAG, "3");
        //获取MyApplication唯一实例
        mApp = MyApplication.getInstance();
        if (mApp.getHandDrawItem() == null) ToastUtil.showIntro(this, 3);
        poem = mApp.getPoem();
        //获取控件
        drawer_layout = findViewById(R.id.drawerLayout);
        linear_layout = findViewById(R.id.linear_layout);
        rl_bg = findViewById(R.id.rl_bg);
        btn_setroute = findViewById(R.id.btn_setroute);
        btn_static = findViewById(R.id.btn_static);
        rv_delete = findViewById(R.id.rv_delete);
//        btn_delete = findViewById(R.id.btn_delete);
        btn_tip = findViewById(R.id.btn_tip);
        spinner_type = findViewById(R.id.spinner_type);
        spinner_static = findViewById(R.id.spinner_static_or_active);

        try {
            setLayout();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setRouteButtonNotValid();
        //设置各种点击事件
        ((Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());
        ((ImageButton) findViewById(R.id.btn_nva)).setOnClickListener(this);
        //左侧隐藏的NavigationView布局，设置点击事件
        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);//左侧nva菜单的Item点击事件钮监听
        //设置右上角跳往下一个activity的监听事件
        findViewById(R.id.btn_next).setOnClickListener(this);
//        btn_delete.setOnClickListener(this);
        btn_tip.setOnClickListener(this);
        findViewById(R.id.btn_hand_draw).setOnClickListener(this);
        findViewById(R.id.btn_local_upload).setOnClickListener(this);

//        rl_bg.removeViewAt(rl_bg.getChildCount() - 1);
        // 设置下拉框选择
        setSpinner();
    }

    private void setRouteButtonNotValid() {
        if (flag == true) {
            //能添加item
            btn_static.setBackgroundColor(getColor(R.color.transparent));
            btn_setroute.setBackgroundColor(getColor(R.color.transparent));
            btn_setroute.setEnabled(false);
            btn_static.setEnabled(false);
            sdv.setEnabled(false);
        } else {
            //不能添加item，即画运动路径时
            btn_static.setBackgroundColor(getColor(R.color.grey));
            btn_setroute.setBackgroundColor(getColor(R.color.grey));
            btn_setroute.setEnabled(true);
            btn_static.setEnabled(true);
            sdv.setEnabled(true);
        }
    }

    private void setLayout() throws IOException {
        newParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        TextView tv_toolbar_poem = findViewById(R.id.tv_toolbar_poem);
        //设置toolbar标题诗歌
        tv_toolbar_poem.setText(poem.getTitle());
        //设置toolbar标题为“素材选择”，字体为楷体
        tv_toolbar_title.setText(R.string.item_select);
//        Typeface mtypeface = Typeface.createFromAsset(getAssets(), "font/handwriting.ttf");
//        tv_toolbar_title.setTypeface(mtypeface);
//        tv_toolbar_poem.setTypeface(mtypeface);
        //设置背景为上一个Activity选中的背景
        ((ImageView) findViewById(R.id.bg)).setImageDrawable(mApp.getBackground());
        //加上画布，但要设置不可见
        sdv = new SimpleDoodleView(this);
        sdv.setEnabled(false);
        //开启能够调整子View的顺序
        rl_bg.addView(sdv, newParams);
        getHandDrawItem();
        getGeneGif();
        setItems();
    }

    private void getHandDrawItem() {
        if (mApp.getHandDrawItem() == null) return;
        ImageView handDraw = new ImageView(this);
        handDraw.setImageBitmap(mApp.getHandDrawItem());
        //设置图片的位置
        handDraw.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //设置播放
        //设置监听事件
        handDraw.setOnClickListener(v -> {
            if (flag == false){
                ToastUtil.show(this, "请先画出其运动路径，再添加下一个素材");
                return;
            }
            flag = false;
            //设置”画运动路径“等按钮可用
            setRouteButtonNotValid();
            //在背景中添加一个新的view
            ImageView copy_view = new ImageView(this);
            copy_view.setImageDrawable(handDraw.getDrawable());
            copy_view.setOnTouchListener(this);
            //添加新视图
            rl_bg.addView(copy_view, newParams);
            Item item = new Item();
            item.setAnimType(Item.animTypes.HANDDRAW);
            item.setHandDrawable(handDraw.getDrawable());
            item.setItem_name("手绘素材");
            setRoute(item, copy_view);
            mApp.addAItem(item);
        });
        linear_layout.addView(handDraw,newParams);
    }

    private void getGeneGif() {
        if (mApp.getGeneGif() == null) return;
        GifImageView gif_iv = new GifImageView(this);
        gif_iv.setImageDrawable(mApp.getGeneGif());
        //设置图片的位置
        gif_iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //设置播放
        //设置监听事件
        gif_iv.setOnClickListener(v -> {
            if (flag == false){
                ToastUtil.show(this, "请先画出其运动路径，再添加下一个素材");
                return;
            }
            flag = false;
            //设置”画运动路径“等按钮可用
            setRouteButtonNotValid();
            //在背景中添加一个新的view
            GifImageView new_gif_v = new GifImageView(this);
            new_gif_v.setImageDrawable(mApp.getGeneGif());
            new_gif_v.setOnTouchListener(this);
            //添加新视图
            rl_bg.addView(new_gif_v, newParams);
            Item item = new Item();
            item.setAnimType(Item.animTypes.GENEGIF);
            item.setGifDrawable(mApp.getGeneGif());
            item.setItem_name("生成GIF素材");
            setRoute(item, new_gif_v);
            mApp.addAItem(item);
        });
       //把每一首item的视图添加到网格布局中
        linear_layout.addView(gif_iv, newParams);
    }

    private void setItems() throws IOException {
        //获取参数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.item_height));
        //请求所有items数据，这里先用本地代替
        ArrayList<Item> item_list = Item.getDefaultList();
        ArrayList<GifImageView> gif_v_list = new ArrayList<GifImageView>();
        for(Item item : item_list){
            //获取item_id
            int item_id = getResources().getIdentifier(item.getItem_name(), "drawable", this.getPackageName());
            if (item.getAnimType() != Item.animTypes.GIF) {
                //获取布局文件poem.xml的根视图
                ImageView imageView = new ImageView(this);
                //把每个view添加到v_list中，方便设置点击事件
                v_list.add(imageView);
                //为控件设置值
                imageView.setImageResource(item_id);
                //设置图片的位置
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //设置播放
                //设置监听事件
                imageView.setOnClickListener(v -> {
                    if (flag == false){
                        ToastUtil.show(this, "请先画出其运动路径，再添加下一个素材");
                        return;
                    }
                    flag = false;
                    //设置”画运动路径“等按钮可用
                    setRouteButtonNotValid();
                    //在背景中添加一个新的view
                    ImageView copy_view = new ImageView(this);
                    copy_view.setImageResource(item_id);
                    copy_view.setOnTouchListener(this);
                    //添加新视图
                    rl_bg.addView(copy_view, newParams);
                    item.setItem_id(item_id);
                    setRoute(item, copy_view);
                    mApp.addAItem(item);
                });
                if (item.getAnimType() == Item.animTypes.ANIMATION) {
                    // Get the Drawable, which has been compiled to an AnimationDrawable object.
                    AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getDrawable();
                    // Start the animation (looped playback by default).
                    frameAnimation.start();
                }
                //把每一首item的视图添加到网格布局中
                linear_layout.addView(imageView, params);
            } else {
                GifImageView gif_iv = new GifImageView(this);
                GifDrawable gifDrawable = new GifDrawable(getResources(), item_id);
                gif_iv.setImageDrawable(gifDrawable);
                gif_v_list.add(gif_iv);
                //设置图片的位置
                gif_iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //设置播放
                //设置监听事件
                gif_iv.setOnClickListener(v -> {
                    if (flag == false){
                        ToastUtil.show(this, "请先画出其运动路径，再添加下一个素材");
                        return;
                    }
                    flag = false;
                    //设置”画运动路径“等按钮可用
                    setRouteButtonNotValid();
                    //在背景中添加一个新的view
                    GifImageView new_gif_v = new GifImageView(this);
                    new_gif_v.setImageDrawable(gifDrawable);
                    new_gif_v.setOnTouchListener(this);
                    //添加新视图
                    rl_bg.addView(new_gif_v, newParams);
                    item.setItem_id(item_id);
                    setRoute(item, new_gif_v);
                    mApp.addAItem(item);
                });
                gifDrawable.start();
                //把每一首item的视图添加到网格布局中
                linear_layout.addView(gif_iv, params);
            }
        }
    }

    //画出路线
    public void setRoute(Item item, ImageView iv) {
        setRouteButtonNotValid();
        //选择是静态素材
        btn_static.setOnClickListener(v -> {
            iv.setEnabled(false);
            flag = true;
            setRouteButtonNotValid();
        });
        //画运动路径
        btn_setroute.setOnClickListener(v -> {
            if (btn_setroute.getText().equals("画运动路径")) {
                btn_setroute.setText("画好了！");
                iv.setEnabled(false);
                sdv.setEnabled(true);
                //新视图放进去的位置
//                rl_bg.addView(sdv, newParams);
            }else {
                sdv.savePath(item);
                //rl_bg.removeView(sdv);
                flag = true;
                setRouteButtonNotValid();
                btn_setroute.setText("画运动路径");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_nva) {
            drawer_layout.openDrawer(GravityCompat.START);//设置左边菜单栏显示出来
        } else if (v.getId() == R.id.btn_next) {
            if (btn_setroute.isEnabled()) {
                ToastUtil.show(this, "请先画出其运动路径，再进入下一个页面");
                return;
            }
            Intent intent = new Intent();
            intent.setClass(this, SetActionTimeActivity.class);
            startActivity(intent);
//        } else if (v.getId() == R.id.btn_delete){
//            ToastUtil.show(this, "删除咯！");
//            rl_bg.removeViewAt(rl_bg.getChildCount() - 1);
//            mApp.removeLastItem();
//            sdv.removeLastPath();
//            flag = true;
//            setRouteButtonNotValid();
//            btn_setroute.setText("画运动路径");
        }else if (v.getId() == R.id.btn_tip) {
            CustomDialog.build().setCustomView(new OnBindView<CustomDialog>(R.layout.tip) {
                        @Override
                        public void onBind(CustomDialog dialog, View v) {
                            ((TextView) v.findViewById(R.id.tv_tip)).setText(poem.getItem_tip());
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
        } else if (v.getId() == R.id.btn_hand_draw){
//            mApp.setBackground(((ImageButton) v).getDrawable());
////            SharedClass.setBg_drawable(((ImageButton) v).getDrawable());
//            bg.setImageDrawable(mApp.getBackground());
            Intent intent = new Intent(this, DrawingActivity.class);
            intent.putExtra("flag", DRAW_ITEM);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_local_upload) {
            Intent intent = new Intent(this, AnimatedDrawingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    //单指拖拽、双指放缩所需
    private int MODE;//当前状态
    private Matrix startMatrix = new Matrix();//初始矩阵
    private Matrix endMatrix = new Matrix();//变化后的矩阵
    private PointF startPointF = new PointF();//初始坐标
    private float distance;//初始距离
    private float scaleMultiple;//缩放倍数
    //单指拖拽、双指放缩所需
    public static final int MODE_NONE = 0;//无操作
    public static final int MODE_DRAG = 1;//单指操作
    public static final int MODE_SCALE = 2;//双指操作
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN://单指触碰

                //设置缩放类型
                ((ImageView) v).setScaleType(ImageView.ScaleType.MATRIX);
                Log.d(TAG, "单指摸到了");
                //起始矩阵先获取ImageView的当前状态
                startMatrix.set(((ImageView) v).getImageMatrix());
                //获取起始坐标
                startPointF.set(event.getX(), event.getY());
                //此时状态是单指操作
                MODE = MODE_DRAG;

                break;
            case MotionEvent.ACTION_POINTER_DOWN://双指触碰
//                Log.d(TAG, "双指摸到了");
                //最后的状态传给起始状态
                startMatrix.set(endMatrix);
                //获取距离
                distance = getDistance(event);
                //状态改为双指操作
                MODE = MODE_SCALE;

                break;
            case MotionEvent.ACTION_MOVE://滑动（单+双）
                if (MODE == MODE_DRAG) {//单指滑动时
//                    Log.d(TAG, "单指滑动");
                    //获取初始矩阵
                    endMatrix.set(startMatrix);
                    //向矩阵传入位移距离
                    endMatrix.postTranslate(event.getX() - startPointF.x, event.getY() - startPointF.y);
                    //删除操作
                    if (event.getX() >= rv_delete.getX() && event.getX() <= rv_delete.getX()+rv_delete.getWidth()
                    && event.getY() >= rv_delete.getY() && event.getY() <= rv_delete.getY()+rv_delete.getHeight()) {
                        ToastUtil.show(this, "松手删除哦！");
                    }
                } else if (MODE == MODE_SCALE) {//双指滑动时
//                    Log.d(TAG, "双指滑动");
                    //计算缩放倍数
                    scaleMultiple = getDistance(event) / distance;
                    //获取初始矩阵
                    endMatrix.set(startMatrix);
                    //向矩阵传入缩放倍数
                    endMatrix.postScale(scaleMultiple, scaleMultiple, startPointF.x, startPointF.y);
                }
                break;
            case MotionEvent.ACTION_UP://单指离开
            case MotionEvent.ACTION_POINTER_UP://双指离开
                Log.d(TAG, "走了");
                //手指离开后，重置状态
                MODE = MODE_NONE;
                mApp.setMatrix(endMatrix);
                //事件结束后，把矩阵的变化同步到ImageView上
                ((ImageView) v).setImageMatrix(endMatrix);
                //删除操作
                if (event.getX() >= rv_delete.getX() && event.getX() <= rv_delete.getX()+rv_delete.getWidth()
                        && event.getY() >= rv_delete.getY() && event.getY() <= rv_delete.getY()+rv_delete.getHeight()) {
                    ToastUtil.show(this, "删除咯！");
                    rl_bg.removeView(v);
                    mApp.removeLastItem();
                    flag = true;
                    setRouteButtonNotValid();
                    btn_setroute.setText("画运动路径");
                }
                break;
        }
        return true;
    }

    //获取距离
    private static float getDistance(MotionEvent event) {//获取两点间距离
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 展示某种种类的素材
    private void setTypeItems(List<Integer> typeItems) throws IOException {
        linear_layout.removeAllViews();
        //获取参数
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.item_height));
        //请求所有items数据，这里先用本地代替
        ArrayList<Item> item_list = Item.getDefaultTypeList(typeItems);
        v_list = new ArrayList<ImageView>();
        ArrayList<GifImageView> gif_v_list = new ArrayList<GifImageView>();
        for(Item item : item_list){
            //获取item_id
            int item_id = getResources().getIdentifier(item.getItem_name(), "drawable", this.getPackageName());
            if (item.getAnimType() != Item.animTypes.GIF) {
                //获取布局文件poem.xml的根视图
                ImageView imageView = new ImageView(this);
                //把每个view添加到v_list中，方便设置点击事件
                v_list.add(imageView);
                //为控件设置值
                imageView.setImageResource(item_id);
                //设置图片的位置
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //设置播放
                //设置监听事件
                imageView.setOnClickListener(v -> {
                    if (flag == false){
                        ToastUtil.show(this, "请先画出其运动路径，再添加下一个素材");
                        return;
                    }
                    flag = false;
                    //设置”画运动路径“等按钮可用
                    setRouteButtonNotValid();
                    //在背景中添加一个新的view
                    ImageView copy_view = new ImageView(this);
                    copy_view.setImageResource(item_id);
                    copy_view.setOnTouchListener(this);
                    //添加新视图
                    rl_bg.addView(copy_view, newParams);
                    item.setItem_id(item_id);
                    setRoute(item, copy_view);
                    mApp.addAItem(item);
                });
                if (item.getAnimType() == Item.animTypes.ANIMATION) {
                    // Get the Drawable, which has been compiled to an AnimationDrawable object.
                    AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getDrawable();
                    // Start the animation (looped playback by default).
                    frameAnimation.start();
                }
                //把每一首item的视图添加到网格布局中
                linear_layout.addView(imageView, params);
            } else {
                GifImageView gif_iv = new GifImageView(this);
                GifDrawable gifDrawable = new GifDrawable(getResources(), item_id);
                gif_iv.setImageDrawable(gifDrawable);
                gif_v_list.add(gif_iv);
                //设置图片的位置
                gif_iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //设置播放
                //设置监听事件
                gif_iv.setOnClickListener(v -> {
                    if (flag == false){
                        ToastUtil.show(this, "请先画出其运动路径，再添加下一个素材");
                        return;
                    }
                    flag = false;
                    //设置”画运动路径“等按钮可用
                    setRouteButtonNotValid();
                    //在背景中添加一个新的view
                    GifImageView new_gif_v = new GifImageView(this);
                    new_gif_v.setImageDrawable(gifDrawable);
                    new_gif_v.setOnTouchListener(this);
                    //添加新视图
                    rl_bg.addView(new_gif_v, newParams);
                    item.setItem_id(item_id);
                    setRoute(item, new_gif_v);
                    mApp.addAItem(item);
                });
                gifDrawable.start();
                //把每一首item的视图添加到网格布局中
                linear_layout.addView(gif_iv, params);
            }
        }
    }

    private List<Integer> type;
    private int sta_dy_flag = ALL_TYPE;
    private int type_flag = ALL_TYPE;
    public static final int ALL_TYPE = 0;
    public static final int STATIC_TYPE = 1;
    public static final int DYNAMIC_TYPE = 2;
    public static final int PEOPLE_TYPE = 3;
    public static final int ANIMAL_TYPE = 4;
    public static final int PLANT_TYPE = 5;
    public static final int BUILD_TYPE = 6;
    public static final int HAND_DRAW = 7;
    public static boolean hand_gene_show = true;
    // 设置下拉框
    private void setSpinner() {
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type_selected = getResources().getStringArray(R.array.spinner_types)[position];
                switch (type_selected) {
                    case "全部素材":
                        type_flag = ALL_TYPE;
                        break;
                    case "人物素材":
                        type_flag = PEOPLE_TYPE;
                        break;
                    case "动物素材":
                        type_flag = ANIMAL_TYPE;
                        break;
                    case "植物素材":
                        type_flag = PLANT_TYPE;
                        break;
                    case "建筑/景色素材":
                        type_flag = BUILD_TYPE;
                        break;
                    case "手绘/上传素材":
                        type_flag = HAND_DRAW;
                }
                getItems();
                try {
                    setTypeItems(type);
                    if (hand_gene_show) {
                        getHandDrawItem();
                        getGeneGif();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type_flag = ALL_TYPE;
                getItems();
                try {
                    setTypeItems(type);
                    if (hand_gene_show) {
                        getHandDrawItem();
                        getGeneGif();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        spinner_static.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sta_dy_selected = getResources().getStringArray(R.array.static_active)[position];
                switch (sta_dy_selected) {
                    case "静态":
                        sta_dy_flag = STATIC_TYPE;
                        break;
                    case "动态":
                        sta_dy_flag = DYNAMIC_TYPE;
                        break;
                }
                getItems();
                try {
                    setTypeItems(type);
                    if (hand_gene_show) {
                        getHandDrawItem();
                        getGeneGif();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sta_dy_flag = ALL_TYPE;
                getItems();
                try {
                    setTypeItems(type);
                    if (hand_gene_show) {
                        getHandDrawItem();
                        getGeneGif();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void getItems() {
        type = new ArrayList<>();
        if (type_flag == HAND_DRAW) {
            type = new ArrayList<>();
            hand_gene_show = true;
        } else {
            if (sta_dy_flag == ALL_TYPE) {
                switch (type_flag) {
                    case ALL_TYPE:
                        type = Item.getAll_items();
                        hand_gene_show = true;
                        break;
                    case PEOPLE_TYPE:
                        type.addAll(Item.getPeople_active_items());
                        type.addAll(Item.getPeople_static_items());
                        break;
                    case ANIMAL_TYPE:
                        type.addAll(Item.getAnimal_active_items());
                        type.addAll(Item.getAnimal_static_items());
                        break;
                    case PLANT_TYPE:
                        type.addAll(Item.getPlant_items());
                        break;
                    case BUILD_TYPE:
                        type.addAll(Item.getOther_items());
                        break;
                }
            } else if (sta_dy_flag == STATIC_TYPE) {
                switch (type_flag) {
                    case ALL_TYPE:
                        type.addAll(Item.getPeople_static_items());
                        type.addAll(Item.getAnimal_static_items());
                        type.addAll(Item.getPlant_items());
                        type.addAll(Item.getOther_items());
                        hand_gene_show = true;
                        break;
                    case PEOPLE_TYPE:
                        type.addAll(Item.getPeople_static_items());
                        break;
                    case ANIMAL_TYPE:
                        type.addAll(Item.getAnimal_static_items());
                        break;
                    case PLANT_TYPE:
                        type.addAll(Item.getPlant_items());
                        break;
                    case BUILD_TYPE:
                        type.addAll(Item.getOther_items());
                        break;
                }
            } else if (sta_dy_flag == DYNAMIC_TYPE) {
                switch (type_flag) {
                    case ALL_TYPE:
                        type.addAll(Item.getPeople_active_items());
                        type.addAll(Item.getAnimal_active_items());
                        break;
                    case PEOPLE_TYPE:
                        type.addAll(Item.getPeople_active_items());
                        break;
                    case ANIMAL_TYPE:
                        type.addAll(Item.getAnimal_active_items());
                        break;
                    case PLANT_TYPE:
                        break;
                    case BUILD_TYPE:
                        break;
                }
            }
        }
    }

}