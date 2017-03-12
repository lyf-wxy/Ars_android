package com.example.qman.myapplication.areatab;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
<<<<<<< HEAD
import android.support.design.widget.FloatingActionButton;
=======
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
>>>>>>> Qman29/master
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.indextab.IndexTabMainActivity;
import com.example.qman.myapplication.loginregister.FragmentTwo;
import com.example.qman.myapplication.lyf.drawarea.DrawArea;

import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.ListViewUtil;
import com.example.qman.myapplication.utils.RequestUtil;
import com.example.qman.myapplication.utils.TitleActivity;
import com.example.qman.myapplication.utils.TitleInterface;
import com.example.qman.myapplication.utils.Variables;
import com.example.qman.myapplication.indextab.AddressBean;
import com.example.qman.myapplication.indextab.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AreaFragment extends Fragment
{
    private String json = "";
    private Button tv_address;
    private Button tv_draw;

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
    private String id = "";

    private ListView listView;
    private SearchView mSearchview;
    private RecyclerView recyclerView;
    private ArrayList<HashMap<String,Object>> list = null;//adapt绑定的数据集
    //private SimpleAdapter adapter = null;
    private BaseRecyclerAdapter<String> mAdapter = null;
    private Bundle savedState;//临时数据保存
    private TextView title;
    private Button toolbar_search;
    private Button toolbar_add;
    /**
     * 将控件选择的行政区域加到listView中
     * @param province
     * @param city
     * @param area
     * @return
     */
    private void addData(String province,String city,String area){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("province", province);
        map.put("city", city);
        map.put("area", area);
        list.add(map);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.area_layout, container, false);
        id = ActivityUtil.getParam(getActivity(),"id");
        codeidStr = ActivityUtil.getParam(getActivity(),"locno");//intent.getStringExtra("locno");
        json = "{'id':'" + id + "'," + "'locno':'" + codeidStr + "'}";
        listView = (ListView)view.findViewById(R.id.areaLists);
        recyclerView = (RecyclerView) view.findViewById(R.id.areaRecyclerView);
        mSearchview = (SearchView) view.findViewById(R.id.searchView);
        toolbar_search = (Button)getActivity().findViewById(R.id.toolbar_search);
        toolbar_add = (Button)getActivity().findViewById(R.id.toolbar_add);
        ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"区域");
        toolbar_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSearchview.getVisibility() == View.VISIBLE){
                    mSearchview.setVisibility(View.GONE);
                }
                else if(mSearchview.getVisibility() == View.GONE){
                    mSearchview.setVisibility(View.VISIBLE);
                }
            }
        });
        listView.setTextFilterEnabled(true);//设置listView可以被过虑
        new ListViewLoadThreadTask().execute();

        // 设置该SearchView默认是否自动缩小为图标
        mSearchview.setIconifiedByDefault(false);
        // 设置该SearchView显示搜索按钮
        mSearchview.setSubmitButtonEnabled(false);
        // 设置该SearchView内默认显示的提示文本
        mSearchview.setQueryHint("搜索");
        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 用户输入字符时激发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    // 清除ListView的过滤
                    listView.clearTextFilter();
                } else {
                    // 使用用户输入的内容对ListView的列表项进行过滤
                    listView.setFilterText(query);
                }
                ActivityUtil.toastShow(getActivity(),query);
                return false;
            }
            // 单击搜索按钮时激发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {

                JSONObject aJsonObject = new JSONObject();
                //拼接json串，传给FragmentTwo，注册的第二步
                try {
                    aJsonObject.put("producttype",ActivityUtil.getParam(getActivity(),"producttype"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ActivityUtil.switchToFragment(getActivity(),new FragmentTwo(),R.id.id_content,aJsonObject.toString());
            }
        });

        listView.setOnDragListener(null);
        return view ;
    }


    /**
     * 加载listView时的新线程
     */
    class ListViewLoadThreadTask extends AsyncTask<String, Integer, String>{


        @Override
        protected String doInBackground(String... params) {
            list = ListViewUtil.initSplitData(codeidStr);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            List<String> mDataList = new ArrayList<>();
            String[] productTypes = codeidStr.split("/");
            for (int i = 0; i < productTypes.length; i++) {
                mDataList.add(productTypes[i]);
            }
            //设置item动画
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter = new BaseRecyclerAdapter<String>(R.layout.area_layout_cardview,getActivity(),mDataList) {
                @Override
                public int getItemLayoutId(int viewType) {
                    return viewType;
                }

                @Override
                public void bindData(RecyclerViewHolder holder, int position, String item) {
                    //调用holder.getView(),getXXX()方法根据id得到控件实例，进行数据绑定即可
                    holder.getTextView(R.id.title).setText(item);
                }
            };
            recyclerView.setAdapter(mAdapter);
            //添加item点击事件监听
            ((BaseRecyclerAdapter)mAdapter).setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int pos) {
                    ActivityUtil.switchToFragment(getActivity(),new AreaItemFragment(),R.id.id_content);
                }
            });
            ((BaseRecyclerAdapter)mAdapter).setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(View itemView, int pos) {
                    //Toast.makeText(AdapterTestActivity.this, "long click " + pos, Toast.LENGTH_SHORT).show();
                }
            });
            //设置布局样式LayoutManager
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(!restoreStateFromArguments()){
            //第一次进入做一些初始化操作
        }

        tv_address = (Button) getActivity().findViewById(R.id.tv_address);
        tv_draw = (Button) getActivity().findViewById(R.id.tv_draw);

        //获取json字符串,用来解析以获取集合
        String jsonString = JsonUtils.getJsonString(getActivity(),
                "province_data.json");
        //解析json字符串,向各级集合中添加元素
        parseJson(jsonString);
        mPvOptions = new OptionsPickerView(getActivity());

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
                address = provinceList.get(options1).getPickerViewText()
                            + " " + citiesList.get(options1).get(option2)
                            + " " + areasListsList.get(options1).get(option2).get(options3);
                provinceSelected = provinceList.get(options1).getPickerViewText();
                citiesSelected = citiesList.get(options1).get(option2);
                areaSelecteds = areasListsList.get(options1).get(option2).get(options3);

                //tv_address.setText(address);

                //查询订购区域代码codeid
                new QueryCityCodeIdThreadTask().execute();
                //确定添加，更新数据库的codeid字段
                new UpdateCityCodeIdThreadTask().execute();

            }
        });
        //点击文本框的时候,显示地址选择框
        toolbar_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPvOptions.show();
