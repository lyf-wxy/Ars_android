package com.example.qman.myapplication.loginregister;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.RequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterServiceFragmentActivity extends Activity
{
    private Button loginBtn;
    private String username = null;
    private String paswd = null;
    private String phone = null;

    private CheckBox read = null;
    private boolean readChecked = false;

    private ImageView bt_beginDate = null;
    private ImageView bt_endDate = null;
    private String beginDate = null;
    private String endDate = null;

    /**
     * 初始化组件
     */
    private void initView() {
        loginBtn = (Button) findViewById(R.id.loginBtn);

        //bt_beginDate=(ImageView) findViewById(R.id.bt_beginDate);
        //bt_endDate=(ImageView) findViewById(R.id.bt_endDate);
        beginDateLabel=(TextView)findViewById(R.id.beginDate);
        endDateLabel=(TextView)findViewById(R.id.endDate);

        read = (CheckBox) findViewById(R.id.read_word);

        username = ActivityUtil.getParam(RegisterServiceFragmentActivity.this,"username");
        paswd = ActivityUtil.getParam(RegisterServiceFragmentActivity.this,"paswd");
        phone = ActivityUtil.getParam(RegisterServiceFragmentActivity.this,"phone");

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.checkboxs);
        CheckBoxUtil.initView(layout);//遍历R.id.checkboxs的LinerLayout下的所有checkBox
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_service);
        initView();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setBackgroundResource(R.drawable.bt_login_now_click);
                readChecked = read.isChecked();
                if (readChecked) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("username",username);
                        jsonObject.put("password", paswd);
                        jsonObject.put("producttype",CheckBoxUtil.createResultStr());
                        jsonObject.put("beginDate",beginDate);
                        jsonObject.put("endDate",endDate);
                        RequestUtil.request(jsonObject.toString(),"AndroidService/registerService",callback);
                        ActivityUtil.toastShow(RegisterServiceFragmentActivity.this,"注册成功");
                        ActivityUtil.switchTo(RegisterServiceFragmentActivity.this, MainActivity.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ActivityUtil.switchTo(RegisterServiceFragmentActivity.this,MainActivity.class);
                }

            }
        });

        //得到页面设定日期的按钮的点击事件监听器
        beginDateLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
                new DatePickerDialog(RegisterServiceFragmentActivity.this,
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
                new DatePickerDialog(RegisterServiceFragmentActivity.this,
                        endDateListener,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

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
    //请求后的回调接口
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();

        }
    };
}
