package com.example.qman.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.app.DatePickerDialog;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Locale;

import com.bigkoo.pickerview.OptionsPickerView;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;
public class RegisterActivity extends AppCompatActivity {
    private OkHttpClient okHttpClient = new OkHttpClient();
    JSONObject jsonObject = null;//利用json字符串生成json对象
    private EditText username = null;
    private EditText password = null;
    private EditText repassword = null;
    private CheckBox soilmos = null;
    private CheckBox corpclass = null;
    private CheckBox lai = null;
    private CheckBox disease = null;
    private CheckBox read = null;
    private Button register;
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
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    //获取日期格式器对象
    DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();
    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
    //定义一个TextView控件对象
    TextView beginDateLabel = null;
    TextView endDateLabel = null;
    //获取一个日历对象
    Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

    private TextView tv_address;

    private ArrayList<AddressBean> provinceList = new ArrayList<>();//创建存放省份实体类的集合

    private ArrayList<String> cities ;//创建存放城市名称集合
    private ArrayList<List<String>> citiesList= new ArrayList<>();//创建存放城市名称集合的集合

    private ArrayList<String> areas ;//创建存放区县名称的集合
    private ArrayList<List<String>> areasList ;//创建存放区县名称集合的集合
    private ArrayList<List<List<String>>> areasListsList = new ArrayList<>();//创建存放区县集合的集合的集合
    private OptionsPickerView mPvOptions;

    private String provinceSelected = null;
    private String citiesSelected = null;
    private String areaSelecteds = null;

