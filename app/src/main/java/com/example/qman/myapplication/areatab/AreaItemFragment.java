package com.example.qman.myapplication.areatab;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;

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
    private SearchView mSearchview;
    private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();

    private Bundle savedState;//临时数据保存

    private String mField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.area_item_fragment, container, false);
        productType = ActivityUtil.getParam(getActivity(),"producttype");

        //recycleView
        recyclerView = (RecyclerView) view.findViewById(R.id.prodeucTypeRecyclerView);
        mSearchview = (SearchView) view.findViewById(R.id.searchView);
        final List<String> mDataList = new ArrayList<>();
        String[] productTypes = productType.split("/");
        for (int i = 0; i < productTypes.length; i++) {
            mDataList.add(CheckBoxUtil.getChineseName(productTypes[i]));
        }

        Bundle args = getArguments();
        if(args!=null)
        {
            mField = args.getString("field");
            Log.d("AreaItemFragment",mField);
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
                ActivityUtil.switchToFragment(getActivity(),new AreaItemInfoFragment(),R.id.id_content);

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

        mSearchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Toast.makeText(getActivity().getApplicationContext(), query, Toast.LENGTH_LONG).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view ;
    }

    @Override
    public void onClick(View v) {
        //跳转到AreaItemInfoFragment
        ActivityUtil.switchToFragment(getActivity(), new AreaItemInfoFragment(), R.id.id_content);
    }
}
