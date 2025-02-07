package com.example.poemheavenjava.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class XunFeiUtil {

    public static String appid = "d76f5764";

    public static void initXunFei(Context context){
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"="+appid);
    }



    public static void startVoice(Context context, final XunFeiCallbackListener callbackListener) {
        RecognizerDialog dialog = new RecognizerDialog(context, new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d("TryVoiceToText", "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    Toast.makeText(context, "初始化失败，错误码：" + code + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.setParameter( SpeechConstant.CLOUD_GRAMMAR, null );
        dialog.setParameter( SpeechConstant.SUBJECT, null );
        dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        dialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        dialog.setParameter(SpeechConstant.ASR_PTT, "0");
        dialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                callbackListener.onFinish(recognizerResult);
            }
            @Override
            public void onError(SpeechError speechError) {
            }
        });
        dialog.show();
        //Toast.makeText(this, "请开始说话", Toast.LENGTH_SHORT).show();
    }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }
}



