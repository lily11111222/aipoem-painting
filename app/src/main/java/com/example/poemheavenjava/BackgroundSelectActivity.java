package com.example.poemheavenjava;

import static com.example.poemheavenjava.DrawingActivity.DRAW_BG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.poemheavenjava.entity.BackgroundImg;
import com.example.poemheavenjava.entity.Poem;
import com.example.poemheavenjava.utils.ToastUtil;
import com.google.android.material.navigation.NavigationView;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.interfaces.OnBindView;

import java.util.ArrayList;
import java.util.List;

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
    private Button btn_tip, btn_local_upload, btn_hand_draw;
    private LinearLayout ll_bgs;
    private Uri imageUri;

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
        ll_bgs = findViewById(R.id.ll_bgs);
        bg = findViewById(R.id.bg);
        btn_local_upload = findViewById(R.id.btn_local_upload);
        btn_hand_draw = findViewById(R.id.btn_hand_draw);
        //左侧隐藏的NavigationView布局
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        tv_toolbar_poem = findViewById(R.id.tv_toolbar_poem);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        //设置toolbar标题诗歌
        tv_toolbar_poem.setText(poem.getTitle());
        //设置toolbar标题为“背景选择”，字体为楷体
        tv_toolbar_title.setText(R.string.backgroud_select);

        //设置各种点击事件
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        btn_nva.setOnClickListener(this);
        nav_view.setNavigationItemSelectedListener(this);//左侧nva菜单的Item点击事件钮监听
        btn_tip.setOnClickListener(this);
        btn_local_upload.setOnClickListener(this);
        btn_hand_draw.setOnClickListener(this);

        setItems();
        //点击选择背景
//        findViewById(R.id.bg_1).setOnClickListener(this);
//        findViewById(R.id.bg_2).setOnClickListener(this);
//        findViewById(R.id.bg_3).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);

        if (mApp.getBackground() != null) {
           bg.setBackground(mApp.getBackground());
        }

    }

    private void setItems() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 清楚下面所有子视图
        ll_bgs.removeAllViews();
        // 请求所有background数据
        List<String> bgToSelect = BackgroundImg.getDefaultBgList();
        List<View> v_list = new ArrayList<>();
        for (String bg_name : bgToSelect) {
            //获取布局文件poem.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.background, null);
            //把每个view添加到v_list中，方便设置点击事件
            v_list.add(view);
            // 获取bg_img的id
            int bg_img_id = getResources().getIdentifier(bg_name, "drawable", this.getPackageName());
            // 获取控件
            ImageButton im_bg = view.findViewById(R.id.ib_bg);
            //为控件设置值
            im_bg.setImageResource(bg_img_id);
            // 按钮设置点击事件
            im_bg.setOnClickListener(v -> {
                mApp.setBackground(im_bg.getDrawable());
                bg.setBackground(im_bg.getDrawable());
                for(View vview : v_list){
                    vview.setBackgroundColor(getResources().getColor(R.color.transparent, null));
                }
                view.setBackgroundColor(getResources().getColor(R.color.grey, null));
            });
            ll_bgs.addView(view, param);
        }
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
        }else if (v.getId() == R.id.btn_local_upload){
            if (ContextCompat.checkSelfPermission(BackgroundSelectActivity.this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BackgroundSelectActivity.this, new String[]{ Manifest.permission. READ_MEDIA_IMAGES }, 1);
            } else {
                openAlbum();
            }
        }else if (v.getId() == R.id.btn_hand_draw){
//            mApp.setBackground(((ImageButton) v).getDrawable());
////            SharedClass.setBg_drawable(((ImageButton) v).getDrawable());
//            bg.setImageDrawable(mApp.getBackground());
            Intent intent = new Intent(this, DrawingActivity.class);
            intent.putExtra("flag", DRAW_BG);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1); // 打开相册
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    ToastUtil.show(this, "You denied the permission");
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    ToastUtil.show(this, "选择完成");
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        //handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    //4.4
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //Log.d("image", imagePath);
        displayImage(imagePath); // 根据图片路径显示图片
    }

//    private void handleImageBeforeKitKat(Intent data) {
//        Uri uri = data.getData();
//        String imagePath = getImagePath(uri, null);
//        displayImage(imagePath);
//    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            bg.setImageBitmap(bitmap);
            mApp.setBackground(bg.getDrawable());
        } else {
            ToastUtil.show(this, "failed to get image");
        }
    }
}
