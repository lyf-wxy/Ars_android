package com.example.qman.myapplication.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.qman.myapplication.R;
import com.example.qman.myapplication.loginregister.FragmentOne;
import com.example.qman.myapplication.loginregister.MainActivity;
import com.example.qman.myapplication.loginregister.RegisterFragmentActivity;

import java.util.Map;

/**
 * Created by Qman on 2017/2/15.
 */

public class ActivityUtil extends AppCompatActivity {
   /* public static void jumpFormActivityToActivity(Activity fromActivity, Class<? extends Activity> toActivity){
        Intent intent = new Intent();
        intent.setClass(fromActivity, toActivity.class);
        Activity activity = new Activity();
        activity.startActivity(intent);//RegisterFragmentActivity为要打开的新窗口的类名
    }
    public void jumpFormActivityToFragment(){

    }*/

    /**
     * </br><b>title : </b>       设置Activity全屏显示
     * </br><b>description :</b>设置Activity全屏显示。
     * @param activity Activity引用
     * @param isFull true为全屏，false为非全屏
     */
    public static void setFullScreen(Activity activity,boolean isFull){
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (isFull) {
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(params);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * </br><b>title : </b>       隐藏系统标题栏
     * </br><b>description :</b>隐藏Activity的系统默认标题栏
     * @param activity Activity对象
     */
    public static void hideTitleBar(Activity activity){
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * </br><b>title : </b>       设置Activity的显示方向为垂直方向
     * </br><b>description :</b>强制设置Actiity的显示方向为垂直方向。
     * @param activity Activity对象
     */
    public static void setScreenVertical(Activity activity){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * </br><b>title : </b>       设置Activity的显示方向为横向
     * </br><b>description :</b>强制设置Actiity的显示方向为横向。
     * @param activity Activity对象
     */
    public static void setScreenHorizontal(Activity activity){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * </br><b>title : </b>     隐藏软件输入法
     * </br><b>description :</b>隐藏软件输入法
     * </br><b>time :</b>       2012-7-12 下午7:20:00
     * @param activity
     */
    public static void hideSoftInput(Activity activity){
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * </br><b>title : </b>       使UI适配输入法
     * </br><b>description :</b>使UI适配输入法
     * </br><b>time :</b>     2012-7-17 下午10:21:26
     * @param activity
     */
    public static void adjustSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * </br><b>title : </b>       跳转到某个Activity。
     * </br><b>description :</b>跳转到某个Activity
     * </br><b>time :</b>     2012-7-8 下午3:20:00
     * @param activity          本Activity
     * @param targetActivity    目标Activity的Class
     */
    public static void switchTo(Activity activity,Class<? extends Activity> targetActivity){
        switchTo(activity, new Intent(activity,targetActivity));
    }

    /**
     * </br><b>title : </b>       根据给定的Intent进行Activity跳转
     * </br><b>description :</b>根据给定的Intent进行Activity跳转
     * </br><b>time :</b>     2012-7-8 下午3:22:23
     * @param activity          Activity对象
     * @param intent            要传递的Intent对象
     */
    public static void switchTo(Activity activity,Intent intent){
        activity.startActivity(intent);
    }

    /**
     * </br><b>title : </b>       带参数进行Activity跳转
     * </br><b>description :</b>带参数进行Activity跳转
     * </br><b>time :</b>     2012-7-8 下午3:24:54
     * @param activity          Activity对象
     * @param targetActivity    目标Activity的Class
     * @param params            跳转所带的参数
     */
    public static void switchTo(Activity activity,Class<? extends Activity> targetActivity,Map<String,Object> params){
        if( null != params ){
            Intent intent = new Intent(activity,targetActivity);
            for(Map.Entry<String, Object> entry : params.entrySet()){
                setValueToIntent(intent, entry.getKey(), entry.getValue());
            }
            switchTo(activity, intent);
        }
    }

    /**
     * Activity跳转到Fragment
     * @param activity
     * @param fragment
     * @param containerViewId
     */
    public static void switchToFragment(Activity activity,Fragment fragment,int containerViewId){
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.add(containerViewId, fragment);
        tx.commit();
    }

    /**
     * Fragment跳转到Fragment
     * @param activity
     * @param fragment
     * @param containerViewId
     */
    public static void switchToFragment(Activity activity,Fragment fragment,int containerViewId,String params){
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();

        Bundle args = new Bundle();
        args.putString("param", params);
        fragment.setArguments(args);
        tx.add(containerViewId, fragment);
        tx.addToBackStack(null);
        tx.commit();
    }

/*
    public void turnToFragment(Class<? extends Fragment> fromFragmentClass, Class<? extends Fragment> toFragmentClass, Bundle args,int containerViewId) {

        FragmentManager fm = getSupportFragmentManager();
        //被切换的Fragment标签
        String fromTag = fromFragmentClass.getSimpleName();
        //切换到的Fragment标签
        String toTag = toFragmentClass.getSimpleName();
        //查找切换的Fragment
        Fragment fromFragment = fm.findFragmentByTag(fromTag);
        Fragment toFragment = fm.findFragmentByTag(toTag);
        //如果要切换到的Fragment不存在，则创建
        if (toFragment == null) {
            try {
                toFragment = toFragmentClass.newInstance();
                toFragment.setArguments(args);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        //如果有参数传递，
        if( args != null && !args.isEmpty() ) {
            toFragment.getArguments().putAll(args);
        }
        //Fragment事务
        FragmentTransaction ft = fm.beginTransaction();
        *//**
         * 如果要切换到的Fragment没有被Fragment事务添加，则隐藏被切换的Fragment，添加要切换的Fragment
         * 否则，则隐藏被切换的Fragment，显示要切换的Fragment
         *//*
        if( !toFragment.isAdded()) {
            ft.hide(fromFragment).add(containerViewId, toFragment, toTag);
        } else {
            ft.hide(fromFragment).show(toFragment);
        }
        //添加到返回堆栈
//        ft.addToBackStack(tag);
        //不保留状态提交事务
        ft.commitAllowingStateLoss();
    }*/

    /**
     * </br><b>title : </b>       带参数进行Activity跳转
     * </br><b>description :</b>带参数进行Activity跳转
     * </br><b>time :</b>     2012-7-17 下午10:22:58
     * @param activity
     * @param target
     * @param params
     */
   /* public static void switchTo(Activity activity,Class<? extends Activity> target,NameValuePair...params){
        if( null != params ){
            Intent intent = new Intent(activity,target);
            for(NameValuePair param : params){
                setValueToIntent(intent, param.getName(), param.getValue());
            }
            switchTo(activity, intent);
        }
    }*/


    /**
     * </br><b>title : </b>       显示Toast消息。
     * </br><b>description :</b>显示Toast消息，并保证运行在UI线程中
     * </br><b>time :</b>     2012-7-10 下午08:36:02
     * @param activity
     * @param message
     */
    public static void toastShow(final Activity activity,final String message){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 在Fragment中显示Toast消息
     * @param fragment
     * @param message
     */
    public static void toastShowFragment(final Fragment fragment,final String message){
        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(fragment.getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * </br><b>title : </b>       将值设置到Intent里
     * </br><b>description :</b>将值设置到Intent里
     * </br><b>time :</b>     2012-7-8 下午3:31:17
     * @param intent            Inent对象
     * @param key               Key
     * @param val               Value
     */
    public static void setValueToIntent(Intent intent, String key, Object val) {
        if (val instanceof Boolean)
            intent.putExtra(key, (Boolean) val);
        else if (val instanceof Boolean[])
            intent.putExtra(key, (Boolean[]) val);
        else if (val instanceof String)
            intent.putExtra(key, (String) val);
        else if (val instanceof String[])
            intent.putExtra(key, (String[]) val);
        else if (val instanceof Integer)
            intent.putExtra(key, (Integer) val);
        else if (val instanceof Integer[])
            intent.putExtra(key, (Integer[]) val);
        else if (val instanceof Long)
            intent.putExtra(key, (Long) val);
        else if (val instanceof Long[])
            intent.putExtra(key, (Long[]) val);
        else if (val instanceof Double)
            intent.putExtra(key, (Double) val);
        else if (val instanceof Double[])
            intent.putExtra(key, (Double[]) val);
        else if (val instanceof Float)
            intent.putExtra(key, (Float) val);
        else if (val instanceof Float[])
            intent.putExtra(key, (Float[]) val);
    }
}
