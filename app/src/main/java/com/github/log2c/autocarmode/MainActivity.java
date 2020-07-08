package com.github.log2c.autocarmode;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.log2c.autocarmode.util.AccessibilityUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toOpenAccessibility(View view) {
        AccessibilityUtil.jumpToSetting(this);
    }
}