//                selectAreaOrDraw mselectAreaOrDraw =  new selectAreaOrDraw();
//                ActivityUtil.switchToFragment(getActivity(), mselectAreaOrDraw,R.id.fullscreen);
            }
        });

        //Draw
        tv_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawArea mDrawArea =  new DrawArea();
                ActivityUtil.switchToFragment(getActivity(), mDrawArea,R.id.fullscreen);
            }
        });

    }

    class QueryCityCodeIdThreadTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            //查询订购区域代码codeid
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("provinceSelected",provinceSelected);
                jsonObject.put("citiesSelected",citiesSelected);
                jsonObject.put("areaSelecteds",areaSelecteds);
                String str = RequestUtil.request(jsonObject.toString(),"AndroidService/cityService");
                JSONObject jsonObjectResult = new JSONObject(str);
                String result = jsonObjectResult.getString("result");//解析json查询结果
                if (result.equals("success")) {
                    codeidStr += jsonObjectResult.getString("codeid")+"/";
                    //将缓存中的数据更新
                    ActivityUtil.changeParam(getActivity(),"locno",codeidStr);
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class UpdateCityCodeIdThreadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject ajsonObject = new JSONObject();
            try {
                ajsonObject.put("id",id);
                ajsonObject.put("codeidStr",codeidStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestUtil.request(ajsonObject.toString(),"AndroidService/updateUserCodeIdService");
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            addData(provinceSelected,citiesSelected,areaSelecteds);
            //adapter.notifyDataSetChanged();
            mAdapter.notifyDataSetChanged();
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
        return state;
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
}
