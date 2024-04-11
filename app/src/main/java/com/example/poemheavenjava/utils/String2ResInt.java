package com.example.poemheavenjava.utils;

import android.content.Context;
import android.content.res.Resources;

public class String2ResInt {
    public static int string2ResIntDra(Context ctx, String title){
        Resources res = ctx.getResources();
        int titleid=res.getIdentifier(title,//需要转换的资源名称
                "drawable",        //资源类型
                "com.example.myfirstapplication");//R类所在的包名
        return titleid;
    }
}
