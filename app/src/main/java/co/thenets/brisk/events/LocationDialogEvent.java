package co.thenets.brisk.events;

/**
 * Created by DAVID-WORK on 12/04/2016.
 */
public class LocationDialogEvent
{
    private boolean isRetry;

    public LocationDialogEvent(boolean isRetry)
    {
        this.isRetry = isRetry;
    }

    public boolean isRetry()
    {
        return isRetry;
    }

    public void setRetry(boolean retry)
    {
        isRetry = retry;
    }
}
