package com.example.qman.myapplication.areatab;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.qman.myapplication.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Qman on 2017/2/21.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> implements Filterable {
    protected int resource;
    protected List<T> mData,temp_data;
    protected final Context mContext;
    protected LayoutInflater mInflater;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;

    FilterUtil myFilter;
    public BaseRecyclerAdapter(int resource, Context ctx, List<T> list) {
        this.resource = resource;
        mData = (list != null) ? list : new ArrayList<T>();
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        temp_data = mData;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerViewHolder holder = new RecyclerViewHolder(mContext,
                mInflater.inflate(resource, parent, false));//getItemLayoutId(viewType)
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }
        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition());
                    return true;
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        bindData(holder, position, mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void add(int pos, T item) {
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

    public void delete(int pos) {
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    abstract public int getItemLayoutId(int viewType);

    abstract public void bindData(RecyclerViewHolder holder, int position, T item);

    @Override
    public Filter getFilter() {

        if (myFilter == null) {
            myFilter = new FilterUtil();
        }
        return myFilter;
    }
    public interface OnItemClickListener {
        public void onItemClick(View itemView, int pos);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View itemView, int pos);
    }


    class FilterUtil extends Filter {
        /**
         * 对数据进行过滤的工作
         * @param constraint adpter.getFilter().filter(newText)方法传入的newText(也就是过滤的条件)
         * @return
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<T> new_data = new ArrayList();//用来存储过滤后符合条件的值
            if (constraint != null && constraint.toString().trim().length() > 0) {
                for (int i = 0; i < temp_data.size(); i++) {
                    T content = temp_data.get(i);
                    if (content.toString().contains(constraint.toString())) {
                        new_data.add(content);
                    }
                }

            }else {
                new_data = temp_data;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.count = new_data.size();
            filterResults.values = new_data;
            return filterResults;
        }

        /**
         * 进行界面的刷新工作
         * @param constraint
         * @param results performFiltering返回的值.
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mData = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetChanged();
            }

        }
    }

}