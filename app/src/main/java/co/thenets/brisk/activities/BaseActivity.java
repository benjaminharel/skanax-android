package co.thenets.brisk.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import co.thenets.brisk.events.StoresRefreshedEvent;
import co.thenets.brisk.general.BriskApplication;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;

/**
 * Created by DAVID-WORK on 29/02/2016.
 */
public class BaseActivity extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState)
    {
        super.onCreate(savedInstanceState, persistentState);
        UIManager.getInstance().setActivityContext(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        UIManager.getInstance().setActivityContext(this);
        checkAndHandleNetworkConnectivity();

        BriskApplication application = (BriskApplication) getApplication();
        if (application.isRefreshStoresNeeded())
        {
            ContentManager.getInstance().clearCart("");
            // In case the app was in background more than REFRESH_STORES_TIMEOUT
            EventsManager.getInstance().post(new StoresRefreshedEvent());
        }

        (application).stopActivityTransitionTimer();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        ((BriskApplication) getApplication()).startActivityTransitionTimer();
    }

    private void checkAndHandleNetworkConnectivity()
    {
        if (!Utils.isNetworkAvailable(this))
        {
            UIManager.getInstance().displayNoNetworkDialog();
        }
    }
}
