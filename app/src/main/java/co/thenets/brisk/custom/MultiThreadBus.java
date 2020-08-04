package co.thenets.brisk.custom;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by Roei on 24-Nov-15.
 */
public class MultiThreadBus extends Bus
{
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            super.post(event);
        }
        else
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    MultiThreadBus.super.post(event);
                }
            });
        }
    }
}
