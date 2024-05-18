package com.example.poemheavenjava;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import androidx.appcompat.widget.Toolbar;

import com.example.poemheavenjava.views.DrawingBoardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author lishilin
 * date 2023/8/9
 * desc 负责画板UI交互
 */
public class DrawingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton paintBtn,fillPaintBtn,rubberBtn,rectBtn,circleBtn,ellipticBtn,triangleBtn,triangleTowerBtn,selectedBtn,clearBtn,equilateralBtn,rightAngleBtn;
    private ImageButton blackBtn,whiteBtn,redBtn,blueBtn,grayBtn,cyanBtn,currentColorBtn;
    private DrawingBoardView drawingBoardView;
    private View selectColorBoxView;
    private Button btn_save, btn_animate;
    private Toolbar toolbar;
    private MyApplication mApp = MyApplication.getInstance();

    public static final int DRAW_BG = 1;
    public static final int DRAW_ITEM = 2;
    private static int DRAW_WHAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        // 获取是画背景还是画素材
        Intent intent = getIntent();
        DRAW_WHAT = intent.getIntExtra("flag", 0);

        initView();
        //设置各种点击事件
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        findViewById(R.id.btn_next).setOnClickListener(this);
        mApp.setHandDrawItem(null);
    }

    private void initView(){
        btn_save = findViewById(R.id.btn_save);
        btn_animate = findViewById(R.id.btn_animate);
        paintBtn = findViewById(R.id.paint);
        fillPaintBtn = findViewById(R.id.fillPaint);
        //rubberBtn = findViewById(R.id.rubber);
        rectBtn = findViewById(R.id.rect);
        circleBtn = findViewById(R.id.circle);
        ellipticBtn = findViewById(R.id.elliptic);
        triangleBtn = findViewById(R.id.triangle);
        triangleTowerBtn = findViewById(R.id.triangleTower);
        clearBtn = findViewById(R.id.clear);
        equilateralBtn = findViewById(R.id.equilateralTriangle);
        drawingBoardView = findViewById(R.id.drawView);
        selectColorBoxView = findViewById(R.id.selectColorBox);
        rightAngleBtn = findViewById(R.id.rightAngle);

        blackBtn = findViewById(R.id.blackBtn);
        whiteBtn = findViewById(R.id.shallowBlueBtn);
        redBtn = findViewById(R.id.redBtn);
        blueBtn = findViewById(R.id.shallowPinkBtn);
        grayBtn = findViewById(R.id.greenBtn);
        cyanBtn = findViewById(R.id.BroneBtn);

        paintBtn.setSelected(true);
        selectedBtn = paintBtn;
        blackBtn.setSelected(true);
        currentColorBtn = blackBtn;

        paintBtn.setOnClickListener(this);
        fillPaintBtn.setOnClickListener(this);
        //rubberBtn.setOnClickListener(this);
        rectBtn.setOnClickListener(this);
        circleBtn.setOnClickListener(this);
        ellipticBtn.setOnClickListener(this);
        triangleBtn.setOnClickListener(this);
        triangleTowerBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        blackBtn.setOnClickListener(this);
        whiteBtn.setOnClickListener(this);
        redBtn.setOnClickListener(this);
        blueBtn.setOnClickListener(this);
        grayBtn.setOnClickListener(this);
        equilateralBtn.setOnClickListener(this);
        rightAngleBtn.setOnClickListener(this);
        cyanBtn.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_animate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_save || v.getId() == R.id.btn_next){
            switch (DRAW_WHAT) {
                case DRAW_BG:
                    BitmapDrawable drawable = new BitmapDrawable(createBitmapFromView(drawingBoardView));
                    mApp.setBackground(drawable);
                    //String filePath = save();
                    Intent intent1 = new Intent(this, BackgroundSelectActivity.class);
                    //intent.putExtra("paintBgPath", filePath);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                    break;
                case DRAW_ITEM:
                    Bitmap bitmap = createBitmapFromView(drawingBoardView);
                    mApp.setHandDrawItem(bitmap);
                    Intent intent2 = new Intent(this, ItemSelectActivity.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                    break;
            }

        } else if (v.getId() == R.id.btn_animate) {
            // 生成动作
            Bitmap bitmap = createBitmapFromViewWithWhiteBack(drawingBoardView);
            String filePath = getFilesDir() + "staticHand.jpeg";
            File file = new File(filePath);
            try {
                // 创建文件输出流
                FileOutputStream fos = new FileOutputStream(file);

                // 将 Bitmap 压缩到文件输出流中
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                // 关闭文件输出流
                fos.close();

                // 文件保存成功
                // 此时你可以在 file 对象中找到保存的文件
                Intent intent = new Intent(this, AnimatedDrawingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("staticHand", filePath);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
                // 文件保存失败
            }
        } else if(v.getId() == R.id.paint){
            setSelectBtn(paintBtn);
            drawingBoardView.setPaint("paint");
            selectColorBoxView.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.fillPaint) {
            setSelectBtn(fillPaintBtn);
            drawingBoardView.setPaint("fillPaint");
            selectColorBoxView.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.rect) {
            setSelectBtn(rectBtn);
            selectColorBoxView.setVisibility(View.VISIBLE);
            drawingBoardView.setPaint("rect");
        } else if (v.getId() == R.id.circle) {
            setSelectBtn(circleBtn);
            selectColorBoxView.setVisibility(View.VISIBLE);
            drawingBoardView.setPaint("drawCircle");
        } else if (v.getId() == R.id.elliptic) {
            setSelectBtn(ellipticBtn);
            selectColorBoxView.setVisibility(View.VISIBLE);
            drawingBoardView.setPaint("drawOval");
        } else if (v.getId() == R.id.triangle) {
            setSelectBtn(triangleBtn);
            selectColorBoxView.setVisibility(View.VISIBLE);
            drawingBoardView.setPaint("drawTriangle");
        } else if (v.getId() == R.id.equilateralTriangle) {
            setSelectBtn(equilateralBtn);
            selectColorBoxView.setVisibility(View.VISIBLE);
            drawingBoardView.setPaint("drawEquilateral");
        } else if (v.getId() == R.id.triangleTower) {
            setSelectBtn(triangleTowerBtn);
            selectColorBoxView.setVisibility(View.VISIBLE);
            drawingBoardView.setPaint("drawTriangleTower");
        } else if (v.getId() == R.id.rightAngle) {
            setSelectBtn(rightAngleBtn);
            selectColorBoxView.setVisibility(View.VISIBLE);
            drawingBoardView.setPaint("drawRightAngle");
        } else if (v.getId() == R.id.clear) {
            clearStyle();//设置点击清除图标时的背景闪烁
            selectColorBoxView.setVisibility(View.VISIBLE);
            if (drawingBoardView!=null){
                drawingBoardView.clearBoard();
            }
        } else if (v.getId() == R.id.blackBtn) {
            int blackColor = ContextCompat.getColor(this,R.color.black);
            drawingBoardView.setPaintColor(blackColor);
            setCurrentColorBtn(blackBtn);
        } else if (v.getId() == R.id.shallowPinkBtn) {
            int blueColor = ContextCompat.getColor(this,R.color.shallow_pink);
            drawingBoardView.setPaintColor(blueColor);
            setCurrentColorBtn(blueBtn);
        } else if (v.getId() == R.id.shallowBlueBtn) {
            int whiteColor = ContextCompat.getColor(this,R.color.shallow_blue);
            drawingBoardView.setPaintColor(whiteColor);
            setCurrentColorBtn(whiteBtn);
        } else if (v.getId() == R.id.BroneBtn) {
            int cyanColor = ContextCompat.getColor(this,R.color.brone);
            drawingBoardView.setPaintColor(cyanColor);
            setCurrentColorBtn(cyanBtn);
        } else if (v.getId() == R.id.greenBtn) {
            int grayColor = ContextCompat.getColor(this,R.color.bright_green);
            drawingBoardView.setPaintColor(grayColor);
            setCurrentColorBtn(grayBtn);
        } else if (v.getId() == R.id.redBtn) {
            int redColor = ContextCompat.getColor(this,R.color.shallow_red);
            drawingBoardView.setPaintColor(redColor);
            setCurrentColorBtn(redBtn);
        }
    }

    /**
     * 设置选中的图形button
     * @param selectBtn
     */
    private void setSelectBtn(ImageButton selectBtn){
        if (this.selectedBtn!=null){
            this.selectedBtn.setSelected(false);
        }
        selectBtn.setSelected(true);
        this.selectedBtn = selectBtn;
    }

    private void setCurrentColorBtn(ImageButton currentColorBtn){
        if (this.currentColorBtn!=null){
            this.currentColorBtn.setSelected(false);
        }
        currentColorBtn.setSelected(true);
        this.currentColorBtn = currentColorBtn;
    }
    /**
     * 设置点击清除图标时的样式变化，让其背景变色500毫秒
     */
    private void clearStyle(){
        clearBtn.setSelected(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                clearBtn.setSelected(false);
            }
        }, 200);
    }

    private String save(){
        

        // 保存自定义View为Bitmap
        Bitmap bitmap = createBitmapFromView(drawingBoardView);

        // 保存Bitmap到文件
        saveBitmapToFile(bitmap, "drawing_bitmap.jpg");
        return "/data/data/com.example.poemheavenjava/cache/drawing_bitmap.png";
    }

    private Bitmap createBitmapFromView(DrawingBoardView view) {
//        SurfaceHolder holder = view.getHolder();
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = holder.lockCanvas();
//        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
//        canvas.drawBitmap(bitmap, 0, 0, null);
//        holder.unlockCanvasAndPost(canvas);
        return view.saveToBitmap();
    }
    private Bitmap createBitmapFromViewWithWhiteBack(DrawingBoardView view) {
//        SurfaceHolder holder = view.getHolder();
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = holder.lockCanvas();
//        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
//        canvas.drawBitmap(bitmap, 0, 0, null);
//        holder.unlockCanvasAndPost(canvas);
        return view.saveToBitmapWithWhiteBack();
    }

    private void saveBitmapToFile(Bitmap bitmap, String fileName) {
        File file = new File("/data/data/com.example.poemheavenjava/cache/", fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
