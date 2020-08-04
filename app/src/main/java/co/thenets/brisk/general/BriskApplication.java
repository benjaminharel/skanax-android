package co.thenets.brisk.general;

import android.support.multidex.MultiDexApplication;

import java.util.Timer;
import java.util.TimerTask;

import co.thenets.brisk.handlers.AppLifecycleHandler;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.DeviceManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.rest.RestClientManager;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class BriskApplication extends MultiDexApplication
{
    private AppLifecycleHandler mAppLifecycleHandler = new AppLifecycleHandler();

    // Methods for handle if app was in background
    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;
    private boolean mIsWasInBackground;
    private long mTimeTheAppGoesToBackgroundInMillis;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;

    @Override
    public void onCreate()
    {
        super.onCreate();
        setManagers();
        setOthers();
    }

    private void setManagers()
    {
        // Set Retrofit Rest Client
        // Please note that the order of the manager might matter!!
        RestClientManager.init(getApplicationContext());
        ContentManager.init(getApplicationContext());
        DeviceManager.init(getApplicationContext());
        EventsManager.init(getApplicationContext());
        UIManager.init(getApplicationContext());
        AnalyticsManager.init(getApplicationContext());
    }

    private void setOthers()
    {
        registerActivityLifecycleCallbacks(mAppLifecycleHandler);
    }

    public AppLifecycleHandler getAppLifecycleHandler()
    {
        return mAppLifecycleHandler;
    }


    // Methods for handle if app was in background
    public void startActivityTransitionTimer()
    {
        mActivityTransitionTimer = new Timer();
        mActivityTransitionTimerTask = new TimerTask()
        {
            public void run()
            {
                mTimeTheAppGoesToBackgroundInMillis = System.currentTimeMillis();
                mIsWasInBackground = true;
            }
        };

        mActivityTransitionTimer.schedule(mActivityTransitionTimerTask, MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stopActivityTransitionTimer()
    {
        if (mActivityTransitionTimerTask != null)
        {
            mActivityTransitionTimerTask.cancel();
        }

        if (mActivityTransitionTimer != null)
        {
            mActivityTransitionTimer.cancel();
        }

        mTimeTheAppGoesToBackgroundInMillis = 0;
        mIsWasInBackground = false;
    }

    // Check if the app was in background more than REFRESH_STORES_TIMEOUT
    public boolean isRefreshStoresNeeded()
    {
        if(mIsWasInBackground)
        {
            // If the activity was in background more than REFRESH_STORES_TIMEOUT, refresh active products from server
            if(mTimeTheAppGoesToBackgroundInMillis > 0 && System.currentTimeMillis() - mTimeTheAppGoesToBackgroundInMillis > Params.REFRESH_STORES_TIMEOUT)
            {
                return true;
            }
        }
        return false;
    }
}
