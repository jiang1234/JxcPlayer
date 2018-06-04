package com.example.gzs11543.jxcplayer.base;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author jiang
 * @date 2018/05/23
 *
 */
public abstract class BaseOberver<T> implements Observer<T>{
    /**
     * Network request success
     * @param t network data
     */
    public abstract void onSuccess(T t);

    /**
     * Network request failed
     * @param e throwable
     */
    public abstract void onFail(Throwable e);

    @Override
    public void onSubscribe(Disposable d){
    }

    @Override
    public void onNext(T t){
        onSuccess(t);
    }

    @Override
    public void onError(Throwable e){
        onFail(e);
    }

    @Override
    public void onComplete(){

    }
}
