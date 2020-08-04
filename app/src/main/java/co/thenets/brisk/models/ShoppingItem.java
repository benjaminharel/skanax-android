package co.thenets.brisk.models;

/**
 * Created by DAVID-WORK on 30/12/2015.
 */
public class ShoppingItem
{
    private String mID;
    private int mAmount;
    private StoreProduct mStoreProduct;

    /**
     * Add new Store Product
     * @param storeProduct - Store product object
     * @param amount - Current amount
     */
    public ShoppingItem(StoreProduct storeProduct, int amount)
    {
        mStoreProduct = storeProduct;
        mID = mStoreProduct.getID();
        mAmount = amount;
    }

    public String getID()
    {
        return mID;
    }

    public int getAmount()
    {
        return mAmount;
    }

    public void setAmount(int amount)
    {
        mAmount = amount;
    }

    public StoreProduct getStoreProduct()
    {
        return mStoreProduct;
    }

    public void setActiveProduct(StoreProduct storeProduct)
    {
        mStoreProduct = storeProduct;
    }
}
