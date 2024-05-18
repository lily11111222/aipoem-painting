package com.example.poemheavenjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.example.poemheavenjava.utils.AsyncAnimationRequest;
import com.example.poemheavenjava.utils.ToastUtil;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.TipDialog;
import com.kongzue.dialogx.dialogs.WaitDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.InputMismatchException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class AnimationRequestActivity extends AppCompatActivity {
    private static String TAG = "AsyncAnimationRequest";
    private Button btn_gene_gif, btn_upload_img, btn_upload_mv, btn_use_gif;
    private ImageView iv_img;
    private VideoView vv_mv;
    private GifImageView iv_gif;

    public static final int TAKE_PHOTO = 1;

    public static final int CHOOSE_PHOTO = 2;
    public static final int REQUEST_SELECT_VIDEO = 3;

    private String imagePath, videoPath;
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
                    String outGIFPath = String.valueOf(msg.obj);
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
                    TipDialog.show("生成失败！").setBackgroundColor(getResources().getColor(R.color.shallow_green, null));
                    ToastUtil.show(AnimationRequestActivity.this, "图片不能带有透明图层哦！");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_request);

        btn_upload_img = findViewById(R.id.btn_upload_img);
        btn_upload_mv = findViewById(R.id.btn_upload_mv);
        btn_gene_gif = findViewById(R.id.btn_gene_gif);
        btn_use_gif = findViewById(R.id.btn_use_gif);
        iv_gif = findViewById(R.id.iv_gif);
        iv_img = findViewById(R.id.iv_img);
        vv_mv = findViewById(R.id.vv_mv);

        vv_mv.setVisibility(View.INVISIBLE);
        btn_use_gif.setEnabled(false);

        DialogX.init(AnimationRequestActivity.this);

        btn_gene_gif.setOnClickListener(v -> {
            if (imagePath == null || imagePath.isBlank()) {
                ToastUtil.show(this, "请上传一个图片！");
                return ;
            }
            if (videoPath == null || videoPath.isBlank()) {
                ToastUtil.show(this, "请上传一个视频！");
                return;
            }
            asyncRequest();

        });
        btn_upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AnimationRequestActivity.this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AnimationRequestActivity.this, new String[]{ Manifest.permission. READ_MEDIA_IMAGES }, 1);
                } else {
                    openAlbum();
                }
            }
        });
        btn_upload_mv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AnimationRequestActivity.this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AnimationRequestActivity.this, new String[]{ Manifest.permission. READ_MEDIA_VIDEO }, 2);
                } else {
                    openVideo();
                }
            }
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void asyncRequest() {
        WaitDialog.show("动作生成中~").setBackgroundColor(getResources().getColor(R.color.shallow_green, null));
        Thread thread = new Thread() {
            @Override
            public void run() {
                String outGIFPath;
                try {
                    File file = getFilesDir();
                    Log.d("lily", file.toString());
//        String filePath = "/data/data/com.example.poemheavenjava/cache/chaquopy/tmp";
                    outGIFPath = AsyncAnimationRequest.generateAnimatorNoSync(
                            imagePath,
                            videoPath,
                            file);
                    Log.d("lily", outGIFPath);
                    Message message = handler.obtainMessage(1, outGIFPath);
                    handler.sendMessage(message);
                } catch (RuntimeException e) {
                    Message message = handler.obtainMessage(2);
                    handler.sendMessage(message);
                    throw new RuntimeException(e);
                }

            }
        };
        thread.start();
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    private void openVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_SELECT_VIDEO);
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
            case REQUEST_SELECT_VIDEO:
                if (resultCode == RESULT_OK) {
                    vv_mv.setVisibility(View.VISIBLE);
                    // 在VideoView中播放
                    vv_mv.setVideoURI(data.getData());
                    vv_mv.start();
                    videoPath = getVideoPath(data.getData());
                }
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
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openVideo();
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

    // UPDATED!
    public String getVideoPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
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