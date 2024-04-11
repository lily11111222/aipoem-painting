package com.example.poemheavenjava.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * author lishilin
 * date 2023/8/9
 * desc 自定义View，实现画板功能
 */
public class DrawingBoardView extends SurfaceView {

    private Paint erasePaint, rectPaint, circlePaint;
    private Path erasePath, selectPath, triangleTowerPath, trianglePath, equilateralPath, anglePath;
    private List<Path> paintPaths, fillPaths, triangleTowerPaths, trianglePaths, equilateralPaths, anglePaths;
    private List<Paint> paints, fillPaints, rectPaints, circlePaints, ovalPaints, triangleTowerPaints, trianglePaints, equilateralPaints, anglePaints;
    private String pathType = "paintPath";
    private boolean eraseMode = false;
    private List<Rect> rects;
    private List<Circle> circleList;
    private List<RectF> ovals;
    private RectF rectF;
    private float startX, startY, currentX, currentY;
    private boolean isDrawing, drawPath, drawRect, drawCircle, isDrawingCircle, isDrawingOval, drawOval, drawTriangleTower, isDrawingTriangleTower, drawTriangle, isDrawingTriangle, isDrawingEquilateral, drawEquilateral, isDrawingRightAngle, drawRightAngle;
    private Rect rect;
    private Circle circle;
    private int pointX, pointY, radius;
    private float tipX, tipY, leftX, leftY, rightX, rightY, equilateralX;

    public DrawingBoardView(Context context) {
        super(context);
        init();
    }

