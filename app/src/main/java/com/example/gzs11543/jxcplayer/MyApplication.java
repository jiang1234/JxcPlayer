package com.example.gzs11543.jxcplayer;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MyApplication extends Application {
    private static Context context;
    private static int width;
    private static int height;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        width = dm.widthPixels;
        height = dm.heightPixels;
        setupLeakCanary();
    }

    public static Context getContext() {
        return context;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    protected void setupLeakCanary(){
        if(LeakCanary.isInAnalyzerProcess(this)){
            return;

        }
        refWatcher = LeakCanary.install(this);
    }
}
