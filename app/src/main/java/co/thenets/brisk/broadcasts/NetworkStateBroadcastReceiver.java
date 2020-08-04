package co.thenets.brisk.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import co.thenets.brisk.events.NetworkStateEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;

/**
 * Created by DAVID-WORK on 29/02/2016.
 */
public class NetworkStateBroadcastReceiver extends BroadcastReceiver
{
    public static final String LOG_TAG = NetworkStateBroadcastReceiver.class.getSimpleName();

    public void onReceive(final Context context, Intent intent)
    {
        Log.i(LOG_TAG, "Network connectivity change");
        if (intent.getExtras() != null)
        {
            NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.isConnected())
            {
                Log.i(LOG_TAG, "Network " + networkInfo.getTypeName() + " connected");
                EventsManager.getInstance().post(new NetworkStateEvent(true));
            }
        }
        if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE))
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(Params.NO_INTERNET_TIMEOUT);
                        if(!Utils.isNetworkAvailable(context))
                        {
                            Log.i(LOG_TAG, "No network connectivity");
                            UIManager.getInstance().displayNoNetworkDialog();
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }
}
