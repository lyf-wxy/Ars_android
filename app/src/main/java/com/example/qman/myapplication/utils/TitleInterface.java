package com.example.qman.myapplication.utils;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Qman on 2017/3/7.
 */

public interface TitleInterface {
    void onCreate(Bundle savedInstanceState);
    void setupViews();
    void showBackwardView(int backwardResid, boolean show);
    void showForwardView(int forwardResId, boolean show);
    void onBackward(View backwardView);
    void onForward(View forwardView);
    void setTitle(int titleId);
    void setTitle(CharSequence title);
    void setTitleColor(int textColor);
    void setContentView(int layoutResID);
    void setContentView(View view);
    void setContentView(View view, ViewGroup.LayoutParams params);
    void onClick(View v);
}
