package com.example.gzs11543.jxcplayer.base;

/**
 * @author jiang
 * @date 2018/05/23
 *
 */
public class BasePresenter<T extends BaseActivity> {
    protected T view;
    public void attachView(T view){
        this.view = view;
    }

    public void detachView(){
        view = null;
    }
}
