package com.example.qman.myapplication.areatab;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.GalleryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class AreaItemInfoFragment extends Fragment implements OnClickListener
{
    private String codeidStr = "";
    private Bundle savedState;//临时数据保存

    private ImageView legendPic;

    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
    private List<Integer> mDatas;
    private List<String> mdatetime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.area_item_info_fragment, container, false);
        Intent intent= getActivity().getIntent();

        initDatas();

        legendPic = (ImageView) view.findViewById(R.id.legendPic);
        //得到控件
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_horizontal);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAdapter = new GalleryAdapter(getActivity().getApplicationContext(), mDatas,mdatetime);

        mAdapter.setOnItemClickListener(new GalleryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int posiition) {

                //view.setEnabled(true);
                mAdapter.setSelectIndex(posiition);

                Toast.makeText(getActivity().getApplicationContext(),posiition+"",Toast.LENGTH_LONG).show();
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        legendPic.setImageResource(R.drawable.legend);

        return view ;
    }
    @Override
    public void onClick(View v)
    {
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
    private void initDatas()
    {
        mDatas = new ArrayList<Integer>(Arrays.asList(R.drawable.a,
                R.drawable.b, R.drawable.a, R.drawable.b, R.drawable.a,
                R.drawable.b, R.drawable.a, R.drawable.b, R.drawable.a));
        mdatetime = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8","9"));
    }

}
