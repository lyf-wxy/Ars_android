package com.example.qman.myapplication.indextab;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.utils.ActivityUtil;

public class SettingPsdFragment extends Fragment implements View.OnClickListener
{
    private ImageView bt_back;
    private Button changesucceed;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.setting_psd_fragment, container, false);
        //ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"修改密码");
        changesucceed = (Button) view.findViewById(R.id.bt_changesucceed);
        changesucceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changesucceed.setBackgroundResource(R.drawable.bt_changesucceed_click);
            }
        });
        bt_back = (ImageView) view.findViewById(R.id.bt_back);
        /*后退按钮*/
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.switchToFragment(getActivity(),new SettingUserFragment(),R.id.id_content);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v)
    {

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //初始化组件
       /* oldPwdEt = (EditText) getActivity().findViewById(R.id.oldPwdEt);
        newPwdEt = (EditText) getActivity().findViewById(R.id.newPwdEt);
        reNewPwdEt = (EditText) getActivity().findViewById(R.id.reNewPwdEt);
        saveBtn = (Button) getActivity().findViewById(R.id.saveBtn);

        beginDateLabel=(TextView)getActivity().findViewById(R.id.beginDate);
        endDateLabel=(TextView)getActivity().findViewById(R.id.endDate);
        beginDateLabel.setText(ActivityUtil.getParam(getActivity(),"beginDate"));
        endDateLabel.setText(ActivityUtil.getParam(getActivity(),"endDate"));
        beginDate = beginDateLabel.getText().toString();
        endDate = endDateLabel.getText().toString();*/
    }

}