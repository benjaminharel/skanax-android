package co.thenets.brisk.events;

/**
 * Created by DAVID-WORK on 02/03/2016.
 */
public class LocationChangedEvent
{
    private boolean mIsDeviceLocationTurnsOn;

    public LocationChangedEvent(boolean isDeviceLocationTurnsOn)
    {
        mIsDeviceLocationTurnsOn = isDeviceLocationTurnsOn;
    }

    public boolean isDeviceLocationTurnsOn()
    {
        return mIsDeviceLocationTurnsOn;
    }
}
