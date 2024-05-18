package com.example.poemheavenjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.poemheavenjava.entity.AnimatedDrawingResponse;
import com.example.poemheavenjava.utils.AsyncAnimationRequest;
import com.example.poemheavenjava.utils.ToastUtil;
import com.google.gson.Gson;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class AnimatedDrawingActivity extends AppCompatActivity {

    private static String TAG = "AnimatedDrawingActivity";
    private MyApplication mApp = MyApplication.getInstance();
    String userName = "aishihuatest";
    String matName = "testaitem";
    String actionName;
    private Button btn_gene_gif, btn_upload_img, btn_use_gif;
    private ImageView iv_img;
//    private VideoView vv_mv;
    private GifImageView iv_gif;
    private RadioGroup rg_motion;

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;
    private String imagePath, outGIFPath;
    private Uri imageUri;
    Toolbar toolbar;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(@NonNull Message msg) {
            //正常操作
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    TipDialog.show("生成完毕！").setBackgroundColor(getResources().getColor(R.color.shallow_green, null));
                    outGIFPath = String.valueOf(msg.obj);
                    Log.d("lily", "handler:"+outGIFPath);
                    // 放到GifImageView中展示
                    GifDrawable gifDrawable = null;
                    try {
                        gifDrawable = new GifDrawable(outGIFPath);
                        iv_gif.setImageDrawable(gifDrawable);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    btn_use_gif.setEnabled(true);
                    break;
                case 2:
                    TipDialog.show(String.valueOf(msg.obj)).setBackgroundColor(getResources().getColor(R.color.shallow_green, null));
//                    ToastUtil.show(AnimatedDrawingActivity.this, "图片不能带有透明图层哦！");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animated_drawing);

        btn_upload_img = findViewById(R.id.btn_upload_img);
        btn_gene_gif = findViewById(R.id.btn_gene_gif);
        btn_use_gif = findViewById(R.id.btn_use_gif);
        iv_gif = findViewById(R.id.iv_gif);
        iv_img = findViewById(R.id.iv_img);
//        vv_mv = findViewById(R.id.vv_mv);
        rg_motion = findViewById(R.id.rg_motion);

        btn_use_gif.setEnabled(false);

        DialogX.init(AnimatedDrawingActivity.this);

        btn_gene_gif.setOnClickListener(v -> {
            if (imagePath == null || imagePath.isBlank()) {
                ToastUtil.show(this, "请上传一个图片！");
                return ;
            }
            if(actionName == null || actionName.isEmpty()) {
                ToastUtil.show(this, "请选择一个动作！");
                return ;
            }
            asyncRequest();

        });
        btn_upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AnimatedDrawingActivity.this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AnimatedDrawingActivity.this, new String[]{ Manifest.permission. READ_MEDIA_IMAGES }, 1);
                } else {
                    openAlbum();
                }
            }
        });
        rg_motion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb_motion = (RadioButton) findViewById(checkedId);
                String motion_str = rb_motion.getText().toString();
                if(motion_str.equals("作揖")) actionName = "zuoyi";
                else if (motion_str.equals("跳舞")) {
                    actionName = "dance";
                }else if (motion_str.equals("跳一跳")) {
                    actionName = "jump";
                }else if (motion_str.equals("挥手")) {
                    actionName = "wave_hello";
                }
            }
        });
        btn_use_gif.setOnClickListener(v -> {
            GifDrawable drawable = null;
            try {
                drawable = new GifDrawable(outGIFPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            mApp.setGeneGif(drawable);
            Intent intent2 = new Intent(this, ItemSelectActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent2);
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        // 如果是手绘的要转换
        if (intent.hasExtra("staticHand")){
            String staticHand = intent.getStringExtra("staticHand");
            ToastUtil.show(this, staticHand);
            // 放到ImageView中展示
            // 从文件中读取 JPEG 图像并转换为 Bitmap 对象
            Bitmap bitmap = BitmapFactory.decodeFile(staticHand);
            iv_img.setImageBitmap(bitmap);
            iv_img.setBackgroundColor(getColor(R.color.white));
            btn_upload_img.setEnabled(false);
            imagePath = staticHand;
        }

        mApp.setGeneGif(null);
    }

    private void asyncRequest() {
        WaitDialog.show("动作生成中~").setBackgroundColor(getResources().getColor(R.color.shallow_green, null));

        // OkHttp请求对象
        OkHttpClient client = new OkHttpClient();
        // body是一个图片的二进制流，所以格式要用application/octet-stream
        MediaType mediaType = MediaType.Companion.parse("application/octet-stream");
        // 构造请求体
        RequestBody requestBody = RequestBody.Companion.create(mediaType, new File(imagePath));
        // 构造请求的url
        HttpUrl.Builder builder = HttpUrl.parse("http://139.224.214.59:8444/animate/img2anno").newBuilder();
        String fileName = actionName + ".png";
        builder.addPathSegment(userName).addPathSegment(matName).addPathSegment(fileName);
        String url = builder.build().toString();
        // 创建Request对象
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        // 发送异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message message = handler.obtainMessage(2, "生成Annotation请求失败");
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 处理响应
                if(response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Gson解析json数据
                    Gson gson = new Gson();
                    AnimatedDrawingResponse responseMessage = gson.fromJson(responseBody, AnimatedDrawingResponse.class);
                    Log.d(TAG, responseMessage.getMessage());
                    if(responseMessage.getMessage().equals("开始后台任务：img2anno")) {
                        // 每隔1s发送个请求看看生成了没有
                        testGeneAnnoResult();
                    } else {
                        Message message = handler.obtainMessage(2, "服务器生成Annotation出错"+responseMessage.getMessage());
                        handler.sendMessage(message);
                    }
                }else {
//                    Message message = handler.obtainMessage(2, "生成Annotation响应出错"+response.code());
//                    handler.sendMessage(message);
                    Message message = handler.obtainMessage(2, url.toString());
                    handler.sendMessage(message);
                }
            }
        });

    }
    private void testGeneAnnoResult() {
        // OkHttp请求对象
        OkHttpClient client = new OkHttpClient();
        // 构造请求的url
        HttpUrl.Builder builder = HttpUrl.parse("http://139.224.214.59:8444/animate/getAnnoStatus").newBuilder();
        builder.addPathSegment(userName).addPathSegment(matName).addPathSegment(actionName);
        String url = builder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 每隔1秒发送请求看一下生成完了没有
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate((Runnable) () -> {
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    String responseBodyCode = response.body().string();
                    // 0 生成完毕 1 生成中 -1 出错
                    if(responseBodyCode.equals("0")){
                        // 继续根据Annotation生成gif图片
                        geneGif();
                        // 停止定时任务
                        service.shutdown();
                    } else if (responseBodyCode.equals("-1")) {
                        Message message = handler.obtainMessage(2, "服务器生成Annotation出错");
                        handler.sendMessage(message);
                        // 停止定时任务
                        service.shutdown();
                    }
                }
            } catch (IOException e) {
                Message message = handler.obtainMessage(2, "获取生成Annotation状态出现异常");
                handler.sendMessage(message);
                throw new RuntimeException(e);
            }
        }, 0, 1, TimeUnit.SECONDS);// 延迟0秒，每隔1秒执行一次
    }

    private void geneGif() {
        // OkHttp请求对象
        OkHttpClient client = new OkHttpClient();
        // 构造请求的url
        HttpUrl.Builder builder = HttpUrl.parse("http://139.224.214.59:8444/animate/anno2anim").newBuilder();
        builder.addPathSegment(userName).addPathSegment(matName).addPathSegment(actionName);
        String url = builder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 发送异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Message message = handler.obtainMessage(2, "生成gif请求出错");
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // Gson解析json数据
                    Gson gson = new Gson();
                    AnimatedDrawingResponse responseMessage = gson.fromJson(responseBody, AnimatedDrawingResponse.class);
                    Log.d(TAG, responseMessage.getMessage());
                    if(responseMessage.getMessage().equals("开始后台任务：anno2anim")) {
                        // 每隔1s发送个请求看看生成了没有
                        getGeneGif();
                    } else {
                        Message message = handler.obtainMessage(2, "服务器生成GIF出错"+responseMessage.getMessage());
                        handler.sendMessage(message);
                    }
                }else {
                    Message message = handler.obtainMessage(2, "生成gif响应出错");
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void getGeneGif() {
        // OkHttp请求对象
        OkHttpClient client = new OkHttpClient();
        // 构造请求的url
        HttpUrl.Builder builder = HttpUrl.parse("http://139.224.214.59:8444/animate/getExistedGif").newBuilder();
        builder.addPathSegment(userName).addPathSegment(matName).addPathSegment(actionName);
        String url = builder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        // 每隔1秒发送请求看一下生成完了没有
        ScheduledExecutorService service2 = Executors.newScheduledThreadPool(1);
        service2.scheduleAtFixedRate((Runnable) () -> {
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    service2.shutdown();
                    // 将获取到的流写入一个文件
                    File filePath = getFilesDir();
                    String fileName = "animated.gif";
                    File outputFile = new File(filePath, fileName);

                    InputStream inputStream = response.body().byteStream();
                    byte[] bytes = new byte[0];
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        bytes = inputStream.readAllBytes();
                    }
                    //这段代码使用了 Java 7 引入的 try-with-resources 语法，它确保在代码块执行完毕后自动关闭资源，无需手动调用 close() 方法来关闭资源。
                    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                        outputStream.write(bytes);
                    } finally {
                        inputStream.close();
                    }
                    Log.d(TAG, "GIF image saved to: " + outputFile.getAbsolutePath());
                    Message message = handler.obtainMessage(1, outputFile.getAbsolutePath());
                    handler.sendMessage(message);
                }
            } catch (IOException e) {
                Message message = handler.obtainMessage(2, "获取GIF出现异常");
                handler.sendMessage(message);
                throw new RuntimeException(e);
            }
        }, 0, 1, TimeUnit.SECONDS);// 延迟0秒，每隔1秒执行一次



