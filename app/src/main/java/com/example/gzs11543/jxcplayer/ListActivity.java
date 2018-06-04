package com.example.gzs11543.jxcplayer;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.gzs11543.jxcplayer.adapter.ListAdapter;
import com.example.gzs11543.jxcplayer.base.BaseActivity;
import com.example.gzs11543.jxcplayer.bean.InfoBean;
import com.example.gzs11543.jxcplayer.bean.VideoBean;
import com.example.gzs11543.jxcplayer.net.VideoApi;
import com.example.gzs11543.jxcplayer.presenter.ListPresenter;
import com.example.gzs11543.jxcplayer.util.RecyclerViewUtil;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

/**
 * @author jiang
 * @date 2018/05/23
 */
public class ListActivity extends BaseActivity<ListPresenter>{
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    private List<InfoBean> mInfoList;
    private ListAdapter listAdapter;
    private boolean isLoadMore;
    private int pageId = 0;
    private boolean isEnd;
    private static final String TAG = "ListActivity";

    @Override
    public int getLayout() {
        return R.layout.list_layout;
    }

    @Override
    public void initView() {
        mInfoList = new LinkedList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(layoutManager);
        listAdapter = new ListAdapter(mInfoList,this);
        mRecyclerView.setAdapter(listAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!isLoadMore && !mRecyclerView.canScrollVertically(1)){
                    Log.i(TAG, "onScrollStateChanged: end" + pageId + "isEnd = " + isEnd);
                    if(isEnd){
                        RecyclerViewUtil.setIsBottomItem(true);
                        Toast.makeText(ListActivity.this,"end",Toast.LENGTH_SHORT).show();
                    }else{
                        isLoadMore = true;
                        presenter.getVideoList(pageId+"");
                        pageId++;
                    }

                }
            }
        });
    }

    @Override
    public void initData() {
        presenter.getVideoList(pageId+"");
        pageId++;

    }

    public void loadListInfo(VideoBean videoBean){
        VideoBean.DataBean dataBean = videoBean.getData();
        List<VideoBean.DataBean.ListBean> listBean = dataBean.getInfoList();
        if(dataBean.getEnd() == 1){
            isEnd = true;
            RecyclerViewUtil.setIsEnd(isEnd);
        }
        int start = mInfoList.size();
        for(int i = 0;i < listBean.size();i++){
            InfoBean infoBean = new InfoBean();
            infoBean.setCover(listBean.get(i).getCover());
            infoBean.setDuration(listBean.get(i).getDuration());
            infoBean.setTitle(listBean.get(i).getTitle());
            infoBean.setFlv(listBean.get(i).getFlv());
            infoBean.setHeight(listBean.get(i).getHeight());
            infoBean.setWidth(listBean.get(i).getWidth());
            mInfoList.add(infoBean);

        }
        listAdapter.notifyItemRangeInserted(start,listBean.size());
        isLoadMore = false;
    }

    @Override
    public void initPresenter() {
        VideoApi videoApi = VideoApi.getInstance();
        presenter = new ListPresenter(videoApi);

    }





}
