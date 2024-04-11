package com.example.poemheavenjava.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poemheavenjava.R;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.dialogs.PopTip;
import com.kongzue.dialogx.interfaces.OnBindView;

public class ToastUtil {
    public static void show(Context ctx, String desc){
//        Toast.makeText(ctx, desc, Toast.LENGTH_SHORT).show();
        PopTip.build()
                .setCustomView(new OnBindView<PopTip>(R.layout.toast) {
                    @Override
                    public void onBind(PopTip dialog, View v) {
                        ((TextView) v.findViewById(R.id.tv_toast)).setText(desc);
                    }
                }).setRadius(DimenUtils.dp2pxInt(50))
                .show();
//        PopTip.show((CharSequence) desc).setRadius(DimenUtils.dp2pxInt(50));
    }

    public static void showIntro(Context ctx, int i){
        int res = 0;
        switch (i) {
            case 1 : res = R.string.intro1;
            break;
            case 2 : res = R.string.intro2;
            break;
            case 3 : res = R.string.intro3;
            break;
            case 4 : res = R.string.intro4;
            break;
            case 5 : res = R.string.intro5;
                break;
            case 6 : res = R.string.intro6;
                break;
            case 7 : res = R.string.intro7;
                break;
            default:;
        }
        int finalRes = res;
        CustomDialog.build().setCustomView(new OnBindView<CustomDialog>(R.layout.tip) {
                    @Override
                    public void onBind(final CustomDialog dialog, View v) {
//                        ((TextView) v.findViewById(R.id.tv_tip)).setTextSize(DimenUtils.sp2px());
                        ((TextView) v.findViewById(R.id.tv_tip)).setText(finalRes);
                        ((LinearLayout)v.findViewById(R.id.ll_tip)).removeView(v.findViewById(R.id.included));
                        Button btn_ok = v.findViewById(R.id.btn_tip_ok);
                        btn_ok.setOnClickListener(v1 -> {
                            dialog.dismiss();
                        });
                    }
                })
//                .setRadius(DimenUtils.dp2pxInt(50))
//                .setWidth(DimenUtils.dp2pxInt(800))
                .setMaskColor(Color.parseColor("#00000000"))
                // 沉浸式非安全区隔离模式，避免底下空出一块
                .setAutoUnsafePlacePadding(false)
                .show();
    }
}
