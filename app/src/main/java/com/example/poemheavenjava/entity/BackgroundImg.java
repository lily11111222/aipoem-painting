package com.example.poemheavenjava.entity;

import java.util.ArrayList;
import java.util.List;

public class BackgroundImg {
    private String bg_name;
    //private Bitmap bg_img;

    private static String[] bg_names = {
            "bg1", "bg2", "bg7", "bg8", "bg4", "bg5", "bg6", "bg3"
    };
    public static List<String> getDefaultBgList() {
        List<String> bgs = new ArrayList<>();
        for (String s : bg_names) {
            bgs.add(s);
        }
        return bgs;
    }
}
