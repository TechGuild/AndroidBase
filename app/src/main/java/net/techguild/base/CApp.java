package net.techguild.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.squareup.otto.Bus;

import net.techguild.base.data.event.AppActiveEvent;
import net.techguild.base.data.event.ConnectionActiveEvent;
import net.techguild.base.data.service.ConnectivityChangeReceiver;
import net.techguild.base.util.CLog;
import net.techguild.base.util.DaggerHelper;

import javax.inject.Inject;

import dagger.ObjectGraph;
import retrofit.RetrofitError;

public class CApp extends Application {
    @Inject Bus bus;
    private ObjectGraph basicGraph;
    private boolean isActive = false;

    // Static application method
    public static CApp get(Context context) {
        return (CApp) context.getApplicationContext();
    }

    @Override public void onCreate() {
        super.onCreate();

        DaggerHelper.initProductionModules(this);
        DaggerHelper.inject(this);

        registerReceiver(new ConnectivityChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
        CLog.w("T", "App Active: " + isActive);
        bus.post(new AppActiveEvent(isActive));
    }

    public void restart() {
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onConnectionActive() {
        bus.post(new ConnectionActiveEvent());
    }

    public void onApiError(RetrofitError error) {
        // Handle Api Errors
    }
}
