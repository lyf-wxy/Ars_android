package com.example.qman.myapplication.indextab;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.MD5Util;
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

public class SettingUserFragment extends Fragment implements View.OnClickListener
{
    private TextView username = null;
    private ImageView servicesettingselected = null;
    private ImageView psdsettingselected = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.setting_user_fragment, container, false);
        username = (TextView) view.findViewById(R.id.usernameTitle);
        username.setText(ActivityUtil.getParam(getActivity(),"username"));
        servicesettingselected =  (ImageView) view.findViewById(R.id.servicesettingselected);
        psdsettingselected =  (ImageView) view.findViewById(R.id.psdsettingselected);
        /*设置服务按钮*/
        servicesettingselected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.switchToFragment(getActivity(),new SettingFragment(),R.id.id_content);
            }
        });
        /*修改密码按钮*/
        psdsettingselected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.switchToFragment(getActivity(),new SettingPsdFragment(),R.id.id_content);
            }
        });
        //ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"用户设置");
        return view;
    }

    @Override
    public void onClick(View v)
    {

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化组件
       /* oldPwdEt = (EditText) getActivity().findViewById(R.id.oldPwdEt);
        newPwdEt = (EditText) getActivity().findViewById(R.id.newPwdEt);
        reNewPwdEt = (EditText) getActivity().findViewById(R.id.reNewPwdEt);
        saveBtn = (Button) getActivity().findViewById(R.id.saveBtn);

        beginDateLabel=(TextView)getActivity().findViewById(R.id.beginDate);
        endDateLabel=(TextView)getActivity().findViewById(R.id.endDate);
        beginDateLabel.setText(ActivityUtil.getParam(getActivity(),"beginDate"));
        endDateLabel.setText(ActivityUtil.getParam(getActivity(),"endDate"));
        beginDate = beginDateLabel.getText().toString();
        endDate = endDateLabel.getText().toString();*/
    }

}