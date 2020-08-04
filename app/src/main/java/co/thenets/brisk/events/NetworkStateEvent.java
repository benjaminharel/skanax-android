package co.thenets.brisk.events;

/**
 * Created by DAVID-WORK on 29/02/2016.
 */
public class NetworkStateEvent
{
    private boolean mIsOn = false;

    public NetworkStateEvent(boolean isOn)
    {
        mIsOn = isOn;
    }

    public boolean isOn()
    {
        return mIsOn;
    }
}
