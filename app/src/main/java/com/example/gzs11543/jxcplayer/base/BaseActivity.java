package com.example.gzs11543.jxcplayer.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.gzs11543.jxcplayer.net.VideoApi;
import com.example.gzs11543.jxcplayer.presenter.ListPresenter;

import butterknife.ButterKnife;

/**
 * @author jiang
 * @date 2018/05/23
 *
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {
    protected T presenter ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        initPresenter();
        attachView();
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }


    public void attachView(){
        presenter.attachView(this);
    }

    public void detachView(){
        presenter.detachView();
    }

    /**
     * return layout id
     * @return layout id
     */
    public abstract int getLayout();

    /**
     * initialize view
     */
    public abstract void initView();

    /**
     * initialize data
     */
    public abstract void initData();

    public abstract void initPresenter();

}
