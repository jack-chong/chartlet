package com.jack.chartlet.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

/**
 * 作者: jack(黄冲)
 * 邮箱: 907755845@qq.com
 * create on 2018/5/30 11:42
 */

public abstract class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private List<T> mData;
    private int mLayout;
    private int mCount;
    private Object mTag;
    public Activity mAct;
    private OnItemLongClickListener<T> mOnItemLongClickListener;
    private OnItemClickListener<T> mOnItemClickListener;


    public RecyclerViewAdapter(List<T> data, @LayoutRes int layout) {
        this(data, layout, 0);
    }

    public RecyclerViewAdapter(List<T> data, @LayoutRes int layout, int count) {
        this(null, data, layout, 0);
    }

    public RecyclerViewAdapter(Activity activity, List<T> data, @LayoutRes int layout) {
        this(activity, data, layout, 0);
    }

    public RecyclerViewAdapter(Activity activity, List<T> data, @LayoutRes int layout, int count) {
        mAct = activity;
        mData = data;
        mLayout = layout;
        mCount = count;
    }


    @NonNull
    @Override
    public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(mLayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        holder.contentView.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(holder.contentView, mData == null || mData.size() <= position ? null : mData.get(position), position);
            }
        });
        holder.contentView.setOnLongClickListener(view -> {
            if (mOnItemLongClickListener != null) {
                return mOnItemLongClickListener.onClick(holder.contentView, mData == null || mData.size() <= position ? null : mData.get(position), position);
            }
            return false;
        });
        setData(holder, mData == null || mData.size() <= position ? null : mData.get(position) , position);
    }



    @Override
    public int getItemCount() {
        return mData == null ? mCount < 0 ? 0 : mCount : mData.size() + mCount < 0 ? 0 : mData.size() + mCount;
    }

    public void setData(List<T> list){
        mData = list;
        notifyDataSetChanged();
    }

    public void addDateAll(List<T> list){
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void addDate(T bean){
        mData.add(bean);
        notifyItemChanged(mData.size() - 1);
    }

    public void addDate(int position, T bean){
        mData.add(position, bean);
        notifyDataSetChanged();
    }


    public List<T> getData(){
        return mData;
    }


    public T getData(int position){
        return mData == null ? null : mData.get(position);
    }

    public void setData(int index, T bean){
        if (mData != null && mData.size() > index){
            mData.set(index, bean);
            notifyItemChanged(index);
        }
    }

    public void setCount(int count){
        mCount = count;
    }

    public int getCount(){
        return mCount;
    }

    public void setTag(Object tag){
        mTag = tag;
    }

    public <T> T getTag(){
        return (T) mTag;
    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder{

        public View contentView;
        public Context context;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            itemView.getLayoutParams().height = -2;
            contentView = itemView;
            context = itemView.getContext();
        }

        public <V extends View> V getView(@IdRes int id){
            return ((V) contentView.findViewById(id));
        }

        public void setText(@IdRes int id, String text){
            ((TextView) getView(id)).setText(text);
        }

        public void setText(@IdRes int id, int text){
            setText(id, String.valueOf(text));
        }

        public void setImageResource(@IdRes int id, @DrawableRes int res){
            ((ImageView) getView(id)).setImageResource(res);
        }

        public TextView getTV(@IdRes int id){
            return getView(id);
        }

        public ImageView getIV(@IdRes int id){
            return getView(id);
        }

        public <V extends View>V getView(@IdRes int id, Class<V> cls){
            return ((V) getView(id));
        }

        public void setImageUrl(@IdRes int id, String url) {
            if (TextUtils.isEmpty(url)){
                getIV(id).setImageBitmap(null);
            }else {
//                Picasso.get().load(url).noPlaceholder().into(getIV(id));
            }
        }
    }


    protected abstract void setData(RecyclerViewAdapter.RecyclerViewHolder holder, T bean, int position);

    public interface OnItemClickListener<T>{
        void onClick(View contentView, T bean, int position);
    }
    public interface OnItemLongClickListener<T>{
        boolean onClick(View contentView, T bean, int position);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener<T> onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener<T> onItemLongClickListener){
        mOnItemLongClickListener = onItemLongClickListener;
    }


    public OnItemClickListener<T> getOnItemClickListener() {
        return mOnItemClickListener;
    }
}
