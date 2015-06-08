package net.techguild.base.util;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

public abstract class CActivity extends FragmentActivity {
    @Override public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override protected void onResume() {
        ApplicationStateChecker.viewResumed(this);
        super.onResume();
    }

    @Override protected void onStop() {
        ApplicationStateChecker.viewStopped(this);
        super.onStop();

    }

    @Override protected void onPause() {
        ApplicationStateChecker.viewPaused(this);
        super.onPause();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
    }
}
