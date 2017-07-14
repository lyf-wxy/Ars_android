package com.example.qman.myapplication.lyf.drawarea;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.areatab.AreaFragment;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.FormFile;
import com.example.qman.myapplication.utils.RequestUtil;
import com.example.qman.myapplication.utils.SocketHttpRequester;
import com.example.qman.myapplication.utils.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.example.qman.myapplication.areatab.AreaFragment.TAG;

/**
 * Created by lyf on 13/07/2017.
 */

public class generatePicFromMapviewAndUpLoadInfo {
    public static String TAG="generatePic";
    public static File upfile;
    public static Activity m_activity;
    public static String codeIdTemp;
    public static String geometry;
    public static String m_fieldName;
    public static String m_CropkindsStr;//作物种类
    public generatePicFromMapviewAndUpLoadInfo(Activity activity,File upfile,String codeIdTemp,String geometry,String m_fieldName,String m_CropkindsStr)
    {
        this.m_activity=activity;
        this.upfile=upfile;//缩略图文件
        this.codeIdTemp=codeIdTemp;//地块代号
        this.geometry=geometry;//地块几何形状字符串
        this.m_fieldName=m_fieldName;//地块名称
        this.m_CropkindsStr=m_CropkindsStr;//地块种植作物种类
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            //progress.dismiss();
            // TODO
            // UI界面的更新等相关操作
        }
    };

    public static Runnable runnable=new Runnable() {

        @Override
        public void run() {

            //progress = ProgressDialog.show(getActivity(), "", "Please wait....save task is executing");

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
    public static void uploadFile(File imageFile) {
        Log.i(TAG, "upload start");
        try {
            String requestUrl = Variables.serviceIP+"upload/UploadAction.do";
            //请求普通信息
            Map<String, String> params = new HashMap<String, String>();
            params.put("userid", ActivityUtil.getParam(m_activity,"id"));
            params.put("username", ActivityUtil.getParam(m_activity,"username"));
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

    public static class UpdateCityCodeIdThreadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject ajsonObject = new JSONObject();
            try {

                String fileURL = Variables.serviceIP+"upload/image/"+ActivityUtil.getParam(m_activity,"id")+"/"+upfile.getName();

                String id = ActivityUtil.getParam(m_activity,"id");//用户id
                ajsonObject.put("id",id);

                String codeidStr = ActivityUtil.getParam(m_activity,"locno");//intent.getStringExtra("locno");
                codeidStr += codeIdTemp+"/";



                ajsonObject.put("codeidStr",codeidStr);
                ajsonObject.put("ordername",m_fieldName);
                ajsonObject.put("cropkinds",m_CropkindsStr);
                //ajsonObject.put("sdpath","pathTemp");
                //http://10.2.3.222:8080/upload/image/55/2017-03-28-16-47-00_test11.png
                ajsonObject.put("sdpath",fileURL);
                ajsonObject.put("userid",ActivityUtil.getParam(m_activity,"id"));
                ajsonObject.put("codeid",codeIdTemp);

                ajsonObject.put("geometry",geometry);
                RequestUtil.request(ajsonObject.toString(),"AndroidService/areaCodeInfoService");//新增订购区域信息
                RequestUtil.request(ajsonObject.toString(),"AndroidService/updateUserCodeIdService");

                //将缓存中的数据更新
                ActivityUtil.changeParam(m_activity,"locno",codeidStr);
//                ListViewUtil.addData(addAreaName,fileURL,"000",codeIdTemp,"000");//第二个参数为缩略图显示地址
//                mDataList.add(codeIdTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            if (upfile.delete())
            {
                ActivityUtil.switchToFragment(m_activity, new AreaFragment(), R.id.id_content);
                Log.i(TAG, upfile.getName()+" local delete");
            }
            //mAdapter.notifyItemInserted(mAdapter.getItemCount());
            //mAdapter.notifyDataSetChanged();
        }
    }
}
