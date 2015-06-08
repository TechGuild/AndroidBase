package net.techguild.base.data.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import net.techguild.base.CApp;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo infoItem : info) {
                    if (infoItem.getState() == NetworkInfo.State.CONNECTED) {
                        CApp.get(context).onConnectionActive();
                        return;
                    }
                }
            }
        }
    }
}
