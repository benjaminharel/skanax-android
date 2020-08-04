package co.thenets.brisk.events;

/**
 * Created by DAVID-WORK on 08/12/2015.
 */
public class CartItemsRemovedEvent
{
    private String mStoreID;
    private String mStoreProductID;

    public CartItemsRemovedEvent(String storeID, String storeProductID)
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
