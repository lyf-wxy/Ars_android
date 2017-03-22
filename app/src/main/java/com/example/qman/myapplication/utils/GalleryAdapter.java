package com.example.qman.myapplication.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qman.myapplication.R;

import java.util.List;

/**
 * Created by lyf on 17/02/2017.
 */


public class GalleryAdapter extends
        RecyclerView.Adapter<GalleryAdapter.ViewHolder>
{
    private Animation myAlphaAnimation;//声明Animation类的对象

    private int selectIndex = -1;
    /*
    *ItemClick的回调接口 by lyf
    */
    public interface OnItemClickListener
    {
        void onItemClick(View view,int posiition);
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickLitener)
    {
        this.mOnItemClickListener = mOnItemClickLitener;
    }

    private LayoutInflater mInflater;
    private List<String> mDatas;
    private List<String> mdatetime;

    private Context mContext;
    public GalleryAdapter(Context context, List<String> datats,List<String> datetime)
    {
        mContext = context;

        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        mdatetime = datetime;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View arg0)
        {
            super(arg0);
        }

        ImageView mImg;
        TextView mTxt;
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = mInflater.inflate(R.layout.date_item,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mImg = (ImageView) view
                .findViewById(R.id.id_index_gallery_item_image);

        viewHolder.mTxt = (TextView) view
                .findViewById(R.id.id_index_gallery_item_text);

        //设置加载动画
        viewHolder.mImg.setImageResource(R.drawable.loading);
        myAlphaAnimation= AnimationUtils.loadAnimation(mContext, R.anim.imageloading);
        myAlphaAnimation.setInterpolator(new LinearInterpolator());
        viewHolder.mImg.startAnimation(myAlphaAnimation);

        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {

        //用Volley加载图片
        Util util =new Util();
        Util.VolleyLoadPicture vlp = util.new VolleyLoadPicture(mContext, viewHolder.mImg);

        vlp.getmImageLoader().get(mDatas.get(i)+"/exportImage?bbox=105.48500796400003%2C31.691026704000073%2C111.27500738500004%2C39.621025911000075&bboxSR=&size=&imageSR=&time=&format=jpgpng&pixelType=F32&noData=&noDataInterpretation=esriNoDataMatchAny&interpolation=+RSP_BilinearInterpolation&compressionQuality=&bandIds=&mosaicRule=&renderingRule=&f=image", vlp.getOne_listener());

        //viewHolder.mImg.setImageResource(mDatas.get(i));



        viewHolder.mTxt.setText(mdatetime.get(i));

        //myAlphaAnimation.cancel();
//        if(i == selectIndex){
//            viewHolder.itemView.setSelected(true);
//        }else{
//            viewHolder.itemView.setSelected(false);
//        }

        if (mOnItemClickListener!= null)
        {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(viewHolder.itemView,i);

                }
            });
        }
    }
    public void setSelectIndex(int i){
        selectIndex = i;
    }
}
