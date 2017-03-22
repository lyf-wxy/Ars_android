package com.example.qman.myapplication.lyf.drawarea;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.example.qman.myapplication.R;
import com.example.qman.myapplication.areatab.AreaFragment;
import com.example.qman.myapplication.utils.ActivityUtil;
import com.example.qman.myapplication.utils.IncreasingId;
import com.example.qman.myapplication.utils.RequestUtil;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static android.R.attr.bitmap;
import java.util.Date;
/**
 * Created by lyf on 08/03/2017.
 */

public class SaveDrawArea extends Fragment {
    String mDrawAreaStr;
    MapView mMapView;
    GraphicsLayer DrawAreaGraphicLayer = new GraphicsLayer();

    FloatingActionButton mSave;
    FloatingActionButton mCancel;

    EditText mDrawAreaName;
    String fieldName = "";
    String FileDirectory = "";
    public static SaveDrawArea newInstance(String graphic)
    {
        SaveDrawArea newFragment = new SaveDrawArea();
        Bundle bundle = new Bundle();
        bundle.putString("DrawAreaString",graphic);
        newFragment.setArguments(bundle);
        return newFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.savedrawarea, container, false);
        mMapView = (MapView)view.findViewById(R.id.mapofsavedrawarea);

        mSave = (FloatingActionButton)view.findViewById(R.id.saveOfSaveDrawArea);
        mCancel = (FloatingActionButton)view.findViewById(R.id.cancelOfSaveDrawArea);

        mDrawAreaName = (EditText)view.findViewById(R.id.fieldNameofDrawArea);


        /*Bundle args = getArguments();
        if(args!=null)
        {
            mDrawAreaStr = args.getString("DrawAreaString");
            Log.d("SaveDrawArea",mDrawAreaStr);
        }*/
        mDrawAreaStr = ActivityUtil.getParam(getActivity(),"DrawAreaString");
        Log.d("SaveDrawArea",mDrawAreaStr);
        try{
            JsonFactory factory = new JsonFactory();
            JsonParser jsonParser = factory.createJsonParser(mDrawAreaStr);
            MapGeometry mapGeometry = GeometryEngine.jsonToGeometry(jsonParser);
            Geometry geometry = mapGeometry.getGeometry();

            SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.YELLOW);
            simpleFillSymbol.setAlpha(100);
            simpleFillSymbol.setOutline(new SimpleLineSymbol(Color.BLACK, 4));
            Graphic graphic = new Graphic(geometry, (simpleFillSymbol));

            //DrawAreaGraphicLayer = new GraphicsLayer();
            DrawAreaGraphicLayer.addGraphic(graphic);
            mMapView.addLayer(DrawAreaGraphicLayer);

            Log.d("SaveDrawArea","added");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }


        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //"/storage/sdcard0/Arsandroid/liua.png"
                fieldName = mDrawAreaName.getText().toString();//地块名称

                Bitmap bitmap=getViewBitmap(mMapView);

                FileDirectory = saveMyBitmap(fieldName,bitmap);//保存缩略图，并返回文件路径

                bitmap.recycle();
                new AreaCodeInfoServiceThreadTask().execute();




            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.remove(this);
//                ft.show(Fragment1);
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                ft.commit();



            }
        });

        return view;
    }

    class AreaCodeInfoServiceThreadTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject ajsonObject = new JSONObject();
            try {
                String codeIdTemp = "10"+ (int)((Math.random()*9+1)*1000);
                ajsonObject.put("userid",ActivityUtil.getParam(getActivity(),"id"));
                ajsonObject.put("codeid",codeIdTemp);
                ajsonObject.put("sdpath",FileDirectory);
                ajsonObject.put("geometry",mDrawAreaStr);//mDrawAreaStr
                ajsonObject.put("ordername",fieldName);
                RequestUtil.request(ajsonObject.toString(),"AndroidService/areaCodeInfoService");//新增订购区域信息
                String newLocno = ActivityUtil.getParam(getActivity(),"locno")+codeIdTemp+"/";//拼接新的codeid字符串
                ajsonObject.put("codeidStr",newLocno);
                ActivityUtil.changeParam(getActivity(),"locno",newLocno);
                ajsonObject.put("id",ActivityUtil.getParam(getActivity(),"id"));
                RequestUtil.request(ajsonObject.toString(),"AndroidService/updateUserCodeIdService");//更新用户表中的区域codeid字段
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "";
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String s) {
            ActivityUtil.switchToFragment(getActivity(),new AreaFragment(),R.id.id_content);
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    private Bitmap getViewBitmap(MapView v) {

        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = null;
        while (cacheBitmap == null) {
            cacheBitmap = v.getDrawingMapCache(0, 0, v.getWidth(),
                    v.getHeight());
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }
    public String saveMyBitmap(String bitName,Bitmap mBitmap){

        //String FileName=this.getInnerSDCardPath() + "/" + bitName + ".png";
        //获取当前系统时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date curDate =new Date(System.currentTimeMillis());
        String DateStr = formatter.format(curDate);
        Log.d("saveMyBitmap",DateStr);

        String FileName = null;
        try{

            String FileDir = CreateFileDir("Arsandroid");
            FileName = FileDir+ "/" + DateStr+"_"+bitName + ".png";

            Log.d("saveMyBitmap",FileName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        //ShowMessage(FileName);
        File f = new File(FileName);
        try {
            f.createNewFile();
        } catch (IOException e) {
        // TODO Auto-generated catch block
            Log.e("在保存"+FileName+"图片时出错：" + e.toString(),"在保存"+FileName+"图片时出错：" + e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileName;
    }

    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }
    //创建文件夹及文件
    public String CreateFileDir(String fileDir) throws IOException {

        String fileDirectory = getSDPath()+"/"+fileDir;//文件夹路径
        File filedir = new File(fileDirectory);
        if (!filedir.exists()) {
            try {
                //按照指定的路径创建文件夹
                filedir.mkdirs();
                return fileDirectory;
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return fileDirectory;
    }
}
