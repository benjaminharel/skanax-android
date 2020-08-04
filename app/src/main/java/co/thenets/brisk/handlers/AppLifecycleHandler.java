package co.thenets.brisk.handlers;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by DAVID BELOOSESKY on 12/02/2015.
 */
public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks
{
    private static final String LOG_TAG = AppLifecycleHandler.class.getSimpleName();
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState)
    {
    }

    @Override
    public void onActivityDestroyed(Activity activity)
    {
    }

    @Override
    public void onActivityResumed(Activity activity)
    {
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity)
    {
        ++paused;
        Log.d(LOG_TAG, "application is in foreground: " + (resumed > paused));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState)
    {
    }

    @Override
    public void onActivityStarted(Activity activity)
    {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity)
    {
        ++stopped;
        Log.d(LOG_TAG, "application is visible: " + (started > stopped));
    }

    public static boolean isApplicationVisible()
    {
        return started > stopped;
    }

    public static boolean isApplicationInForeground()
    {
        return resumed > paused;
    }
}