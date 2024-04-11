package com.example.poemheavenjava.entity;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.poemheavenjava.utils.TimeUtil;
import com.example.poemheavenjava.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Item {
    private final static String TAG = "lily";
    private Integer item_id;
    private String item_name;
    // 在布局中的位置和大小
    private Matrix matrix = new Matrix();
    public static enum animTypes {STATIC, ANIMATION, GIF, HANDDRAW};
    // 是否是动态的
    private animTypes animType ;
    // 活动轨迹
    private Path actionPath;
    // 活动开始时间
    private TimeUtil startTime = new TimeUtil(0, 0);
    private Drawable handDrawable;


    public Drawable getHandDrawable() {
        return handDrawable;
    }

    public void setHandDrawable(Drawable handDrawable) {
        this.handDrawable = handDrawable;
    }

    public TimeUtil getStartTime() {
        return startTime;
    }


    public void setStartTime(Integer min, Integer sec) {
        this.startTime.setMinute(min);
        this.startTime.setSecond(sec);
    }

    public Integer getItem_id() {
        return item_id;
    }

    public void setItem_id(Integer item_id) {
        this.item_id = item_id;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix.set(matrix);
    }

    public animTypes getAnimType() {
        return animType;
    }

    public void setAnimType(animTypes animType) {
        this.animType = animType;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public Path getActionPath() {
        return actionPath;
    }

    public void setActionPath(Path actionPath, Context ctx) {
        if (actionPath != null) this.actionPath = actionPath;
        else {
            Log.d(TAG, "null");
            ToastUtil.show(ctx, "空路径！");
        }
    }

    private static Integer[] all_items = {8, 7, 6, 5, 4, 3, 2, 1, 0};
    private static Integer[] people_static_items = {1};
    private static Integer[] people_active_items = {4, 6, 7};
    private static Integer[] animal_static_items = {};
    private static Integer[] animal_active_items = {8, 5, 3, 2};
    private static Integer[] plant_items = {};
    private static Integer[] other_items = {0};

    private static String[] item_names = {
            "item_building", "item_boy", "sheep_anim", "item_birds", "item_ship_poet",
            "item_swallow", "item_poet_up", "item_poet_handup", "item_fish"
    };
    private static animTypes[] item_animTypes = {
            animTypes.STATIC, animTypes.STATIC, animTypes.ANIMATION, animTypes.GIF, animTypes.GIF,
            animTypes.GIF, animTypes.GIF, animTypes.GIF, animTypes.GIF
    };
    public static ArrayList<Item> getDefaultList(){
        ArrayList<Item> list = new ArrayList<Item>();
        for(int i=item_names.length-1; i>=0; i--){
            Item item = new Item();
            item.setItem_name(item_names[i]);
            item.setAnimType(item_animTypes[i]);
            list.add(item);
        }
        return list;
    }

    public static ArrayList<Item> getDefaultTypeList(List<Integer> type){
        ArrayList<Item> list = new ArrayList<Item>();
        for(int i : type){
            Item item = new Item();
            item.setItem_name(item_names[i]);
            item.setAnimType(item_animTypes[i]);
            list.add(item);
        }
        return list;
    }

    public static List<Integer> getAll_items() {
        return new ArrayList<>(Arrays.asList(all_items));
    }

    public static List<Integer> getPeople_static_items() {
        return new ArrayList<>(Arrays.asList(people_static_items));
    }

    public static List<Integer> getPeople_active_items() {
        return new ArrayList<>(Arrays.asList(people_active_items));
    }

    public static List<Integer> getAnimal_static_items() {
        return new ArrayList<>(Arrays.asList(animal_static_items));
    }

    public static List<Integer> getAnimal_active_items() {
        return new ArrayList<>(Arrays.asList(animal_active_items));
    }

    public static List<Integer> getPlant_items() {
        return new ArrayList<>(Arrays.asList(plant_items));
    }

    public static List<Integer> getOther_items() {
        return new ArrayList<>(Arrays.asList(other_items));
    }
}
