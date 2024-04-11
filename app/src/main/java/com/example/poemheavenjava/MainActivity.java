package com.example.poemheavenjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        TextView title = findViewById(R.id.tv_title);
//        Typeface mtypeface=Typeface.createFromAsset(getAssets(),"font/handwriting.ttf");
//        title.setTypeface(mtypeface);
        findViewById(R.id.btn_begin).setOnClickListener(this);
        findViewById(R.id.btn_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_begin){
            Intent intent = new Intent(this, PoemSelectActivity.class);
//            Intent intent = new Intent(this, AnimationRequestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if(v.getId() == R.id.btn_test){
            Intent intent = new Intent(this, TestActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}