    public DrawingBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DrawingBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        erasePaint = new Paint();
        circle = new Circle();
        rect = new Rect();
        triangleTowerPaints = new ArrayList<>();
        triangleTowerPaths = new ArrayList<>();
        ovals = new ArrayList<>();
        trianglePaints = new ArrayList<>();
        trianglePaths = new ArrayList<>();
        rectPaints = new ArrayList<>();
        ovalPaints = new ArrayList<>();
        circlePaints = new ArrayList<>();
        circleList = new ArrayList<>();
        anglePaints = new ArrayList<>();
        anglePath = new Path();
        anglePaths = new ArrayList<>();
        equilateralPaths = new ArrayList<>();
        equilateralPaints = new ArrayList<>();
        circlePaint = new Paint();
        triangleTowerPath = new Path();
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5);
        erasePath = new Path();
        trianglePath = new Path();
        equilateralPath = new Path();
        triangleTowerPath = new Path();
        paints = new ArrayList<>();
        fillPaints = new ArrayList<>();
        fillPaths = new ArrayList<>();
        paintPaths = new ArrayList<>();
        setPaintColor(Color.BLACK);
        rects = new ArrayList<>();
        rectPaint = new Paint();
        rectPaint.setColor(Color.BLACK);
        rectPaint.setStrokeWidth(5);
        rectPaint.setStyle(Paint.Style.STROKE);
        drawPath = true;
        isDrawing = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = paints.get(paints.size() - 1);
        if (isDrawing) {
            int left = (int) Math.min(startX, currentX);
            int right = (int) Math.max(startX, currentX);
            int top = (int) Math.min(startY, currentY);
            int bottom = (int) Math.max(startY, currentY);
            rect.set(left, top, right, bottom);
            canvas.drawRect(rect, paint);
            isDrawing = false;
        }
        if (!isDrawing) {
            for (int i = 0; i < rects.size(); i++) {
                canvas.drawRect(rects.get(i), rectPaints.get(i));
            }
        }

        for (int i = 0; i < paintPaths.size(); i++) {
            canvas.drawPath(paintPaths.get(i), paints.get(i));
        }

        for (int i = 0; i < trianglePaths.size(); i++) {
            canvas.drawPath(trianglePaths.get(i), trianglePaints.get(i));
        }
        if (isDrawingCircle) {
            circle.setPointX(pointX);
            circle.setPointY(pointY);
            circle.setRadius(radius);
            canvas.drawCircle(circle.getPointX(), circle.getPointY(), circle.getRadius(), paint);
            isDrawingCircle = false;
        }
        if (!isDrawingCircle) {
            for (int i = 0; i < circleList.size(); i++) {
                canvas.drawCircle(circleList.get(i).getPointX(), circleList.get(i).getPointY(), circleList.get(i).getRadius(), circlePaints.get(i));
            }
        }
        if (isDrawingOval) {
            int left = (int) Math.min(startX, currentX);
            int right = (int) Math.max(startX, currentX);
            int top = (int) Math.min(startY, currentY);
            int bottom = (int) Math.max(startY, currentY);
            rectF.set(left, top, right, bottom);
            canvas.drawOval(rectF, paint);
            isDrawingOval = false;
        }
        if (!isDrawingOval) {
            for (int i = 0; i < ovals.size(); i++) {
                canvas.drawOval(ovals.get(i), ovalPaints.get(i));
            }
        }
        if (isDrawingTriangleTower) {
            float leftX = currentX - (currentX - startX) * 2;
            float leftY = currentY;
            triangleTowerPath.lineTo(leftX, leftY);
            triangleTowerPath.lineTo(currentX, currentY);
            triangleTowerPath.close();
            canvas.drawPath(triangleTowerPath, paint);
            isDrawingTriangleTower = false;
        }

        if (!isDrawingTriangleTower){
            for (int i = 0; i < triangleTowerPaths.size(); i++) {
                canvas.drawPath(triangleTowerPaths.get(i), triangleTowerPaints.get(i));
            }
        }
        if (isDrawingTriangle) {
            trianglePath.reset();
            trianglePath.moveTo(tipX, tipY);
            trianglePath.lineTo(leftX, leftY);
            trianglePath.lineTo(rightX, rightY);
            trianglePath.close();
            canvas.drawPath(trianglePath, paint);
            isDrawingTriangle = false;
        }
        if (isDrawingEquilateral) {
            equilateralPath.reset();
            equilateralPath.moveTo(equilateralX, startY);
            int x = (int) ((currentY - startY) / Math.sqrt(3));
            int rightX = (int) (equilateralX + x);
            int leftX = (int) (equilateralX - x);
            equilateralPath.lineTo(leftX, currentY);
            equilateralPath.lineTo(rightX, currentY);
            equilateralPath.close();
            canvas.drawPath(equilateralPath, paint);
            isDrawingEquilateral = false;
        }
        if (!isDrawingEquilateral) {
            for (int i = 0; i < equilateralPaths.size(); i++) {
                canvas.drawPath(equilateralPaths.get(i), equilateralPaints.get(i));
            }
        }
        if (isDrawingRightAngle) {
            anglePath.reset();
            anglePath.moveTo(startX, startY);
            anglePath.lineTo(startX, currentY);
            anglePath.lineTo(currentX, currentY);
            anglePath.close();
            canvas.drawPath(anglePath, paint);
            isDrawingRightAngle = false;
        }
        if (!isDrawingRightAngle) {
            for (int i = 0; i < anglePaths.size(); i++) {
                canvas.drawPath(anglePaths.get(i), anglePaints.get(i));
            }
        }

        for (int i = 0; i < fillPaths.size(); i++) {
            canvas.drawPath(fillPaths.get(i), fillPaints.get(i));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Paint paint = paints.get(paints.size() - 1);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                if (drawPath && selectPath != null) {
                    selectPath.moveTo(x, y);
                }
                if (drawRect) {
                    rect = new Rect();
                    rectPaints.add(paint);
                }
                if (drawCircle) {
                    circle = new Circle();
                    circlePaints.add(paint);
                }
                if (drawOval) {
                    rectF = new RectF();
                    ovalPaints.add(paint);
                }
                if (drawTriangleTower) {
                    triangleTowerPaints.add(paint);
                    triangleTowerPath.moveTo(event.getX(), event.getY());
                }
                if (drawTriangle) {
                    trianglePath = new Path();
                    tipY = startY;
                    leftX = startX;
                    trianglePaints.add(paint);
                }
                if (drawEquilateral) {
                    equilateralPath = new Path();
                    equilateralPaints.add(paint);
                }
                if (drawRightAngle) {
                    anglePath = new Path();
                    anglePaints.add(paint);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                if (drawPath) {
                    selectPath.lineTo(x, y);
                }
                if (drawRect) {
                    isDrawing = true;
                    invalidate();
                }
                if (drawOval) {
                    isDrawingOval = true;
                    invalidate();
                }
                if (drawCircle) {
                    pointX = (int) (startX + (currentX - startX) / 2);
                    pointY = (int) (startY + (currentY - startY) / 2);
                    radius = (int) Math.abs(currentY - startY);
                    isDrawingCircle = true;
                    invalidate();
                }
                if (drawTriangleTower) {
                    isDrawingTriangleTower = true;
                    invalidate();
                }
                if (drawTriangle) {
                    isDrawingTriangle = true;
                    leftY = currentY;
                    tipX = (currentX - leftX) / 2 + leftX;
                    rightX = currentX;
                    rightY = currentY;
                    invalidate();
                }
                if (drawEquilateral) {
                    equilateralX = startX + (currentX - startX) / 2;
                    isDrawingEquilateral = true;
                    invalidate();
                }
                if (drawRightAngle) {
                    isDrawingRightAngle = true;
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
                if (drawRect) {
                    rects.add(rect);
                    isDrawing = false;
                }
                if (drawCircle) {
                    circleList.add(circle);
                    isDrawingCircle = false;
                }
                if (drawOval) {
                    ovals.add(rectF);
                    isDrawingOval = false;
                }
                if (drawTriangleTower) {
                    triangleTowerPaths.add(triangleTowerPath);
                    isDrawingTriangleTower = false;
                }
                if (drawTriangle) {
                    trianglePaths.add(trianglePath);
                    isDrawingTriangle = false;
                }
                if (drawEquilateral) {
                    equilateralPaths.add(equilateralPath);
                    isDrawingEquilateral = false;
                }
                if (drawRightAngle) {
                    anglePaths.add(anglePath);
                    isDrawingRightAngle = false;
                }
                break;
        }
        invalidate();
        return true;
    }

    public void clearBoard() {
        for (int i = 0; i < paintPaths.size(); i++) {
            paintPaths.get(i).reset();
        }
        for (int i = 0; i < fillPaths.size(); i++) {
            fillPaths.get(i).reset();
        }
        for (int i = 0; i < triangleTowerPaths.size(); i++) {
            triangleTowerPaths.get(i).reset();
        }
        for (int i = 0; i < trianglePaths.size(); i++) {
            trianglePaths.get(i).reset();
        }
        for (int i = 0; i < equilateralPaths.size(); i++) {
            equilateralPaths.get(i).reset();
        }
        for (Path path : anglePaths) {
            path.reset();
        }
        rects.clear();
        ovals.clear();
        circleList.clear();
        erasePath.reset();
        eraseMode = false;
        invalidate();
    }

    public void setPaint(String type) {
        switch (type) {
            case "paint":
                selectPath = paintPaths.get(paintPaths.size() - 1);
                this.pathType = "paintPath";
                drawPath = true;
                drawRect = false;
                drawCircle = false;
                drawOval = false;
                drawTriangle = false;
                drawTriangleTower = false;
                drawEquilateral = false;
                drawRightAngle = false;
                break;
            case "fillPaint":
                selectPath = fillPaths.get(fillPaths.size() - 1);
                this.pathType = "fillPath";
                drawPath = true;
                drawRect = false;
                drawCircle = false;
                drawOval = false;
                drawTriangle = false;
                drawTriangleTower = false;
                drawEquilateral = false;
                drawRightAngle = false;
                break;
            case "rect":
                drawPath = false;
                drawRect = true;
                drawCircle = false;
                drawOval = false;
                drawTriangle = false;
                drawTriangleTower = false;
                drawRightAngle = false;
                drawEquilateral = false;
                break;
            case "drawCircle":
                drawRect = false;
                drawPath = false;
                drawOval = false;
                drawCircle = true;
                drawTriangle = false;
                drawTriangleTower = false;
                drawEquilateral = false;
                drawRightAngle = false;
                break;
            case "drawOval":
                drawPath = false;
                drawRect = false;
                drawCircle = false;
                drawOval = true;
                drawTriangle = false;
                drawTriangleTower = false;
                drawEquilateral = false;
                drawRightAngle = false;
                break;
            case "drawTriangleTower":
                drawPath = false;
                drawRect = false;
                drawCircle = false;
                drawOval = false;
                drawTriangle = false;
                drawTriangleTower = true;
                drawEquilateral = false;
                drawRightAngle = false;
                break;
            case "drawTriangle":
                drawPath = false;
                drawRect = false;
                drawCircle = false;
                drawOval = false;
                drawTriangleTower = false;
                drawTriangle = true;
                drawRightAngle = false;
                drawEquilateral = false;
                break;
            case "drawEquilateral":
                drawPath = false;
                drawRect = false;
                drawCircle = false;
                drawOval = false;
                drawTriangleTower = false;
                drawTriangle = false;
                drawRightAngle = false;
                drawEquilateral = true;
                break;
            case "drawRightAngle":
                drawPath = false;
                drawRect = false;
                drawCircle = false;
                drawOval = false;
                drawTriangleTower = false;
                drawTriangle = false;
                drawEquilateral = false;
                drawRightAngle = true;
                break;
        }
    }

    public void setPaintColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(3);
        paints.add(paint);
        Paint fillPaint = new Paint();
        fillPaint.setColor(color);
        fillPaint.setStyle(Paint.Style.STROKE);
        fillPaint.setStrokeWidth(20);
        fillPaint.setAntiAlias(true);
        fillPaint.setDither(true);
        fillPaint.setAlpha(250);
        fillPaint.setStrokeJoin(Paint.Join.ROUND);
        fillPaint.setStrokeCap(Paint.Cap.ROUND);
        fillPaints.add(fillPaint);
        Path path = new Path();
        Path fillPath = new Path();
        Path triangleTowerPath = new Path();
        this.triangleTowerPath = triangleTowerPath;
        this.triangleTowerPaints.add(paint);
        triangleTowerPaths.add(triangleTowerPath);
        Path trianglePath = new Path();
        trianglePaths.add(trianglePath);
        trianglePaints.add(paint);
        fillPaths.add(fillPath);
        paintPaths.add(path);
        Path anglePath = new Path();
        anglePaths.add(anglePath);
        this.anglePath = anglePath;
        this.anglePaints.add(paint);
        Path equilateralPath = new Path();
        this.equilateralPath = equilateralPath;
        equilateralPaths.add(equilateralPath);
        equilateralPaints.add(paint);
        if (pathType.equals("paintPath")) {
            selectPath = path;
        } else {
            selectPath = fillPath;
        }
    }

    public void setEraseMode() {
        eraseMode = true;
        drawPath = true;
        drawRect = false;
        drawTriangleTower = false;
        drawTriangle = false;
        drawCircle = false;
        drawEquilateral = false;
        drawRightAngle = false;
        erasePath = new Path();
        erasePaint.setAntiAlias(true);
        erasePaint.setDither(true);
        erasePaint.setStrokeWidth(30);
        erasePaint.setStyle(Paint.Style.STROKE);
        erasePaint.setStrokeJoin(Paint.Join.ROUND);
        erasePaint.setStrokeCap(Paint.Cap.SQUARE);
        erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        selectPath = erasePath;
    }

    private class Circle {
        int pointX;
        int pointY;
        int radius;

        public int getPointX() {
            return pointX;
        }

        public void setPointX(int pointX) {
            this.pointX = pointX;
        }

        public int getPointY() {
            return pointY;
        }

        public void setPointY(int pointY) {
            this.pointY = pointY;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }
    }

    public Bitmap saveToBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        try {
            if (canvas != null) {
                for (int i = 0; i < paintPaths.size(); i++) {
                    canvas.drawPath(paintPaths.get(i), paints.get(i));
                }

                for (int i = 0; i < trianglePaths.size(); i++) {
                    canvas.drawPath(trianglePaths.get(i), trianglePaints.get(i));
                }
                for (int i = 0; i < circleList.size(); i++) {
                    canvas.drawCircle(circleList.get(i).getPointX(), circleList.get(i).getPointY(), circleList.get(i).getRadius(), circlePaints.get(i));
                }
                for (int i = 0; i < ovals.size(); i++) {
                    canvas.drawOval(ovals.get(i), ovalPaints.get(i));
                }

                for (int i = 0; i < triangleTowerPaths.size(); i++) {
                    canvas.drawPath(triangleTowerPaths.get(i), triangleTowerPaints.get(i));
                }
                for (int i = 0; i < equilateralPaths.size(); i++) {
                    canvas.drawPath(equilateralPaths.get(i), equilateralPaints.get(i));
                }
                for (int i = 0; i < anglePaths.size(); i++) {
                    canvas.drawPath(anglePaths.get(i), anglePaints.get(i));
                }

                for (int i = 0; i < fillPaths.size(); i++) {
                    canvas.drawPath(fillPaths.get(i), fillPaints.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return bitmap;
    }
}

