package com.example.qman.myapplication.areatab;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.tasks.query.QueryParameters;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.indextab.IndexTabMainActivity;
import com.example.qman.myapplication.loginregister.FragmentTwo;
import com.example.qman.myapplication.lyf.drawarea.DrawArea;

import com.example.qman.myapplication.lyf.drawarea.SaveDrawArea;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.FormFile;
import com.example.qman.myapplication.utils.ListViewUtil;
import com.example.qman.myapplication.utils.RequestUtil;
import com.example.qman.myapplication.utils.SocketHttpRequester;
import com.example.qman.myapplication.utils.TitleActivity;
import com.example.qman.myapplication.utils.TitleInterface;
import com.example.qman.myapplication.utils.Util;
import com.example.qman.myapplication.utils.Variables;
import com.example.qman.myapplication.indextab.AddressBean;
import com.example.qman.myapplication.indextab.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.esri.core.tasks.query.QueryTask;
import com.esri.core.map.Feature;
import com.esri.core.geometry.Point;

import static android.R.id.message;
import static android.content.ContentValues.TAG;
import static com.example.qman.myapplication.R.id.map;
import static com.example.qman.myapplication.utils.Variables.targetServerURL;

public class AreaFragment extends Fragment
{
    private String json = "";

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

    private SearchView mSearchview;
    private RecyclerView recyclerView;
    private List<HashMap<String,Object>> list = null;//adapt绑定的数据集
    private BaseRecyclerAdapter<HashMap<String,Object>> mAdapter = null;
    private Bundle savedState;//临时数据保存
    private Button toolbar_search;
    private Button toolbar_add;
    private Button toolbar_draw;
    private String addAreaName = "";//未被添加过，需要刷新RecyclerAdapter
    private String codeIdTemp = "";
    private MapView mMapView;

    ProgressDialog progress;
    GraphicsLayer graphicsLayer;
    File upfile;

    public static String TAG = "AreaFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.area_layout, container, false);
        id = ActivityUtil.getParam(getActivity(),"id");
        codeidStr = ActivityUtil.getParam(getActivity(),"locno");//intent.getStringExtra("locno");
        json = "{'id':'" + id + "'," + "'locno':'" + codeidStr + "'}";
        recyclerView = (RecyclerView) view.findViewById(R.id.areaRecyclerView);
        mSearchview = (SearchView) view.findViewById(R.id.searchView);

        mMapView = (MapView) view.findViewById(R.id.mapofAreaFragment);

