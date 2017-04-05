package com.example.qman.myapplication.indextab;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.loginregister.FragmentOne;
import com.example.qman.myapplication.loginregister.FragmentTwo;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.RequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingFragment extends Fragment implements View.OnClickListener
{
    private Button saveBtn;
    private String beginDate = null;
    private String endDate = null;
    //判断开始时间和结束时间是否执行过，false表示未执行
    private boolean isClickedBegin = false;
    private boolean isClickedEnd = false;
    private EditText oldPwdEt = null;
    private EditText newPwdEt = null;
    private EditText reNewPwdEt = null;

    private String oldPwdInput = null;
    private String newPwdInput = null;
    private String reNewPwdInput = null;
    private String id = "";
    private String productType = "";
    JSONObject jsonObject = null;//利用json字符串生成json对象
    JSONObject ajsonObject = null;
    /**
     * 日期控件参数定义
     * */
    //获取日期格式器对象
    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
    //定义一个TextView控件对象
    TextView beginDateLabel = null;
    TextView endDateLabel = null;
    //获取一个日历对象
    Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

    //当点击DatePickerDialog控件的设置按钮时，调用该方法
    DatePickerDialog.OnDateSetListener beginDateListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {//beginOrEnd=0表示服务起始时间，beginOrEnd=1表示服务结束时间
            //修改日历控件的年，月，日
            //这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //将页面TextView的显示更新为最新时间
            beginDate = format
                    .format(dateAndTime.getTime());
            beginDateLabel.setText(beginDate);
            isClickedBegin = true;
        }
    };
    DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {//beginOrEnd=0表示服务起始时间，beginOrEnd=1表示服务结束时间
            //修改日历控件的年，月，日
            //这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //将页面TextView的显示更新为最新时间
            endDate = format
                    .format(dateAndTime.getTime());
            endDateLabel.setText(endDate);
            isClickedEnd = true;
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.setting_fragment, container, false);
        id = ActivityUtil.getParam(getActivity(),"id");
        productType = ActivityUtil.getParam(getActivity(),"producttype");
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.checkboxs);
        CheckBoxUtil.initView(layout);//遍历R.id.checkboxs的LinerLayout下的所有checkBox
        CheckBoxUtil.setChecked(productType);//初始化checkBox状态
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(this);
        isClickedBegin = false;
        isClickedEnd = false;
        ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"用户设置");
        return view;
    }

    @Override
    public void onClick(View v)
    {
        ajsonObject = new JSONObject();
        oldPwdInput = oldPwdEt.getText().toString().trim();
        newPwdInput = newPwdEt.getText().toString().trim();
        reNewPwdInput = reNewPwdEt.getText().toString().trim();
        try {
            //密码信息有输入
            if (!TextUtils.isEmpty(oldPwdInput) || !TextUtils.isEmpty(newPwdInput) || !TextUtils.isEmpty(reNewPwdInput)) {
                //三项均不为空
                if(!TextUtils.isEmpty(oldPwdInput) && !TextUtils.isEmpty(newPwdInput) && !TextUtils.isEmpty(reNewPwdInput)){
                    if (!newPwdInput.equals(reNewPwdInput)) {
                        //判断两次新密码输入是否一致
                        ActivityUtil.toastShowFragment(SettingFragment.this,"两次输入的密码必须一致");
                        return;
                    } else {
                        //一致时通过，加入json中待传给后台
                        ajsonObject.put("oldPwdInput",oldPwdInput);
                        ajsonObject.put("paswd",newPwdInput);
                    }
                }
                else{
                    //有为空项时提醒完善密码信息
                    ActivityUtil.toastShowFragment(SettingFragment.this,"请将密码信息填完整");
                }
                return;
            }
            if(!productType.equals(CheckBoxUtil.createResultStr()))
                //当有更新时加入json中待传给后台
                ajsonObject.put("producttype",CheckBoxUtil.createResultStr());
            if(isClickedBegin)
                ajsonObject.put("beginDate",beginDate);
            if(isClickedEnd)
                ajsonObject.put("endDate",endDate);
            if(ajsonObject != null) {
                //有变化时才进行更新
                ajsonObject.put("id", id);
                RequestUtil.request(ajsonObject.toString(),"AndroidService/updateUserInfoService",callback);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //请求后的回调接口
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            // setResult(e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();
            try {
                jsonObject = new JSONObject(str);
                String result =  jsonObject.getString("result");//解析json查询结果
                if(result.equals("success")){
                    ActivityUtil.toastShow(getActivity(),"修改成功");
                    Iterator iterator = ajsonObject.keys();
                    while(iterator.hasNext()){
                        String key = (String) iterator.next();
                        String value = ajsonObject.getString(key);
                        if(!key.equals("id")) {
                            //将缓存中的数据更新
                            ActivityUtil.changeParam(getActivity(), key, value);
                        }
                    }
                } else {
                    ActivityUtil.toastShow(getActivity(),"修改失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化组件
        oldPwdEt = (EditText) getActivity().findViewById(R.id.oldPwdEt);
        newPwdEt = (EditText) getActivity().findViewById(R.id.newPwdEt);
        reNewPwdEt = (EditText) getActivity().findViewById(R.id.reNewPwdEt);
        saveBtn = (Button) getActivity().findViewById(R.id.saveBtn);

        beginDateLabel=(TextView)getActivity().findViewById(R.id.beginDate);
        endDateLabel=(TextView)getActivity().findViewById(R.id.endDate);
        beginDateLabel.setText(ActivityUtil.getParam(getActivity(),"beginDate"));
        endDateLabel.setText(ActivityUtil.getParam(getActivity(),"endDate"));
        beginDate = beginDateLabel.getText().toString();
        endDate = endDateLabel.getText().toString();
        //得到页面设定日期的按钮的点击事件监听器
        beginDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
                new DatePickerDialog(getActivity(),
                        beginDateListener,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        endDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
                new DatePickerDialog(getActivity(),
                        endDateListener,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

}