package com.example.poemheavenjava.entity;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.widget.ImageView;

/**
 * 用于一个形象或素材根据其路径运动的辅助类
 * 包含路径测量、初始/变化后矩阵等
 */
public class ItemMove {
    //路经测量类
    private PathMeasure pathMeasure;
    //动画数字，0-1
    private float mAnimatorValue;
    //路径长度
    private float mLength;

    private float[] position;
    private Matrix startMatrix ;//初始矩阵
    private Matrix endMatrix;//变化后的矩阵

    public ItemMove(Path path, ImageView view) {
        pathMeasure = new PathMeasure(path, false);
        mLength = pathMeasure.getLength();
        position = new float[2];
        startMatrix = new Matrix();
        startMatrix.set(view.getImageMatrix());
        endMatrix = new Matrix();
    }

    public PathMeasure getPathMeasure() {
        return pathMeasure;
    }

    public void setPathMeasure(PathMeasure pathMeasure) {
        this.pathMeasure = pathMeasure;
    }

    public float getmAnimatorValue() {
        return mAnimatorValue;
    }

    public void setmAnimatorValue(float mAnimatorValue) {
        this.mAnimatorValue = mAnimatorValue;
    }

    public float getmLength() {
        return mLength;
    }

    public void setmLength(float mLength) {
        this.mLength = mLength;
    }

    public float[] getPosition() {
        return position;
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    public Matrix getStartMatrix() {
        return startMatrix;
    }

    public void setStartMatrix(Matrix startMatrix) {
        this.startMatrix = startMatrix;
    }

    public Matrix getEndMatrix() {
        return endMatrix;
    }

    public void setEndMatrix(Matrix endMatrix) {
        this.endMatrix = endMatrix;
    }
}
