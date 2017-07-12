package com.example.qman.myapplication.indextab;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.loginregister.MainActivity;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.CheckBoxUtil;
import com.example.qman.myapplication.utils.MD5Util;
import com.example.qman.myapplication.utils.RequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
public class SettingPsdFragment extends Fragment implements View.OnClickListener
{
    private String id = "";

    private ImageView bt_back;
    private Button changesucceed;
    private JSONObject ajsonObject_CheckPsd = null;
    private JSONObject ajsonObject_CheckPsd_CallBack = null;
    private JSONObject ajsonObject_ChangeInfo = null;
    private JSONObject ajsonObject_ChangeInfo_CallBack = null;
    private JSONObject ajsonObject_Login = null;
    private JSONObject ajsonObject_Login_CallBack = null;
    private JSONObject jsonObject = null;

    private EditText usernameEt = null;
    private EditText oldPwdEt = null;
    private EditText newPwdEt = null;
    private EditText reNewPwdEt = null;

    private String usernameInput = null;
    private String oldPwdInput = null;
    private String newPwdInput = null;
    private String reNewPwdInput = null;
    private boolean psdCheckResult = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.setting_psd_fragment, container, false);
        //ActivityUtil.setTitle(getActivity(),R.id.toolbar_title,"修改密码");
        id = ActivityUtil.getParam(getActivity(),"id");
        //初始化组件
        usernameEt = (EditText) view.findViewById(R.id.usernameEt);
        oldPwdEt = (EditText) view.findViewById(R.id.oldpsdEt);
        newPwdEt = (EditText) view.findViewById(R.id.newpsdEt);
        reNewPwdEt = (EditText) view.findViewById(R.id.renewpsdEt);

        changesucceed = (Button) view.findViewById(R.id.bt_changesucceed);

        //点击输入框时将输入框内容清空
        usernameEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEt.setText(null);
            }
        });
        //点击输入框时将输入框内容清空
        oldPwdEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPwdEt.setText(null);
                oldPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        //点击输入框时将输入框内容清空
        newPwdEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPwdEt.setText(null);
                newPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        //点击输入框时将输入框内容清空
        reNewPwdEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reNewPwdEt.setText(null);
                reNewPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        changesucceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changesucceed.setBackgroundResource(R.drawable.bt_changesucceed_click);
                usernameInput = usernameEt.getText().toString().trim();
                oldPwdInput = oldPwdEt.getText().toString().trim();
                newPwdInput = newPwdEt.getText().toString().trim();
                reNewPwdInput = reNewPwdEt.getText().toString().trim();

                //密码信息有输入
                if (!TextUtils.isEmpty(usernameInput) || !TextUtils.isEmpty(oldPwdInput) || !TextUtils.isEmpty(newPwdInput) || !TextUtils.isEmpty(reNewPwdInput)) {
                    //四项均不为空
                    if(!TextUtils.isEmpty(usernameInput) && !TextUtils.isEmpty(oldPwdInput) && !TextUtils.isEmpty(newPwdInput) && !TextUtils.isEmpty(reNewPwdInput)){
                        if (!newPwdInput.equals(reNewPwdInput)) {
                            //判断两次新密码输入是否一致
                            ActivityUtil.toastShowFragment(SettingPsdFragment.this,"两次输入的密码必须一致");
                            return ;
                        } else {
                            //一致时通过，检验原密码是否正确，在CheckPsdThreadTask()的回调函数中进行修改
                            new CheckPsdThreadTask().execute();
                        }
                    }
                    else{
                        //有为空项时提醒完善密码信息
                        ActivityUtil.toastShowFragment(SettingPsdFragment.this,"请将密码信息填完整");
                    }
                }
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

    class CheckPsdThreadTask  extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            ajsonObject_CheckPsd = new JSONObject();

            try {
                ajsonObject_CheckPsd.put("id", id);
                ajsonObject_CheckPsd.put("username",usernameInput);
                ajsonObject_CheckPsd.put("password",MD5Util.getMD5String(oldPwdInput));
                RequestUtil.request(ajsonObject_CheckPsd.toString(),"AndroidService/loginService",callbackCheckPsd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {

        }
    }
    //请求后的回调接口
    private Callback callbackCheckPsd = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            ActivityUtil.toastShow(getActivity(),e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();
            try {
                ajsonObject_CheckPsd_CallBack = new JSONObject(str);
                String result =  ajsonObject_CheckPsd_CallBack.getString("result");//解析json查询结果
                //返回结果一直为fail
                if(result.equals("success")){
                    psdCheckResult = true;
                    new ChangeInfoThreadTask().execute();
                } else {
                    psdCheckResult = false;
                    //new ChangeInfoThreadTask().execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    class ChangeInfoThreadTask  extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            ajsonObject_ChangeInfo = new JSONObject();

            try {
                ajsonObject_ChangeInfo.put("username",usernameInput);
                ajsonObject_ChangeInfo.put("paswd", MD5Util.getMD5String(newPwdInput));
                ajsonObject_ChangeInfo.put("id", id);
                RequestUtil.request(ajsonObject_ChangeInfo.toString(),"AndroidService/updateUserInfoService",callback);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {


        }
    }
    class LoginThreadTask  extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            ajsonObject_Login = new JSONObject();

            try {
                ajsonObject_Login.put("username",usernameInput);
                ajsonObject_Login.put("password", MD5Util.getMD5String(newPwdInput));
                ajsonObject_Login.put("id", id);
                RequestUtil.request(ajsonObject_Login.toString(),"AndroidService/loginService",callbackLogin);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String s) {

        }
    }
    //请求后的回调接口
    private Callback callbackLogin = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            ActivityUtil.toastShow(getActivity(),e.getMessage());
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String str = response.body().string();
            try {
                ajsonObject_Login_CallBack = new JSONObject(str);
                String result =  ajsonObject_Login_CallBack.getString("result");//解析json查询结果
                if(result.equals("success")){
                    //登陆成功，带参数跳转到首页
                    Map<String,Object> params = new HashMap<String,Object>();
                    params.put("id",ajsonObject_Login_CallBack.getString("id"));
                    params.put("username",ajsonObject_Login_CallBack.getString("username"));
                    params.put("paswd",ajsonObject_Login_CallBack.getString("paswd"));
                    params.put("beginDate",ajsonObject_Login_CallBack.getString("beginDate"));
                    params.put("endDate",ajsonObject_Login_CallBack.getString("endDate"));
                    params.put("producttype",ajsonObject_Login_CallBack.getString("producttype"));
                    if(ajsonObject_Login_CallBack.getString("locno") != null)
                        params.put("locno",ajsonObject_Login_CallBack.getString("locno"));
                    ActivityUtil.switchTo(getActivity(), IndexTabMainActivity.class,params);
                } else {
                    ActivityUtil.toastShow(getActivity(),"登陆失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
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
                ajsonObject_ChangeInfo_CallBack = new JSONObject(str);
                String result =  ajsonObject_ChangeInfo_CallBack.getString("result");//解析json查询结果
                if(result.equals("success")){
                    ActivityUtil.toastShow(getActivity(),"修改成功");
                    new LoginThreadTask().execute();
                    Iterator iterator = ajsonObject_ChangeInfo_CallBack.keys();
                    while(iterator.hasNext()){
                        String key = (String) iterator.next();
                        String value = ajsonObject_ChangeInfo_CallBack.getString(key);
                        if(!key.equals("id")) {
                            //将缓存中的数据更新
                            ActivityUtil.changeParam(getActivity(), key, value);
                        }
                    }
                } else {
                    ActivityUtil.toastShow(getActivity(),"修改失败");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}