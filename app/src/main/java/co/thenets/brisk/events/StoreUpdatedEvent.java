package co.thenets.brisk.events;

/**
 * Created by DAVID-WORK on 22/02/2016.
 */
public class StoreUpdatedEvent
{
    private boolean mForceStoreRebuild;

    public StoreUpdatedEvent(boolean forceStoreRebuild)
    {
        mForceStoreRebuild = forceStoreRebuild;
    }

    public boolean isForceStoreRebuild()
    {
        return mForceStoreRebuild;
    }
}
