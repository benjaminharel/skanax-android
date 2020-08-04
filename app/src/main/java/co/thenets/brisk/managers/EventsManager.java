package co.thenets.brisk.managers;

import android.content.Context;

import co.thenets.brisk.custom.MultiThreadBus;

/**
 * Created by DAVID BELOOSESKY on 11/11/2015
 */
public class EventsManager
{
    private final Context mContext;
    private static EventsManager msInstance;
    private MultiThreadBus mBus;

    private EventsManager(Context context)
    {
        mContext = context;
        mBus = new MultiThreadBus();
    }

    public static EventsManager init(Context context)
    {
        if (msInstance == null)
        {
            msInstance = new EventsManager(context);
        }

        return msInstance;
    }

    public static EventsManager getInstance()
    {
        return msInstance;
    }

    public void post(Object object)
    {
        mBus.post(object);
    }

    public void register(Object object)
    {
        mBus.register(object);
    }

    public void unregister(Object object)
    {
        mBus.unregister(object);
    }
}


