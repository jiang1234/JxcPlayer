package com.example.player.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    public final static int INTERENT_UNKNOWN = -1;
    public final static int NO_INTERNET = 0;
    public final static int INTERENT_STATE_WIFI = 1;
    public final static int INTERENT_STATE_MOBILE = 2;
    private int networkState;
    @Override
    public void onReceive(Context context, Intent intent) {
        networkState = INTERENT_UNKNOWN;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null){
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                networkState = INTERENT_STATE_WIFI;
            }else if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                networkState = INTERENT_STATE_MOBILE;
            }
        }else{
            networkState = NO_INTERNET;
        }
        Intent netIntent = intent.putExtra("NET_TYPE",networkState);

    }
}