    //当点击DatePickerDialog控件的设置按钮时，调用该方法
    DatePickerDialog.OnDateSetListener beginDateListener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {//beginOrEnd=0表示服务起始时间，beginOrEnd=1表示服务结束时间
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput = username.getText().toString().trim();
                passwordInput = password.getText().toString().trim();
                repasswordInput = repassword.getText().toString().trim();

                if (TextUtils.isEmpty(usernameInput) || TextUtils.isEmpty(passwordInput) || TextUtils.isEmpty(repasswordInput) || TextUtils.isEmpty(beginDate) || TextUtils.isEmpty(endDate)) {
                    Toast.makeText(RegisterActivity.this, "请完善所有信息", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (!passwordInput.equals(repasswordInput)) {
                        Toast.makeText(RegisterActivity.this, "两次输入的密码必须一致", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        //获取复选框信息
                        soilmosChecked = soilmos.isChecked();
                        corpclassChecked = corpclass.isChecked();
                        laiChecked = lai.isChecked();
                        diseaseChecked = disease.isChecked();
                        readChecked = read.isChecked();
                        if (!readChecked) {
                            Toast.makeText(RegisterActivity.this, "请阅读并同意协议", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //判断是否登陆成功
                        String json = "{'username':'" + usernameInput + "'," + "'password':'" + passwordInput + "',"
                                + "'soilmos':'" + soilmosChecked + "'," + "'corpclass':'" + corpclassChecked + "',"
                                + "'lai':'" + laiChecked + "'," + "'disease':'" + diseaseChecked + "',"
                                + "'beginDate':'" + beginDate + "'," + "'endDate':'" + endDate + "',"
                                + "'provinceSelected':'" + provinceSelected + "'," + "'citiesSelected':'" + citiesSelected + "',"
                                + "'areaSelecteds':'" + areaSelecteds +
                                "'}";
                        //把请求的内容字符串转换为json
                        RequestBody body = RequestBody.create(JSON, json);
                        Request request = new Request.Builder()
                                .url("http://10.2.3.182:8080/AndroidService/registerService")
                                .post(body)
                                .build();
                        okHttpClient.newCall(request).enqueue(callback);//callback是请求后的回调接口

                    }
                }
            }

        });
        //得到页面设定日期的按钮控件对象
        Button dateBtn = (Button) findViewById(R.id.setBeginDate);
        Button dateBtn1 = (Button) findViewById(R.id.setEndDate);
        //设置按钮的点击事件监听器
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
                new DatePickerDialog(RegisterActivity.this,
                        beginDateListener,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        dateBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
                new DatePickerDialog(RegisterActivity.this,
                        endDateListener,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        //更新在界面显示


        tv_address = (TextView) findViewById(R.id.tv_address);
        //获取json字符串,用来解析以获取集合
        String jsonString = JsonUtils.getJsonString(RegisterActivity.this,
                "province_data.json");
        //解析json字符串,向各级集合中添加元素
        parseJson(jsonString);
        mPvOptions = new OptionsPickerView(this);

        //设置三级联动的效果
        mPvOptions.setPicker(provinceList, citiesList, areasListsList, true);

        //设置可以循环滚动,true表示这一栏可以循环,false表示不可以循环
        mPvOptions.setCyclic(true, false, false);

        //设置默认选中的位置
        mPvOptions.setSelectOptions(0, 0, 0);
        mPvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                //返回的分别是三个级别的选中位置
                String city = provinceList.get(options1).getPickerViewText();
                String address;
                //  如果是直辖市或者特别行政区只设置市和区/县
                if ("北京市".equals(city) || "上海市".equals(city) || "天津市".equals(city) || "重庆市".equals(city) || "澳门".equals(city) || "香港".equals(city)) {
                    address = provinceList.get(options1).getPickerViewText()
                            + " " + areasListsList.get(options1).get(option2).get(options3);
                    provinceSelected = provinceList.get(options1).getPickerViewText();
                    citiesSelected = null;
                    areaSelecteds = areasListsList.get(options1).get(option2).get(options3);;
                } else {
                    address = provinceList.get(options1).getPickerViewText()
                            + " " + citiesList.get(options1).get(option2)
                            + " " + areasListsList.get(options1).get(option2).get(options3);
                    provinceSelected = provinceList.get(options1).getPickerViewText();
                    citiesSelected = citiesList.get(options1).get(option2);
                    areaSelecteds = areasListsList.get(options1).get(option2).get(options3);;
                }
                tv_address.setText(address);
                Toast.makeText(RegisterActivity.this, "我被点击了", Toast.LENGTH_LONG).show();

            }
        });
        //点击文本框的时候,显示地址选择框
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPvOptions.show();
            }
        });

    }
    //解析获得的json字符串,放在各个集合中
        private void parseJson(String json){
            try {
                //得到一个数组类型的json对象
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {//对数组进行遍历得到每一个jsonobject对象
                    JSONObject provinceObject = (JSONObject) jsonArray.get(i);
                    String provinceName = provinceObject.getString("name");//得到省份的名字
                    provinceList.add(new AddressBean(provinceName));//向集合里面添加元素
                    JSONArray cityArray = provinceObject.optJSONArray("city");
                    cities = new ArrayList<>();//创建存放城市名称集合
                    areasList = new ArrayList<>();//创建存放区县名称的集合的集合
                    for (int j = 0; j < cityArray.length(); j++) {//遍历每个省份集合下的城市列表
                        JSONObject cityObject = (JSONObject) cityArray.get(j);
                        String cityName = cityObject.getString("name");
                        cities.add(cityName);//向集合里面添加元素
                        JSONArray areaArray = cityObject.optJSONArray("area");
                        areas = new ArrayList<>();//创建存放区县名称的集合
                        for (int k = 0; k < areaArray.length(); k++) {
                            String areaName = areaArray.getString(k);
                            areas.add(areaName);
                        }
                        areasList.add(areas);
                    }
                    citiesList.add(cities);
                    areasListsList.add(areasList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }
    //更新页面TextView的方法
    private void updateLabel(TextView tv) {
        tv.setText(format
                .format(dateAndTime.getTime()));
    }
    /**
    * 初始化组件
     */
    private void initView() {
        tv = (TextView) findViewById(R.id.textView1);
        username = (EditText) findViewById(R.id.usernameEt);
        password = (EditText) findViewById(R.id.pwdEt);
        repassword = (EditText) findViewById(R.id.repwdEt);
        register = (Button) findViewById(R.id.registerBtn);
        soilmos = (CheckBox) findViewById(R.id.soilmos);
        corpclass = (CheckBox) findViewById(R.id.corpclass);
        lai = (CheckBox) findViewById(R.id.lai);
        disease = (CheckBox) findViewById(R.id.disease);
        read = (CheckBox) findViewById(R.id.read);
        beginDateLabel=(TextView)findViewById(R.id.beginDate);
        endDateLabel=(TextView)findViewById(R.id.endDate);
    }
    //显示调用结果
    private void setResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                //tv.setText(msg);
            }
        });
    }

    //请求后的回调接口
    private Callback callback = new Callback() {

        @Override
        public void onFailure(Call call, IOException e) {
            setResult(e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();
            try {
                jsonObject = new JSONObject(str);
                String result =  jsonObject.getString("result");//解析json查询结果
                if(result.equals("success")){
                    //注册成功，跳转到登陆
                    setResult("注册成功，请登陆");
                    Intent intent = new Intent();
                    intent.setClass(RegisterActivity.this, MainActivity.class);
                    //intent.putExtra("usernameLogin",usernameInput);
                    startActivity(intent);//红色部分为要打开的心窗口的类名
                } else {
                    setResult("注册失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
