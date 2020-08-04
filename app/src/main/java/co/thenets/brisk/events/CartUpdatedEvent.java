package co.thenets.brisk.events;

/**
 * Created by DAVID-WORK on 08/12/2015.
 */
public class CartUpdatedEvent
{
    private String mStoreID;
    private String mStoreProductID;

    public CartUpdatedEvent(String storeID, String storeProductID)
    {
        mStoreID = storeID;
        mStoreProductID = storeProductID;
    }

    public String getStoreID()
    {
        return mStoreID;
    }

    public String getStoreProductID()
    {
        return mStoreProductID;
    }
}
