package net.techguild.base;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.otto.Bus;

import net.techguild.base.data.event.AppActiveEvent;
import net.techguild.base.data.event.ConnectionActiveEvent;
import net.techguild.base.data.module.BasicModule;
import net.techguild.base.data.service.ConnectivityChangeReceiver;
import net.techguild.base.util.CLog;

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

    public void restartApp() {
        buildObjectGraphAndInject();
    }

    @Override public void onCreate() {
        super.onCreate();
        buildObjectGraphAndInject();
        Fresco.initialize(this);
        registerReceiver(new ConnectivityChangeReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    // Initialize Injection module
    private void buildObjectGraphAndInject() {
        basicGraph = ObjectGraph.create(new BasicModule(this, getSharedPreferences("fr", MODE_PRIVATE)));
        basicGraph.inject(this);
    }

    // Global injection method used in all classes that needs injection. Call this method on class creation if you need injection
    public boolean inject(Object o) {
        try {
            basicGraph.inject(o);
            return false;
        } catch (Exception e) {
            CLog.e("T", "Injection failed:", e);
        }

        return false;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
        CLog.w("T", "App Active: " + isActive);
        bus.post(new AppActiveEvent(isActive));
    }

    public void onConnectionActive() {
        bus.post(new ConnectionActiveEvent());
    }

    public void onApiError(RetrofitError error) {
        // Handle Api Errors
    }
}