        toolbar_search = (Button)getActivity().findViewById(R.id.toolbar_search);
        toolbar_add = (Button)getActivity().findViewById(R.id.toolbar_add);
        toolbar_draw = (Button)getActivity().findViewById(R.id.toolbar_draw);
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
                return true;
            }
            // 单击搜索按钮时激发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().
                        filter(newText);
                return true;
            }
        });
        return view ;
    }


    /**
     * 加载listView时的新线程
     */
    class ListViewLoadThreadTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {

            list = ListViewUtil.initSplitData(ActivityUtil.getParam(getActivity(),"id"), codeidStr);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                final List<String> mDataList = new ArrayList<>();
                String[] productTypes = codeidStr.split("/");
                for (int i = 0; i < productTypes.length; i++) {
                    mDataList.add(productTypes[i]);
                }

                //设置item动画
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                mAdapter = new BaseRecyclerAdapter<HashMap<String,Object>>(R.layout.area_layout_cardview,getActivity(),list) {//HashMap<String,Object>
                    @Override
                    public int getItemLayoutId(int viewType) {
                        return viewType;
                    }

                    @Override
                    public void bindData(RecyclerViewHolder holder, int position, HashMap<String,Object> item) {
                        //调用holder.getView(),getXXX()方法根据id得到控件实例，进行数据绑定即可
                        holder.getTextView(R.id.title).setText(item.get("ordername").toString());

                        Log.d("AreaFragment-bindData",item.get("sdpath").toString());
                        //用Volley加载图片
                        Util util =new Util();
                        Util.VolleyLoadPicture vlp = util.new VolleyLoadPicture(mContext, holder.getImageView(R.id.image));
                        vlp.getmImageLoader().get(item.get("sdpath").toString(), vlp.getOne_listener());


//                        Bitmap bit = BitmapFactory.decodeFile(item.get("sdpath").toString()); //自定义//路径
//                        holder.getImageView(R.id.image).setImageBitmap(bit);
                    }
                };
                recyclerView.setAdapter(mAdapter);
                //添加item点击事件监听
                ((BaseRecyclerAdapter)mAdapter).setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View itemView, int pos) {

                        //ActivityUtil.switchToFragment(getActivity(),new AreaItemFragment(),R.id.id_content);

                        Toast.makeText(getActivity(), mDataList.get(pos), Toast.LENGTH_SHORT).show();

                        ActivityUtil.putParam(getActivity(),"codeid",mDataList.get(pos));
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
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

                addAreaName = "";//默认该行政区域未被添加过，需要更新RecyclerView
                //查询订购区域代码codeid
                new QueryCityCodeIdThreadTask().execute();

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
        toolbar_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawArea mDrawArea =  new DrawArea();
                ActivityUtil.switchToFragment(getActivity(), mDrawArea,R.id.id_content);
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
                    codeIdTemp = jsonObjectResult.getString("codeid");
                    if(codeidStr.indexOf(codeIdTemp)!=-1){
                        addAreaName = "";//已被添加过，不需要更新RecyclerAdapter
                        //该区域已经被添加过
                        ActivityUtil.toastShow(getActivity(),"该区域已经被添加过");
                    }else{
                        addAreaName = provinceSelected + "-" + citiesSelected + "-" + areaSelecteds;
                        codeidStr += jsonObjectResult.getString("codeid") +"/";
                        //将缓存中的数据更新
                        ActivityUtil.changeParam(getActivity(),"locno",codeidStr);

                    }
                } else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return addAreaName;
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String s) {

            //进行行政区域缩略图生成操作
            String targetLayer = Variables.targetServerURL.concat("/0");

            String[] queryArray = { targetLayer, "ADCODE99='110100'" };
            AsyncQueryTask ayncQuery = new AsyncQueryTask();
            ayncQuery.execute(queryArray);

        }
    }
    /**
     *
     * Query Task executes asynchronously.
     *
     */
    private class AsyncQueryTask extends AsyncTask<String, Void, FeatureResult> {

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), "", "Please wait....query task is executing");

        }

        /**
         * First member in string array is the query URL; second member is the where
         * clause.
         */
        @Override
        protected FeatureResult doInBackground(String... queryArray) {
            if (queryArray == null || queryArray.length <= 1)
                return null;

            String url = queryArray[0];
            QueryParameters qParameters = new QueryParameters();
            String whereClause = queryArray[1];
            SpatialReference sr = SpatialReference.create(102100);
            //qParameters.setGeometry(new Envelope(-20147112.9593773, 557305.257274575, -6569564.7196889, 11753184.6153385));
            qParameters.setOutSpatialReference(sr);
            qParameters.setReturnGeometry(true);
            qParameters.setWhere(whereClause);

            QueryTask qTask = new QueryTask(url);

            try {
                FeatureResult results = qTask.execute(qParameters);
                return results;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(FeatureResult results) {

//            mMapView.removeAll();
//
            graphicsLayer = new GraphicsLayer();
            SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.RED);
            simpleFillSymbol.setAlpha(100);
            SimpleRenderer sr = new SimpleRenderer(simpleFillSymbol);
            graphicsLayer.setRenderer(sr);
            mMapView.addLayer(graphicsLayer);



            String message = "No result comes back";
            if (results != null) {
                int i = 0;
                Geometry[] GeosforExtent = new Geometry[(int)results.featureCount()];

                for (Object element : results) {
                    if (element instanceof Feature) {
                        Feature feature = (Feature) element;
                        // turn feature into graphic
                        Graphic graphic = new Graphic(feature.getGeometry(), feature.getSymbol(), feature.getAttributes());

                        GeosforExtent[i] = feature.getGeometry();

                        // add graphic to layer
                        graphicsLayer.addGraphic(graphic);
                        i++;
                    }
                }
                // update message with results
                message = String.valueOf(results.featureCount()) + " results have returned from query.";

                Geometry geometryUnion = GeometryEngine.union(GeosforExtent,mMapView.getSpatialReference());

                Envelope env = new Envelope();
                geometryUnion.queryEnvelope(env);

                Log.d(TAG,env.toString());
                mMapView.setExtent(env);
            }


            //mMapView.setExtent(newExtent);

            /**
             * upload image file

             */
            Bitmap bitmap=Util.getViewBitmap(mMapView);

            String FileDirectory = Util.saveMyBitmap(codeIdTemp,bitmap);//保存缩略图，并返回文件路径

            Log.d(TAG,FileDirectory);

            bitmap.recycle();
            // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
            upfile = new File(FileDirectory);
            new Thread(runnable).start();


            progress.dismiss();

            Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
            toast.show();



        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
        }
    };

    Runnable runnable=new Runnable() {

        @Override
        public void run() {
            Log.i(TAG, "runnable run");
            uploadFile(upfile);
            //handler.postDelayed(runnable, 5000);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }

    };
    /**
     * 上传图片到服务器
     *
     * @param imageFile 包含路径
     */
    public void uploadFile(File imageFile) {
        Log.i(TAG, "upload start");
        try {
            String requestUrl = Variables.serviceIP+"upload/UploadAction.do";
            //请求普通信息
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", ActivityUtil.getParam(getActivity(),"id"));
            params.put("username", ActivityUtil.getParam(getActivity(),"username"));
//            params.put("age", "21");
            params.put("fileName", imageFile.getName());

            Log.i(TAG, imageFile.getName());

            //上传文件
            FormFile formfile = new FormFile(imageFile.getName(), imageFile, "image", "application/octet-stream");

            SocketHttpRequester.post(requestUrl, params, formfile);
            Log.i(TAG, "upload success");
        } catch (Exception e) {
            Log.i(TAG, "upload error");
            e.printStackTrace();
        }
        finally {
            //            更新数据库表
            //确定添加，更新数据库的codeid字段
            new UpdateCityCodeIdThreadTask().execute();

        }
        Log.i(TAG, "upload end");
    }
    class UpdateCityCodeIdThreadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject ajsonObject = new JSONObject();
            try {

                String fileURL = Variables.serviceIP+"upload/image/"+ActivityUtil.getParam(getActivity(),"id")+"/"+upfile.getName();

                ajsonObject.put("id",id);
                ajsonObject.put("codeidStr",codeidStr);
                ajsonObject.put("ordername",addAreaName);
                //ajsonObject.put("sdpath","pathTemp");
                //http://10.2.3.222:8080/upload/image/55/2017-03-28-16-47-00_test11.png
                ajsonObject.put("sdpath",fileURL);
                ajsonObject.put("userid",ActivityUtil.getParam(getActivity(),"id"));
                ajsonObject.put("codeid",codeIdTemp);
                ajsonObject.put("geometry","000");
                RequestUtil.request(ajsonObject.toString(),"AndroidService/areaCodeInfoService");//新增订购区域信息
                RequestUtil.request(ajsonObject.toString(),"AndroidService/updateUserCodeIdService");
                ListViewUtil.addData(addAreaName,fileURL,"000");//第二个参数为缩略图显示地址
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            String test="testtest";
            //删除设备上的中间文件
            //删除设备上的中间文件
            if (upfile.delete())
            {
                Log.i(TAG, upfile.getName()+" local delete");
            }

            mAdapter.notifyDataSetChanged();
        }
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
