package com.example.qman.myapplication.loginregister;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FragmentOne extends Fragment implements OnClickListener
{
    private String json = "";
    private EditText username = null;
    private EditText password = null;
    private EditText repassword = null;

    private CheckBox read = null;
    private Button nextBtn;
    private String usernameInput = null;
    private String passwordInput = null;
    private String repasswordInput = null;
    private boolean readChecked = false;
    private String beginDate = null;
    private String endDate = null;

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
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_register_one, container, false);
        nextBtn = (Button) view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(this);
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.checkboxs);
        CheckBoxUtil.initView(layout);//遍历R.id.checkboxs的LinerLayout下的所有checkBox
        return view;
    }

    @Override
    public void onClick(View v)
    {
        JSONObject jsonObject = new JSONObject();
        usernameInput = username.getText().toString().trim();
        passwordInput = password.getText().toString().trim();
        repasswordInput = repassword.getText().toString().trim();

        if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(repasswordInput) || TextUtils.isEmpty(beginDate) || TextUtils.isEmpty(endDate)) {
            ActivityUtil.toastShowFragment(FragmentOne.this,"请完善所有信息");
            return;
        } else {
            if (!passwordInput.equals(repasswordInput)) {
                ActivityUtil.toastShowFragment(FragmentOne.this,"两次输入的密码必须一致");
                return;
            } else {
                readChecked = read.isChecked();
                if (!readChecked) {
                    ActivityUtil.toastShowFragment(FragmentOne.this,"请阅读并同意协议");
                    return;
                }

                //拼接json串，传给FragmentTwo，注册的第二步
                try {
                    jsonObject.put("username",usernameInput);
                    jsonObject.put("password",passwordInput);
                    jsonObject.put("producttype",CheckBoxUtil.createResultStr());
                    jsonObject.put("beginDate",beginDate);
                    jsonObject.put("endDate",endDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        ActivityUtil.switchToFragment(getActivity(),new FragmentTwo(),R.id.id_content,jsonObject.toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化组件
        username = (EditText) getActivity().findViewById(R.id.usernameEt);
        password = (EditText) getActivity().findViewById(R.id.pwdEt);
        repassword = (EditText) getActivity().findViewById(R.id.repwdEt);
        nextBtn = (Button) getActivity().findViewById(R.id.registerBtn);

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
}