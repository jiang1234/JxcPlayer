package com.example.gzs11543.jxcplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gzs11543.jxcplayer.MyApplication;
import com.example.gzs11543.jxcplayer.R;
import com.example.gzs11543.jxcplayer.VideoActivity;
import com.example.gzs11543.jxcplayer.bean.InfoBean;
import com.example.gzs11543.jxcplayer.util.ImageLoaderUtil;
import com.example.gzs11543.jxcplayer.util.decodeUnicode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter{
    private List<InfoBean> mInfoList;
    private Context mContext;

    public ListAdapter(List<InfoBean> infoList,Context context){
        this.mInfoList = infoList;
        this.mContext = context;
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.cover) ImageView cover;
        @BindView(R.id.duration) TextView duration;
        @BindView(R.id.title) TextView title;

        public InfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_item,parent,false);
        final InfoViewHolder infoViewHolder = new InfoViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = infoViewHolder.getAdapterPosition();
                Intent intent = new Intent(mContext, VideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("cover",mInfoList.get(position).getCover());
                bundle.putString("flv",mInfoList.get(position).getFlv());
                bundle.putInt("height",mInfoList.get(position).getHeight());
                bundle.putInt("width",mInfoList.get(position).getWidth());
                intent.putExtras(bundle);
                mContext.startActivity(intent);

            }
        });
        return infoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        InfoBean infoBean = mInfoList.get(position);
        InfoViewHolder infoViewHolder = (InfoViewHolder) holder;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,MyApplication.getHeight()/5);
        infoViewHolder.cover.setLayoutParams(params);
        ImageLoaderUtil.glideImageLoader(mContext,infoBean.getCover(),infoViewHolder.cover);
        infoViewHolder.duration.setText(infoBean.getDuration());
        infoViewHolder.title.setText(infoBean.getTitle());

    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }
}
