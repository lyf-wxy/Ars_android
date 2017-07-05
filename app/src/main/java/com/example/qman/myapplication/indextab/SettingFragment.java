package com.example.qman.myapplication.indextab;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.areatab.AreaFragment;
import com.example.qman.myapplication.loginregister.FragmentOne;
import com.example.qman.myapplication.loginregister.FragmentTwo;
import com.example.qman.myapplication.loginregister.MainActivity;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.ListViewUtil;
import com.example.qman.myapplication.utils.MD5Util;
import com.example.qman.myapplication.utils.RequestUtil;
import com.example.qman.myapplication.utils.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingFragment extends Fragment implements View.OnClickListener
{
    private ImageView bt_back;
    private Button saveBtn;
    private String beginDate = null;
    private String endDate = null;
    //判断开始时间和结束时间是否执行过，false表示未执行
    private boolean isClickedBegin = false;
    private boolean isClickedEnd = false;
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
        //RelativeLayout layout = (RelativeLayout)view.findViewById(R.id.checkboxs);
        //CheckBoxUtil.initView(layout);//遍历R.id.checkboxs的LinerLayout下的所有checkBox
        //CheckBoxUtil.setChecked(productType);//初始化checkBox状态
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        bt_back = (ImageView) view.findViewById(R.id.bt_back);
        /*后退按钮*/
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.switchToFragment(getActivity(),new SettingUserFragment(),R.id.id_content);
            }
        });
        saveBtn.setOnClickListener(this);
        isClickedBegin = false;
        isClickedEnd = false;
        //ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"用户设置");
        return view;
    }

    @Override
    public void onClick(View v)
    {
        saveBtn.setBackgroundResource(R.drawable.submit_click);
        ajsonObject = new JSONObject();
    }


    class ChangeInfoThreadTask  extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            //JSONObject ajsonObject = new JSONObject();
            RequestUtil.request(ajsonObject.toString(),"AndroidService/updateUserInfoService",callback);
            return null;
        }
        @Override
        protected void onPostExecute(String s) {

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