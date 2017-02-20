package com.example.qman.myapplication.utils;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Qman on 2017/2/15.
 */

public class CheckBoxUtil extends AppCompatActivity {
    public enum Type {
        //添加类型时在这里添加，注意中文名应与界面显示一致
        TRSQ("土壤墒情监测"),//土壤墒情
        NZWJXFL("农作物精细分类"),//农作物精细分类
        YMJZSJC("叶面积指数监测"),//叶面积指数监测
        NYBCHJC("农业病虫害监测"),//农业病虫害监测
        NZWZSJC("农作物长势监测"),//农作物长势监测
        NZWGC("农作物估产"),//农作物估产
        TRFL("土壤肥力");//土壤肥力

        // 定义私有变量
        private   String   name ;

        // 构造函数，枚举类型只能为私有
        private Type( String name) {
            this . name = name;
        }

        public String getName(){
            return this.name;
        }
    }

    private static List<CheckBox> checkBoxList = new ArrayList<CheckBox>();

    public static void initView(ViewGroup viewGroup){
        checkBoxList.clear();
        traversalView(viewGroup);
    }
    /**
     * 遍历所有view
     *
     * @param viewGroup
     */
    public static void traversalView(ViewGroup viewGroup) {

        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                traversalView((ViewGroup) view);
                doView((ViewGroup) view);
            } else {
                return;
            }
        }
    }

    /**
     * 处理view,遍历R.id.checkboxs的LinerLayout下的所有checkBox
     *
     * @param viewGroup
     */
    private static void doView(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            CheckBox checkBox = (CheckBox)viewGroup.getChildAt(i);
            checkBoxList.add(checkBox);
        }
    }

    /**
     * 将勾选的checkBox拼接成串
     * @return
     */
    public static String createResultStr(){
        String result = "";
        //遍历集合中的checkBox,判断是否选择，获取选中的文本
        for (CheckBox checkbox : checkBoxList) {
            if (checkbox.isChecked()){
                Type[] allType = Type.values ();
                for (Type aType : allType) {
                    if(checkbox.getText().toString().equals(aType.getName())){
                        result += aType.toString() + "/";
                    }
                }
            }
        }
        return result;
    }

    /**
     * 设置是否选中
     * @param productType
     */
    public static void setChecked(String productType){
        String[] strs = productType.split("/");
        for(String str : strs){
            Type[] allType = Type.values ();
            for (Type aType : allType) {
                if (aType.name().equals(str)) {
                    for (CheckBox checkBox : checkBoxList) {
                        if (checkBox.getText().toString().equals(aType.getName())) {
                            checkBox.setChecked(true);
                        }
                    }
                }
            }
        }
    }
}
