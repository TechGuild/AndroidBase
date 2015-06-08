package net.techguild.base.util;

import android.app.Activity;

import net.techguild.base.CApp;

public class ApplicationStateChecker {
    private static final String pauseString = "paused";
    private static final String resumeString = "resumed";

    private static String viewLastState;
    private static boolean fromBackground = true;

    public static void viewPaused(Activity activity) {
        viewLastState = pauseString;
    }

    public static void viewStopped(Activity activity) {
        if (pauseString.equals(viewLastState)) {
            //if stop called and last event was pause then app is brought to background
            fromBackground = true;
            CApp.get(activity).setActive(false);
        }  //if
    }

    public static void viewResumed(Activity activity) {
        if (fromBackground) {
            //Do your stuff here , app is brought to foreground
            CApp.get(activity).setActive(true);
        }  //if

        fromBackground = false;
        viewLastState = resumeString;
    }

    public static boolean isActive() {
        return resumeString.equals(viewLastState);
    }
}