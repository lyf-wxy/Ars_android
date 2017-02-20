package com.example.qman.myapplication.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private List<Integer> mDatas;
    private List<String> mdatetime;

    public GalleryAdapter(Context context, List<Integer> datats,List<String> datetime)
    {
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



        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        viewHolder.mImg.setImageResource(mDatas.get(i));
        viewHolder.mTxt.setText(mdatetime.get(i));

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
