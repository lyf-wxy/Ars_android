package com.example.qman.myapplication.areatab;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.loginregister.MainActivity;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.RequestUtil;
import com.example.qman.myapplication.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AreaItemFragment extends Fragment implements OnClickListener
{
    JSONObject jsonObject = null;//利用json字符串生成json对象

    private String codeidStr = "";
    private String productType = "";
    private RecyclerView recyclerView;

    private ImageView imageView;
    private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();

    private Bundle savedState;//临时数据保存

    private String mField;
    private String userid;
    private String geometry;
    private String areaTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.area_item_fragment, container, false);
        productType = ActivityUtil.getParam(getActivity(),"producttype");
        mField = ActivityUtil.getParam(getActivity(),"codeid");
        userid = ActivityUtil.getParam(getActivity(),"id");
        codeidStr =  ActivityUtil.getParam(getActivity(),"locno");
        //recycleView
        recyclerView = (RecyclerView) view.findViewById(R.id.prodeucTypeRecyclerView);

        imageView = (ImageView) view.findViewById(R.id.backdrop);
        final List<String> mDataList = new ArrayList<>();
        String[] productTypes = productType.split("/");
        for (int i = 0; i < productTypes.length; i++) {
            mDataList.add(CheckBoxUtil.getChineseName(productTypes[i]));
        }


        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        BaseRecyclerAdapter<String> mAdapter = new BaseRecyclerAdapter<String>(R.layout.area_item_fragment_cardview,getActivity(),mDataList) {
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
        //recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        recyclerView.setAdapter(mAdapter);
        //添加item点击事件监听
        ((BaseRecyclerAdapter)mAdapter).setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
                //ActivityUtil.switchToFragment(getActivity(),new AreaItemInfoFragment(),R.id.id_content);

                String selectedClass = mDataList.get(pos);
                Toast.makeText(getActivity(), mField+","+mDataList.get(pos), Toast.LENGTH_SHORT).show();

                AreaItemInfoFragment areaItemInfoFragment = new AreaItemInfoFragment();
                Bundle bundle = new Bundle();
                bundle.putString("field",mField);
                bundle.putString("selectedClass",selectedClass);
                areaItemInfoFragment.setArguments(bundle);

                Log.d("field",mField);
                Log.d("selectedClass",selectedClass);

                ActivityUtil.switchToFragment(getActivity(),areaItemInfoFragment,R.id.id_content);

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
// recyclerView.addItemDecoration(new ItemDividerDecoration(MainActivity.this, OrientationHelper.VERTICAL));

        new QueryOrdersThreadTask().execute();//查询该项信息
        return view ;
    }

    class QueryOrdersThreadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            //查询订购区域代码codeid
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("codeid",mField);
                jsonObject.put("userid",userid);
                String str = RequestUtil.request(jsonObject.toString(),"AndroidService/cityInfoService");//服务名称不规范
                JSONObject jsonObjectResult = new JSONObject(str);
                String result = jsonObjectResult.getString("result");//解析json查询结果
                if (result.equals("success")) {
                    String dataStr = jsonObjectResult.getString("data");
                    JSONArray areaLists = new JSONArray(dataStr);
                    if (areaLists.length() > 0) {
                        for (int i = 0; i < areaLists.length(); i++) {
                            JSONArray aArea = new JSONArray(areaLists.get(i).toString());
                            geometry = aArea.get(2).toString();
                            ActivityUtil.putParam(getActivity(),"geometry",geometry);
                            areaTitle = aArea.get(0).toString();
                            return aArea.get(1).toString();
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String s) {
            //用Volley加载图片
            Util util =new Util();
            Util.VolleyLoadPicture vlp = util.new VolleyLoadPicture(getActivity(), imageView);
            vlp.getmImageLoader().get(s, vlp.getOne_listener());
            ActivityUtil.setTitle(getActivity(),R.id.toolbar_title, areaTitle);
//            Bitmap bit = BitmapFactory.decodeFile(s); //自定义//路径
//            imageView.setImageBitmap(bit);

        }
    }
    @Override
    public void onClick(View v) {
        //跳转到AreaItemInfoFragment
        ActivityUtil.switchToFragment(getActivity(), new AreaItemInfoFragment(), R.id.id_content);
    }

}