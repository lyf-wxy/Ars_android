package com.example.qman.myapplication;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FragmentOne extends Fragment implements OnClickListener
{
    private String json = "";
    JSONObject jsonObject = null;//利用json字符串生成json对象
    private EditText username = null;
    private EditText password = null;
    private EditText repassword = null;
    private CheckBox soilmos = null;
    private CheckBox corpclass = null;
    private CheckBox lai = null;
    private CheckBox disease = null;
    private CheckBox read = null;
    private Button nextBtn;
    private TextView tv;
    private String usernameInput = null;
    private String passwordInput = null;
    private String repasswordInput = null;
    private boolean soilmosChecked = false;
    private boolean corpclassChecked = false;
    private boolean laiChecked = false;
    private boolean diseaseChecked = false;
    private boolean readChecked = false;
    private String beginDate = null;
    private String endDate = null;
    int counter = 3;


    //获取日期格式器对象
    DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
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
            Log.v("endDate",endDate);
            endDateLabel.setText(endDate);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_register_one, container, false);
        nextBtn = (Button) view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v)
    {
        usernameInput = username.getText().toString().trim();
        passwordInput = password.getText().toString().trim();
        repasswordInput = repassword.getText().toString().trim();

        if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(repasswordInput) || TextUtils.isEmpty(beginDate) || TextUtils.isEmpty(endDate)) {
            Toast.makeText(getActivity(), "请完善所有信息", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!passwordInput.equals(repasswordInput)) {
                Toast.makeText(getActivity(), "两次输入的密码必须一致", Toast.LENGTH_SHORT).show();
                return;
            } else {
                //获取复选框信息
                soilmosChecked = soilmos.isChecked();
                corpclassChecked = corpclass.isChecked();
                laiChecked = lai.isChecked();
                diseaseChecked = disease.isChecked();
                readChecked = read.isChecked();
                if (!readChecked) {
                    Toast.makeText(getActivity(), "请阅读并同意协议", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断是否登陆成功
                json = "{'username':'" + usernameInput + "'," + "'password':'" + passwordInput + "',"
                        + "'soilmos':'" + soilmosChecked + "'," + "'corpclass':'" + corpclassChecked + "',"
                        + "'lai':'" + laiChecked + "'," + "'disease':'" + diseaseChecked + "',"
                        + "'beginDate':'" + beginDate + "'," + "'endDate':'" + endDate + "',";
            }
        }

        FragmentTwo fTwo = FragmentTwo.newInstance(json);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.replace(R.id.id_content, fTwo, "TWO");
        tx.addToBackStack(null);
        tx.commit();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initView(view);
//        tv = (TextView) getActivity().findViewById(R.id.textView1);
        username = (EditText) getActivity().findViewById(R.id.usernameEt);
        password = (EditText) getActivity().findViewById(R.id.pwdEt);
        repassword = (EditText) getActivity().findViewById(R.id.repwdEt);
        nextBtn = (Button) getActivity().findViewById(R.id.registerBtn);
        soilmos = (CheckBox) getActivity().findViewById(R.id.soilmos);
        corpclass = (CheckBox) getActivity().findViewById(R.id.corpclass);
        lai = (CheckBox) getActivity().findViewById(R.id.lai);
        disease = (CheckBox) getActivity().findViewById(R.id.disease);
        read = (CheckBox) getActivity().findViewById(R.id.read);
        beginDateLabel=(TextView)getActivity().findViewById(R.id.beginDate);
        endDateLabel=(TextView)getActivity().findViewById(R.id.endDate);

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

    //更新页面TextView的方法
    private void updateLabel(TextView tv) {
        tv.setText(format
                .format(dateAndTime.getTime()));
    }
/*
    private void initView(View view) {
        tv = (TextView) view.findViewById(R.id.textView1);
        username = (EditText) view.findViewById(R.id.usernameEt);
        password = (EditText) view.findViewById(R.id.pwdEt);
        repassword = (EditText) view.findViewById(R.id.repwdEt);
        register = (Button) view.findViewById(R.id.registerBtn);
        soilmos = (CheckBox) view.findViewById(R.id.soilmos);
        corpclass = (CheckBox) view.findViewById(R.id.corpclass);
        lai = (CheckBox) view.findViewById(R.id.lai);
        disease = (CheckBox) view.findViewById(R.id.disease);
        read = (CheckBox) view.findViewById(R.id.read);
        beginDateLabel=(TextView)view.findViewById(R.id.beginDate);
        endDateLabel=(TextView)view.findViewById(R.id.endDate);
    }*/
    //显示调用结果
/*    private void setResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                //tv.setText(msg);
            }
        });
    }*/


}