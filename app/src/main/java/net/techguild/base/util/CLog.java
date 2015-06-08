package net.techguild.base.util;

import android.util.Log;

import net.techguild.base.BuildConfig;

import java.io.Serializable;

public class CLog {
    static final boolean enabled = BuildConfig.DEBUG;

    public static void t(Serializable... messages) {
        if (enabled) {
            String message = "";
            for (Serializable msg : messages) {
                if (message.isEmpty())
                    message = msg.toString();
                else
                    message += " " + msg;
            }
            Log.d("T", message);
        }
    }

    public static void i(String tag, String string) {
        if (enabled) Log.i(tag, string);
    }

    public static void e(String tag, String string) {
        if (enabled) Log.e(tag, string);
    }

    public static void e(String tag, String string, Throwable throwable) {
        if (enabled) Log.e(tag, string, throwable);
    }

    public static void d(String tag, String string) {
        if (enabled) Log.d(tag, string);
    }

    public static void d(String tag, String string, Throwable throwable) {
        if (enabled) Log.d(tag, string, throwable);
    }

    public static void v(String tag, String string) {
        if (enabled) Log.v(tag, string);
    }

    public static void w(String tag, String string) {
        if (enabled) Log.w(tag, string);
    }
}
