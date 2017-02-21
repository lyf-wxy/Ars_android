package com.example.qman.myapplication.areatab;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.loginregister.FragmentTwo;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class AreaItemFragment extends Fragment implements OnClickListener
{
    private String json = "";
    private Button skipBtn;
    JSONObject jsonObject = null;//利用json字符串生成json对象

    private String codeidStr = "";
    private String productType = "";
    private ListView listView;
    private ArrayList<HashMap<String, Object>> adaptList = new ArrayList<HashMap<String,Object>>();
    private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
    private SimpleAdapter adapter = null;

    private Bundle savedState;//临时数据保存
    private AreaItemInfoFragment mAreaItemInfo;
    private ArrayList<HashMap<String, Object>> initSplitData(){
        data.clear();
        return data;
    }

    protected void initDataProductType(String productType){
        String[] productTypes = productType.split("/");

        for(String aProductType : productTypes){
            HashMap<String, Object> listm = new HashMap<String, Object>();
            listm.put("producttype", CheckBoxUtil.getChineseName(aProductType));
            adaptList.add(listm);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.area_item_fragment, container, false);
        productType = ActivityUtil.getParam(getActivity(),"producttype");
        listView = (ListView)view.findViewById(R.id.prodeucTypeLists);
        initDataProductType(productType);
        adapter = new SimpleAdapter(getActivity(), adaptList, R.layout.producttype,
                new String[]{"producttype"}, new int[]{R.id.product_type});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                ActivityUtil.switchToFragment(getActivity(),new AreaItemInfoFragment(),R.id.id_content);
            }
        });
        return view ;
    }
    @Override
    public void onClick(View v)
    {
        //跳转到AreaItemInfoFragment
        ActivityUtil.switchToFragment(getActivity(),new AreaItemInfoFragment(),R.id.id_content);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!restoreStateFromArguments()){
            //第一次进入做一些初始化操作
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        //可能再次保存临时数据
        saveStateToArguments();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //也有可能再次保存临时数据
        saveStateToArguments();
    }
    //保存临时数据
    private void saveStateToArguments(){
        savedState = saveState();

        if(savedState != null){
            getActivity().getIntent().getExtras();
            Bundle b = getActivity().getIntent().getExtras();//getArguments();
            Log.v("saveStateToArguments()b",b.toString());
            Log.v("saveStateToArguments()s",savedState.toString());
            b.putBundle("codeidStr",savedState);
        }
    }
    //取出临时数据
    private boolean restoreStateFromArguments(){
        Bundle b = getArguments();
        if(b!=null)
            savedState = b.getBundle("codeidStr");
        if(savedState != null){
            restoreState();
            return true;
        }
        return false;
    }

    private void restoreState(){
        if(savedState != null){
            codeidStr = savedState.getString("codeidStr");
        }
    }

    private Bundle saveState(){
        Bundle state = new Bundle();
        state.putString("codeidStr",codeidStr);
        Log.v("saveState()",codeidStr);
        return state;
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
                    codeidStr += jsonObject.getString("codeid")+"/";
                } else {
                    //setResult("注册失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    //请求后的回调接口
    private Callback callbackCityInfo = new Callback() {
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
                    String dataStr = jsonObject.getString("data");
                    Log.i("dataStr",dataStr);
                    JSONArray areaLists = new JSONArray(dataStr);
                    if (areaLists.length()>0) {
                        for (int i=0;i<areaLists.length();i++) {
                            JSONArray aArea = new JSONArray(areaLists.get(i).toString());
                            HashMap<String, Object> listm = new HashMap<String, Object>();
                            listm.put("province", aArea.get(0).toString());
                            listm.put("city", aArea.get(1).toString());
                            listm.put("area", aArea.get(2).toString());
                            data.add(listm);
                        }
                    }
                } else {
                    //setResult("注册失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    public static com.example.qman.myapplication.areatab.AreaItemFragment newInstance(String text) {
        com.example.qman.myapplication.areatab.AreaItemFragment fragment = new com.example.qman.myapplication.areatab.AreaItemFragment();
        Bundle args = new Bundle();
        args.putString("param", text);
        fragment.setArguments(args);
        return fragment;
    }

}
