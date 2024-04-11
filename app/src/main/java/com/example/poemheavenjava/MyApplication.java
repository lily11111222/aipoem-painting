package com.example.poemheavenjava;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import com.example.poemheavenjava.entity.Item;
import com.example.poemheavenjava.entity.Poem;
import com.example.poemheavenjava.utils.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class MyApplication extends Application {

    private static final String TAG = "lily";
    private static MyApplication mApp;
    private Poem poem;
    private Drawable background;
    // items
    private List<Item> items = new ArrayList<Item>();
    // 总动画时长
    private TimeUtil time;
    private Bitmap handDrawItem;


    public static MyApplication getInstance(){
        return mApp;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public Bitmap getHandDrawItem() {
        return handDrawItem;
    }

    public void setHandDrawItem(Bitmap handDrawItem) {
        this.handDrawItem = handDrawItem;
    }

    public TimeUtil getTime() {
        return time;
    }

    public void setTime(TimeUtil time) {
        this.time = time;
    }

    public Poem getPoem() {
        return poem;
    }

    public void setPoem(Poem poem) {
        this.poem = poem;
    }

    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void addItem(int item_id, Item.animTypes animType) {
        // 加入新的item，其矩阵位置暂时不知
        Item item = new Item();
        item.setItem_id(item_id);
        item.setAnimType(animType);
        this.items.add(item);
    }

    public void addAItem(Item item) {
        this.items.add(item);
    }

    public void removeLastItem() {
        this.items.remove(items.size() - 1);
    }

    // 因为每次都是加一个item，确定一个他的大小和位置，所以这个matrix直接设成最后一个item的matrix
    public void setMatrix(Matrix matrix) {
        int item_num = this.items.size();
        this.items.get(item_num-1).setMatrix(matrix);
        Log.d(TAG, "共"+item_num);
        for(Item item : mApp.getItems()){
            Log.d(TAG, item.getItem_id()+"，位置："+item.getMatrix());
        }
    }

    public Item getAItem(int id) {
        for(Item item : items) {
            if (item.getItem_id() == id)
                return item;
        }
        return null;
    }
    public Item getLastItem() {
        if (items.size() > 0)
            return items.get(items.size() - 1);
        return null;
    }

}
