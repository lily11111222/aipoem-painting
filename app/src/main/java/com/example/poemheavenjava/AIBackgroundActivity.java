package com.example.poemheavenjava;

import static com.example.poemheavenjava.utils.XunFeiUtil.parseIatResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.poemheavenjava.entity.Poem;
import com.example.poemheavenjava.utils.ToastUtil;
import com.example.poemheavenjava.utils.XunFeiCallbackListener;
import com.example.poemheavenjava.utils.XunFeiUtil;
import com.iflytek.cloud.RecognizerResult;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.kongzue.dialogx.interfaces.OnBindView;

public class AIBackgroundActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "lily";
    MyApplication mApp;
    Poem poem;
    EditText et_desc;
    Button btn_generate, btn_voice;
    ImageView bg;
    String input;
    private Button btn_tip;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            //正常操作
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    TipDialog.show("生成完毕！").setBackgroundColor(getResources().getColor(R.color.shallow_green, null));
                    String res = String.valueOf(msg.obj);
                    // 从路径加载图片
                    Bitmap bitmap = BitmapFactory.decodeFile(res);
                    // 将 Bitmap 设置到 ImageView 中
                    bg.setImageBitmap(bitmap);
                    // 存到mApp里
                    mApp.setBackground(bg.getDrawable());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aibackground);

        ToastUtil.showIntro(this, 6);
        //获取MyApplication唯一实例
        mApp = MyApplication.getInstance();
        poem = mApp.getPoem();
        //获取控件
        bg = findViewById(R.id.bg);
        et_desc = findViewById(R.id.et_desc);
        btn_generate = findViewById(R.id.btn_generate);
        btn_voice = findViewById(R.id.btn_voice);
        btn_tip = findViewById(R.id.btn_tip);

        findViewById(R.id.btn_next).setOnClickListener(this);
        ((Toolbar)findViewById(R.id.toolbar)).setNavigationOnClickListener(v -> finish());

        TextView tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        TextView tv_toolbar_poem = findViewById(R.id.tv_toolbar_poem);
        //设置toolbar标题诗歌
        tv_toolbar_poem.setText(mApp.getPoem().getTitle());
        //设置toolbar标题，字体为楷体
        tv_toolbar_title.setText(R.string.ai_background);

        // 初始化Python环境
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        // 初始化讯飞语音识别
        XunFeiUtil.initXunFei(AIBackgroundActivity.this);

        btn_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaitDialog.show("图片生成中~").setBackgroundColor(getResources().getColor(R.color.shallow_green, null));
                input = et_desc.getText().toString();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        Python python = Python.getInstance(); // 初始化Python环境
                        PyObject pyObject = python.getModule("w2pF");//"text"为需要调用的Python文件名
                        PyObject res = pyObject.callAttr("world2pic",input);//"sayHello"为需要调用的函数名
                        Message message = handler.obtainMessage(1, res);
                        handler.sendMessage(message);
                    }
                };
                thread.start();
            }
        });
        btn_voice.setOnClickListener(v -> {
            // 检查权限是否已授予
            int PermissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
            if(PermissionState == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "RECORD_AUDIO已经授予",Toast.LENGTH_LONG);
            }else if(PermissionState == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "RECORD_AUDIO未授予",Toast.LENGTH_LONG);
                ActivityCompat.requestPermissions(AIBackgroundActivity.this, new String[]{ Manifest.permission. RECORD_AUDIO }, 1);
            }
            XunFeiUtil.startVoice(AIBackgroundActivity.this, new XunFeiCallbackListener() {
                @Override
                public void onFinish(RecognizerResult results) {
                    String text = XunFeiUtil.parseIatResult(results.getResultString());
                    // 自动填写地址
                    et_desc.append(text);
                }
            });
        });
        btn_tip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_next){
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    XunFeiUtil.startVoice(AIBackgroundActivity.this, new XunFeiCallbackListener() {
                        @Override
                        public void onFinish(RecognizerResult results) {
                            String text = XunFeiUtil.parseIatResult(results.getResultString());
                            // 自动填写地址
                            et_desc.append(text);
                        }
                    });
                } else {
                    Toast.makeText(this, "拒绝麦克风权限将无法使用",Toast.LENGTH_LONG);
                }
                break;

            default:
        }
    }
}