//        // 发送异步请求
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                Message message = handler.obtainMessage(2, "获取gif请求出错");
//                handler.sendMessage(message);
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    // 将获取到的流写入一个文件
//                    File filePath = getFilesDir();
//                    String fileName = "animated.gif";
//                    File outputFile = new File(filePath, fileName);
//
//                    InputStream inputStream = response.body().byteStream();
//                    byte[] bytes = new byte[0];
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//                        bytes = inputStream.readAllBytes();
//                    }
//                    //这段代码使用了 Java 7 引入的 try-with-resources 语法，它确保在代码块执行完毕后自动关闭资源，无需手动调用 close() 方法来关闭资源。
//                    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
//                        outputStream.write(bytes);
//                    } finally {
//                        inputStream.close();
//                    }
//                    Log.d(TAG, "GIF image saved to: " + outputFile.getAbsolutePath());
//                    Message message = handler.obtainMessage(1, outputFile.getAbsolutePath());
//                    handler.sendMessage(message);
//                }else {
//                    Message message = handler.obtainMessage(2, "获取gif响应出错");
//                    handler.sendMessage(message);
//                }
//            }
//        });
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        iv_img.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        imagePath = handleImageOnKitKat(data);
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

    //4.4
    @TargetApi(19)
    private String handleImageOnKitKat(Intent data) {
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
        return imagePath;
    }

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
            Log.d(TAG, imagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            iv_img.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}