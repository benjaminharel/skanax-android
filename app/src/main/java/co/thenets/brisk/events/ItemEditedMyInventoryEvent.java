package co.thenets.brisk.events;

/**
 * Created by DAVID-WORK on 17/11/2015.
 */
public class ItemEditedMyInventoryEvent
{
    private String mStoreProductID;

    public ItemEditedMyInventoryEvent(String storeProductID)
    {
        mStoreProductID = storeProductID;
    }

    public String getStoreProductID()
    {
        return mStoreProductID;
    }
}
