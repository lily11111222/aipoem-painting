package com.example.poemheavenjava.entity;

import java.util.ArrayList;

public class Poem {
    private Integer poem_id;
    private String title;
    private String dynasty;
    private String writer;
    private String content;
    private String bg_tip;
    private String item_tip;
    private String meaning;

    public Integer getPoem_id() {
        return poem_id;
    }

    public void setPoem_id(Integer poem_id) {
        this.poem_id = poem_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBg_tip() {
        return bg_tip;
    }

    public void setBg_tip(String bg_tip) {
        this.bg_tip = bg_tip;
    }

    public String getItem_tip() {
        return item_tip;
    }

    public void setItem_tip(String item_tip) {
        this.item_tip = item_tip;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    @Override
    public String toString() {
        return "Poem{" +
                "poem_id=" + poem_id +
                ", title='" + title + '\'' +
                ", dynasty='" + dynasty + '\'' +
                ", writer='" + writer + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    private static String[] mtitle = {
            "《咏鹅》",  "《江雪》","《池上》", "《登鹳雀楼》", "《咏柳》", "《春晓》", "《鹿柴》"
    };
    private static String[] mdynasty = {
            "唐", "唐", "唐", "唐", "唐", "唐", "唐"
    };
    private static String[] mwriter = {
            "骆宾王", "柳宗元", "白居易", "王之涣", "贺知章", "孟浩然", "王维"
    };
    private static String[] mcontent = {
            "鹅，鹅，鹅，曲项向天歌。白毛浮绿水，红掌拨清波。",
            "千山鸟飞绝，万径人踪灭。孤舟蓑笠翁，独钓寒江雪。",
            "小娃撑小艇，偷采白莲回。不解藏踪迹，浮萍一道开。",
            "白日依山尽，黄河入海流。欲穷千里目，更上一层楼。",
            "碧玉妆成一树高，万条垂下绿丝绦。不知细叶谁裁出，二月春风似剪刀。",
            "春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。",
            "空山不见人，但闻人语响。返景入深林，复照青苔上。"
    };
    private static String[] mbg_tip = {
            "鹅，鹅，鹅，曲项向天歌。白毛浮绿水，红掌拨清波。",
            "小朋友，《江雪》描绘了一幅幽静寒冷的画面：在下着大雪的江面上，一叶小舟，一个老渔翁，独自在寒冷的江心垂钓。\n选择描写《江雪》的背景时，想象一下诗中的画面，有雪花飘落、江水悠悠，最好有静谧的感觉。" +
                    "画面宁静美丽，让你感受柳宗元的诗意。请挑选一个温馨的图吧！",
            "小朋友，《池上》讲的是：莲花盛开的夏日里，天真活泼的儿童，撑着一条小船，偷偷地去池中采摘白莲花玩。",
            "白日依山尽，黄河入海流。欲穷千里目，更上一层楼。",
            "碧玉妆成一树高，万条垂下绿丝绦。不知细叶谁裁出，二月春风似剪刀。",
            "春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。",
            "空山不见人，但闻人语响。返景入深林，复照青苔上。"
    };
    private static String[] mitem_tip = {
            "鹅，鹅，鹅，曲项向天歌。白毛浮绿水，红掌拨清波。",
            "小朋友，选择描绘《江雪》的意象时，可以想象蓝天白雪中，有可爱的小鸟在天空飞翔，孤舟漂浮在宁静的江面上，渔翁悠然垂钓。" +
                    "选择的意象，能够让你感受到冬日的宁静和生机。想想诗歌里的美好画面，寻找你喜欢的意象吧！",
            "小娃撑小艇，偷采白莲回。不解藏踪迹，浮萍一道开。",
            "白日依山尽，黄河入海流。欲穷千里目，更上一层楼。",
            "碧玉妆成一树高，万条垂下绿丝绦。不知细叶谁裁出，二月春风似剪刀。",
            "春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。",
            "空山不见人，但闻人语响。返景入深林，复照青苔上。"
    };
    private static String[] mmeaning = {
            "鹅，鹅，鹅，曲项向天歌。白毛浮绿水，红掌拨清波。",
            "山脉重峦叠嶂，鸟儿全都飞走了；所有的路上，不见人影踪迹。\n在这样一个寒冷孤独的环境和氛围中，\n江面有一只小船，一个老头头戴斗笠，身批蓑衣，坐在小船上，在飘满雪花的江面上安静地钓鱼。",
            "小娃撑小艇，偷采白莲回。不解藏踪迹，浮萍一道开。",
            "白日依山尽，黄河入海流。欲穷千里目，更上一层楼。",
            "碧玉妆成一树高，万条垂下绿丝绦。不知细叶谁裁出，二月春风似剪刀。",
            "春眠不觉晓，处处闻啼鸟。夜来风雨声，花落知多少。",
            "空山不见人，但闻人语响。返景入深林，复照青苔上。"
    };
    public static ArrayList<Poem> getDefaultList(){
        ArrayList<Poem> list = new ArrayList<Poem>();
        for(int i=0; i<mtitle.length; i++){
            Poem poem = new Poem();
            poem.setPoem_id(i+1);
            poem.setTitle(mtitle[i]);
            poem.setDynasty(mdynasty[i]);
            poem.setWriter(mwriter[i]);
            poem.setContent(mcontent[i]);
            poem.setBg_tip(mbg_tip[i]);
            poem.setItem_tip(mitem_tip[i]);
            poem.setMeaning(mmeaning[i]);
            list.add(poem);
        }
        return list;
    }
}
