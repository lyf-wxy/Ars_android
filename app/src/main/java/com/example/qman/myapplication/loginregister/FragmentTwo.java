package com.example.qman.myapplication.loginregister;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.bigkoo.pickerview.OptionsPickerView;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.RequestUtil;
import com.example.qman.myapplication.utils.Variables;
import com.example.qman.myapplication.indextab.AddressBean;
import com.example.qman.myapplication.indextab.JsonUtils;
import com.example.qman.myapplication.utils.ActivityUtil;

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

public class FragmentTwo extends Fragment implements OnClickListener
{
    private String json = "";
    JSONObject jsonObject = null;//利用json字符串生成json对象
    private Button registerBtn ;
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
    private String codeidStr = "";

    private ListView listView;

    private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
    private SimpleAdapter adapter = null;
    private String addAreaName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_register_two, container, false);
        //取出FragmentOne跳转时传过来的数据
        if (getArguments() != null) {
            json = getArguments().getString("param");
        }
        listView = (ListView)view.findViewById(R.id.areaLists);
        ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"注册");

        registerBtn = (Button) view.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(this);
        return view ;
    }
    @Override
    public void onClick(View v)
    {
        JSONObject ajsonObject = null;
        try {
            addAreaName = provinceSelected + " " + citiesSelected + " " + areaSelecteds;
            ajsonObject = new JSONObject(json);
            ajsonObject.put("codeidStr",codeidStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestUtil.request(ajsonObject.toString(),"AndroidService/registerService",callback);
        ActivityUtil.toastShow(getActivity(),"注册成功");
        ActivityUtil.switchTo(getActivity(), MainActivity.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }



    //请求后的回调接口
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();
            try {
                jsonObject = new JSONObject(str);
                String result =  jsonObject.getString("result");//解析json查询结果
                if(result.equals("success")){
                    JSONObject ajsonObject = new JSONObject();
                    ajsonObject.put("ordername",addAreaName);
                    ajsonObject.put("sdpath","pathTemp");
                    ajsonObject.put("userid",jsonObject.getString("id"));
                    ajsonObject.put("codeid",jsonObject.getString("codeid"));
                    ajsonObject.put("geometry","000");
                    RequestUtil.request(ajsonObject.toString(),"AndroidService/areaCodeInfoService");//新增订购区域信息
                } else {
                    //setResult("注册失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private Callback callbackCity = new Callback() {
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
                    codeidStr += jsonObject.getString("codeid") + "/";
                    Log.i("codeid",codeidStr);

                } else {
                    //setResult("注册失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
