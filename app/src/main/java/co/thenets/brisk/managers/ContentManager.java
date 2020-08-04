package co.thenets.brisk.managers;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.thenets.brisk.enums.Day;
import co.thenets.brisk.enums.PaymentMethodType;
import co.thenets.brisk.enums.TimeDirection;
import co.thenets.brisk.events.CartItemsRemovedEvent;
import co.thenets.brisk.events.CartUpdatedEvent;
import co.thenets.brisk.events.StoreUpdatedEvent;
import co.thenets.brisk.events.WorkingDayUpdatedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.SecurePreferences;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.SignOutListener;
import co.thenets.brisk.models.ActiveCategory;
import co.thenets.brisk.models.BriskAddress;
import co.thenets.brisk.models.Cart;
import co.thenets.brisk.models.CartItemForUpload;
import co.thenets.brisk.models.Category;
import co.thenets.brisk.models.Dashboard;
import co.thenets.brisk.models.DeviceData;
import co.thenets.brisk.models.GeoArea;
import co.thenets.brisk.models.Order;
import co.thenets.brisk.models.OrderForUpload;
import co.thenets.brisk.models.PaymentMethod;
import co.thenets.brisk.models.Product;
import co.thenets.brisk.models.ShoppingItem;
import co.thenets.brisk.models.Store;
import co.thenets.brisk.models.StoreBasicDetails;
import co.thenets.brisk.models.StoreProduct;
import co.thenets.brisk.models.User;
import co.thenets.brisk.models.WorkingDay;
import co.thenets.brisk.models.WorkingTime;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateUserRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public class ContentManager
{
    private static final String LOG_TAG = ContentManager.class.getSimpleName();
    private final Context mContext;
    private static ContentManager msInstance;
    private static SecurePreferences msSecurePrefs;

    private HashMap<String, HashMap<String, Integer>> mCart = new HashMap<>();
    private HashMap<String, Store> mStores = new HashMap<>();

    private String mPushNotificationToken = "";
    private User mUser;
    private Store mStore;
    private byte[] mStoreCroppedImageByteArray;
    private byte[] mUserCroppedImageByteArray;
    private byte[] mNewProductCroppedImageByteArray;

    private GeoArea mStoreGeoArea;
    private StoreBasicDetails mStoreBasicDetails;
    private ArrayList<Product> mProductList = new ArrayList<>();
    private ArrayList<StoreProduct> mStoreProductList = new ArrayList<>();
    private ArrayList<Cart> mCartList = new ArrayList<>();
    private ArrayList<Order> mOrderList = new ArrayList<>();
    private Order mCurrentOrder;
    private Store mSelectedStore;
    private HashMap<String, Integer> mSelectedCart;


    private ArrayList<Category> mCategoryList = new ArrayList<>();
    private ArrayList<ActiveCategory> mActiveCategoryList = new ArrayList<>();


    private static final String IS_USER_CREATED = "isUserCreated";
    private static final String IS_SELLER = "isSeller";
    private static final String IS_APP_IN_SELLER_MODE = "isAppInSellerMode";
    private static final String CURRENT_SELLER_MAP_ZOOM = "currentSellerMapZoom";
    private static final String USER_OBJECT = "userObject";
    private static final String STORE_OBJECT = "storeObject";
    private static final String CATEGORIES_OBJECT = "categoriesObject";
    private static final String LAST_ORDER_ADDRESS_OBJECT = "lastOrderAddressObject";
    private Category mSelectedCategory;
    private Category mSelectedSubCategory;
    private ActiveCategory mSelectedActiveCategory;
    private ActiveCategory mSelectedActiveSubCategory;
    private Location mCurrentLocation;
    private ArrayList<WorkingDay> mWorkingDays = new ArrayList<>();
    private BriskAddress mLastOrderAddress;
    private Dashboard mDashboard;
    private String mPaymentToken;
    private OrderForUpload mOrderForUpload;
    private ArrayList<PaymentMethod> mPaymentMethodList = new ArrayList<>();
    private PaymentMethodType mPaymentMethodForCurrentOrder;

    private ContentManager(Context context)
    {
        mContext = context;
    }

    public static ContentManager init(Context context)
    {
        if (msInstance == null)
        {
            msInstance = new ContentManager(context);
            msSecurePrefs = new SecurePreferences(context);
        }

        return msInstance;
    }

    public static ContentManager getInstance()
    {
        return msInstance;
    }

    public String getPushNotificationToken()
    {
        return msSecurePrefs.getString(Params.PUSH_NOTIFICATION_TOKEN, mPushNotificationToken);
    }

    public void setPushNotificationToken(String pushNotificationToken)
    {
        msSecurePrefs.edit().putString(Params.PUSH_NOTIFICATION_TOKEN, pushNotificationToken).commit();
    }

    private void setIsUserCreated(boolean isUserCreated)
    {
        msSecurePrefs.edit().putBoolean(IS_USER_CREATED, isUserCreated).commit();
    }

    public void signOut(final SignOutListener signOutListener)
    {
        mUser = null;
        setIsUserCreated(false);
        setIsSeller(false);
        setIsAppInSellerMode(false);
        createUser(signOutListener);
    }

    public boolean isUserRegistered()
    {
        if (ContentManager.getInstance().getUser().getPhone() != null)
        {
            return true;
        }
        return false;
    }

    /**
     * User can register on server, but still exist the process before enter first ans last name,
     * If so, the app will ask him to complete the process.
     *
     * @return
     */
    public boolean isUserCompletelyRegistered()
    {
        if ((!TextUtils.isEmpty(ContentManager.getInstance().getUser().getPhone()))
                && (!TextUtils.isEmpty(ContentManager.getInstance().getUser().getFirstName()))
                && (!TextUtils.isEmpty(ContentManager.getInstance().getUser().getLastName())))
        {
            return true;
        }
        return false;
    }

    public boolean isUserCreated()
    {
        return msSecurePrefs.getBoolean(IS_USER_CREATED, false);
    }

    private void setIsSeller(boolean isSeller)
    {
        msSecurePrefs.edit().putBoolean(IS_SELLER, isSeller).commit();
    }

    public boolean isSeller()
    {
        return msSecurePrefs.getBoolean(IS_SELLER, false);
    }

    public void setIsAppInSellerMode(boolean isAppInSellerMode)
    {
        msSecurePrefs.edit().putBoolean(IS_APP_IN_SELLER_MODE, isAppInSellerMode).commit();
    }

    public boolean isAppInSellerMode()
    {
        return msSecurePrefs.getBoolean(IS_APP_IN_SELLER_MODE, false);
    }

    public void setSellerMapZoom(float zoom)
    {
        msSecurePrefs.edit().putFloat(CURRENT_SELLER_MAP_ZOOM, zoom).commit();
    }

    public float getSellerMapZoom()
    {
        return msSecurePrefs.getFloat(CURRENT_SELLER_MAP_ZOOM, Params.DEFAULT_SELLER_MAP_ZOOM);
    }

    public void setUser(User user)
    {
        mUser = user;

        // Update Shared Pref
        setIsUserCreated(true);

        // Save User object to disk
        Utils.writeObjectToFile(mContext, mUser, USER_OBJECT);
    }

    public void resaveUserToFile()
    {
        Utils.writeObjectToFile(mContext, mUser, USER_OBJECT);
    }


    public User getUser()
    {
        if (isUserCreated())
        {
            if (mUser == null)
            {
                // load User object from disk
                mUser = (User) Utils.readObjectFromFile(mContext, USER_OBJECT);
            }
        }
        return mUser;
    }

    public void setStore(Store store, boolean isInSellerMode)
    {
        mStore = store;

        // Update Shared Pref
        setIsSeller(true);
        setIsAppInSellerMode(isInSellerMode);

        // Set store availability
        if (store.getWorkingDays() != null)
        {
            mWorkingDays = store.getWorkingDays();
        }

        // Save Store object to disk
        Utils.writeObjectToFile(mContext, mStore, STORE_OBJECT);

        EventsManager.getInstance().post(new StoreUpdatedEvent(false));
    }

    public Store getStore()
    {
        if (mStore == null)
        {
            // Load Store object from disk
            mStore = (Store) Utils.readObjectFromFile(mContext, STORE_OBJECT);
        }
        return mStore;
    }

    public Store getStoreForUpload()
    {
        Store store = ContentManager.getInstance().getStore();
        if (store == null)
        {
            store = new Store();
            store.setIsActive(true);
        }

        if (mStoreBasicDetails != null)
        {
            store.setName(mStoreBasicDetails.getName());
            store.setAcceptedPaymentList(mStoreBasicDetails.getAcceptedPaymentTypeList());
            store.setDeliveryPrice(mStoreBasicDetails.getDeliveryPrice());
            store.setImageLink(mStoreBasicDetails.getBase64EncodeImageBytesArray());
        }
        if (mStoreGeoArea != null)
        {
            store.setGeoArea(mStoreGeoArea);
        }
        // add working days if needed
        if (!mWorkingDays.isEmpty())
        {
            store.setWorkingDays(mWorkingDays);
        }

        return store;
    }

    public Store getStoreForUpdateAvailability()
    {
        Store store = ContentManager.getInstance().getStore();
        if (store == null)
        {
            store = new Store();
            store.setIsActive(true);
        }

        if (!mWorkingDays.isEmpty())
        {
            store.setWorkingDays(mWorkingDays);
        }
        else
        {
            store.setWorkingDays(new ArrayList<WorkingDay>());
        }

        return store;
    }

    public void setStoreBasicDetails(StoreBasicDetails storeBasicDetails)
    {
        mStoreBasicDetails = storeBasicDetails;
        if (mStoreCroppedImageByteArray != null)
        {
            String encodedImage = Base64.encodeToString(mStoreCroppedImageByteArray, Base64.DEFAULT);
            mStoreBasicDetails.setBase64EncodeImageBytesArray(encodedImage);
        }
    }

    public void setStoreGeoArea(GeoArea storeGeoArea)
    {
        mStoreGeoArea = storeGeoArea;
    }

    public GeoArea getStoreGeoArea()
    {
        return mStoreGeoArea;
    }

    public byte[] getStoreCroppedImageByteArray()
    {
        return mStoreCroppedImageByteArray;
    }

    public void setStoreCroppedImageByteArray(byte[] storeCroppedImageByteArray)
    {
        mStoreCroppedImageByteArray = storeCroppedImageByteArray;
    }

    public void setUserCroppedImageByteArray(byte[] userCroppedImageByteArray)
    {
        mUserCroppedImageByteArray = userCroppedImageByteArray;
    }

    public byte[] getUserCroppedImageByteArray()
    {
        return mUserCroppedImageByteArray;
    }

    public byte[] getNewProductCroppedImageByteArray()
    {
        return mNewProductCroppedImageByteArray;
    }

    public void setNewProductCroppedImageByteArray(byte[] newProductCroppedImageByteArray)
    {
        mNewProductCroppedImageByteArray = newProductCroppedImageByteArray;
    }

    public ArrayList<Product> getProductList()
    {
        return mProductList;
    }

    public void setProductList(ArrayList<Product> productList)
    {
        mProductList = productList;
    }

    public ArrayList<StoreProduct> getStoreProductList()
    {
        return mStoreProductList;
    }

    public void setStoreProductList(ArrayList<StoreProduct> storeProductList)
    {
        mStoreProductList = storeProductList;
    }

    public void addStoreProduct(StoreProduct storeProduct)
    {
        mStoreProductList.add(storeProduct);
    }

    public void updateStoreProductPrice(String storeProductID, double newPrice)
    {
        for (int i = 0; i < mStoreProductList.size(); i++)
        {
            if (mStoreProductList.get(i).getID().equals(storeProductID))
            {
                mStoreProductList.get(i).setPrice(newPrice);
                break;
            }
        }
    }

    public void removeStoreProduct(String storeProductID)
    {
        for (int i = 0; i < mStoreProductList.size(); i++)
        {
            if (mStoreProductList.get(i).getID().equals(storeProductID))
            {
                mStoreProductList.remove(i);
                break;
            }
        }
    }

    public boolean isProductExistsInMyStoreProducts(String productID)
    {
        for (StoreProduct storeProduct : mStoreProductList)
        {
            if (storeProduct.getProductID().equals(productID))
            {
                return true;
            }
        }

        return false;
    }

    public HashMap<String, ShoppingItem> getShoppingListMap()
    {
        return null;
        // TODO: handle this!
//        return mShoppingListMap;
    }


    public StoreProduct getStoreProductByProductID(String productID)
    {
        for (StoreProduct storeProduct : mStoreProductList)
        {
            if (storeProduct.getID().equals(productID))
            {
                return storeProduct;
            }
        }

        return null;
    }

    public int getCartItemsCounter()
    {
        int counter = 0;
        for (HashMap<String, Integer> storeProductsAndAmount : mCart.values())
        {
            for (Integer amount : storeProductsAndAmount.values())
            {
                counter += amount;
            }
        }
        return counter;
    }

    // Check how many times a Store Product exist in cart
    public int getItemCounterInCart(String storeID, String storeProductID)
    {
        if(mCart.containsKey(storeID))
        {
            HashMap<String, Integer> cartStoreProducts = mCart.get(storeID);
            if(cartStoreProducts.containsKey(storeProductID))
            {
                return cartStoreProducts.get(storeProductID);
            }
        }

        return 0;
    }

    public ArrayList<Cart> getCartList()
    {
        return mCartList;
    }

    public void setCartList(ArrayList<Cart> cartList)
    {
        mCartList = cartList;
    }

    public ArrayList<CartItemForUpload> getShoppingListForUpload(String storeID)
    {
        ArrayList<CartItemForUpload> cartItemForUploads = new ArrayList<>();
        HashMap<String, Integer> storeProductIDAndAmountHashMap = mCart.get(storeID);
        for (Map.Entry<String, Integer> entry : storeProductIDAndAmountHashMap.entrySet())
        {
            String storeProductID = entry.getKey();
            Integer amount = entry.getValue();
            cartItemForUploads.add(new CartItemForUpload(storeProductID, amount));
        }

        return cartItemForUploads;
    }

    public Store getSelectedStore()
    {
        return mSelectedStore;
    }

    public HashMap<String, Integer> getSelectedCart()
    {
        return mSelectedCart;
    }

    public void setSelectedCart(String storeID)
    {
        mSelectedStore = getStore(storeID);
        mSelectedCart = mCart.get(storeID);
        // TODO: Check this!
//        EventsManager.getInstance().post(new CartSelectedEvent());
    }


    public static String getApplicationName(Context context)
    {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public void setCategoryList(ArrayList<Category> categoryList)
    {
        mCategoryList = categoryList;
        Utils.writeObjectToFile(mContext, mCategoryList, CATEGORIES_OBJECT);
    }

    public ArrayList<Category> getCategoryList()
    {
        if (mCategoryList == null)
        {
            mCategoryList = (ArrayList<Category>) Utils.readObjectFromFile(mContext, CATEGORIES_OBJECT);
        }
        return mCategoryList;
    }

//    public Store getCurrentStore()
//    {
//        return mCurrentStore;
//    }
//
//    public void setCurrentStore(Store currentStore)
//    {
//        mCurrentStore = currentStore;
//    }

    public ArrayList<Order> getOrderList()
    {
        return mOrderList;
    }

    public void setOrderList(ArrayList<Order> orderList)
    {
        mOrderList = orderList;
    }

    public void createUser(final SignOutListener signOutListener)
    {
        DeviceData deviceData = DeviceManager.getInstance().getDeviceData();
        User user = new User();
        user.setDeviceData(deviceData);
        RestClientManager.getInstance().createUser(new CreateUserRequest(user), new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                signOutListener.onSignOutSucceed();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                Log.e(LOG_TAG, error.toString());
                signOutListener.onSignOutFailed();
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                Log.e(LOG_TAG, error.toString());
                signOutListener.onSignOutFailed();
            }
        });
    }


    public Order getCurrentOrder()
    {
        return mCurrentOrder;
    }

    public void setCurrentOrder(Order currentOrder)
    {
        mCurrentOrder = currentOrder;
    }

    public void setCurrentLocation(Location lastLocation)
    {
        mCurrentLocation = lastLocation;
    }

    public Location getCurrentLocation()
    {
        return mCurrentLocation;
    }

    public ArrayList<ActiveCategory> getActiveCategoryList()
    {
        return mActiveCategoryList;
    }


    public void setActiveCategoryList(ArrayList<ActiveCategory> activeCategoryList)
    {
        mActiveCategoryList.clear();
        for (ActiveCategory activeCategory : activeCategoryList)
        {
            // Remove all non active categories
            if (activeCategory.isVisible())
            {
                mActiveCategoryList.add(activeCategory);
            }
        }
    }

    public boolean isThereActiveCategoryToDisplay()
    {
        int activeProductsCounter = 0;

        if (mActiveCategoryList != null && !mActiveCategoryList.isEmpty())
        {
            for (ActiveCategory activeCategory : mActiveCategoryList)
            {
                if (activeCategory.getCounter() > 0)
                {
                    activeProductsCounter = activeProductsCounter + activeCategory.getCounter();
                }
            }

            AnalyticsManager.getInstance().mainScreenItemsCounter(activeProductsCounter);
            return true;
        }
        else
        {
            AnalyticsManager.getInstance().mainScreenItemsCounter(activeProductsCounter);
            return false;
        }
    }


    public void addWorkingDay(Day day, int from, int to)
    {
        boolean existedDay = false;

        // if that this day already exists, update time
        for (WorkingDay workingDay : mWorkingDays)
        {
            if (workingDay.getDayLabel().equals(day.getLabel()))
            {
                // Update times
                workingDay.setFrom(new WorkingTime(from));
                workingDay.setTo(new WorkingTime(to));
                existedDay = true;
            }
        }

        // If the day isn't exists already
        if (!existedDay)
        {
            mWorkingDays.add(new WorkingDay(day.getLabel(), new WorkingTime(from), new WorkingTime(to)));
        }
    }

    public void updateWorkingDay(Day day, TimeDirection timeDirection, int value)
    {
        // if that this day exists, update his time
        for (WorkingDay workingDay : mWorkingDays)
        {
            if (workingDay.getDayLabel().equals(day.getLabel()))
            {
                switch (timeDirection)
                {
                    case FROM:
                        workingDay.setFrom(new WorkingTime(value));
                        break;
                    case TO:
                        workingDay.setTo(new WorkingTime(value));
                        break;
                }

                EventsManager.getInstance().post(new WorkingDayUpdatedEvent(day));
            }
        }
    }

    public void removeWorkingDay(Day day)
    {
        ArrayList<WorkingDay> tempWorkingDays = new ArrayList<>();

        for (WorkingDay workingDay : mWorkingDays)
        {
            if (!workingDay.getDayLabel().equals(day.getLabel()))
            {
                // Add the temp array all working days except the one we want to remove
                tempWorkingDays.add(workingDay);
            }
        }

        mWorkingDays = tempWorkingDays;
    }

    public ArrayList<WorkingDay> getWorkingDays()
    {
        return mWorkingDays;
    }

    public WorkingDay getWorkingDay(Day day)
    {
        for (WorkingDay workingDay : mWorkingDays)
        {
            if (workingDay.getDayLabel().equals(day.getLabel()))
            {
                return workingDay;
            }
        }

        return null;
    }

    public ActiveCategory getSelectedActiveCategory()
    {
        return mSelectedActiveCategory;
    }

    public void setSelectedActiveCategory(ActiveCategory selectedActiveCategory)
    {
        mSelectedActiveCategory = selectedActiveCategory;
    }

    public ActiveCategory getSelectedActiveSubCategory()
    {
        return mSelectedActiveSubCategory;
    }

    public void setSelectedActiveSubCategory(ActiveCategory selectedActiveSubCategory)
    {
        mSelectedActiveSubCategory = selectedActiveSubCategory;
    }

    public void clearCategoriesSelection()
    {
        mSelectedActiveCategory = null;
        mSelectedActiveSubCategory = null;
        mSelectedCategory = null;
        mSelectedSubCategory = null;
    }

    public BriskAddress getLastOrderAddress()
    {
        if (mLastOrderAddress == null)
        {
            mLastOrderAddress = (BriskAddress) Utils.readObjectFromFile(mContext, LAST_ORDER_ADDRESS_OBJECT);
        }
        return mLastOrderAddress;
    }

    public void setLastOrderAddress(BriskAddress lastOrderAddress)
    {
        Utils.writeObjectToFile(mContext, lastOrderAddress, LAST_ORDER_ADDRESS_OBJECT);
        mLastOrderAddress = lastOrderAddress;
    }

    public Dashboard getDashboard()
    {
        return mDashboard;
    }

    public void setDashboard(Dashboard dashboard)
    {
        mDashboard = dashboard;
    }

    public String getPaymentToken()
    {
        return mPaymentToken;
    }

    public void setPaymentToken(String paymentToken)
    {
        mPaymentToken = paymentToken;
    }

    public OrderForUpload getOrderForUpload()
    {
        if (mPaymentMethodForCurrentOrder == PaymentMethodType.CREDIT_CARD)
        {
            mOrderForUpload.setPaymentMethod(getPaymentMethodForCurrentOrder());
        }
        return mOrderForUpload;
    }

    public void setOrderForUpload(OrderForUpload orderForUpload)
    {
        mOrderForUpload = orderForUpload;
    }

    public ArrayList<PaymentMethod> getPaymentMethodList()
    {
        return mPaymentMethodList;
    }

    public void setPaymentMethodList(ArrayList<PaymentMethod> paymentMethodList)
    {
        mPaymentMethodList = paymentMethodList;
    }

    public void addPaymentMethodToList(PaymentMethod paymentMethod)
    {
        mPaymentMethodList.add(paymentMethod);
    }

    public PaymentMethod getPaymentMethodForCurrentOrder()
    {
        for (PaymentMethod paymentMethod : mPaymentMethodList)
        {
            if (mPaymentMethodForCurrentOrder != null)
            {
                if (paymentMethod.getMethodType() == mPaymentMethodForCurrentOrder)
                {
                    return paymentMethod;
                }
            }
        }

        return null;
    }

    public boolean isPaymentMethodSelected()
    {
        if (mPaymentMethodForCurrentOrder != null)
        {
            return true;
        }

        return false;
    }

    public void setPaymentMethodForCurrentOrder(PaymentMethodType paymentMethodForCurrentOrder)
    {
        mPaymentMethodForCurrentOrder = paymentMethodForCurrentOrder;
    }


    public void clearCart(String storeID)
    {
        if(!TextUtils.isEmpty(storeID))
        {
            // Clear only this store from cart
            mCart.remove(storeID);
        }
        else
        {
            // Clear all cart
            mCart.clear();
        }

        EventsManager.getInstance().post(new CartUpdatedEvent("", ""));
    }

    public void addToCart(String storeID, StoreProduct storeProduct)
    {
        // Add this product to memory
        addStoreProduct(storeID, storeProduct);

        // If store exists in cart
        if (mCart.containsKey(storeID))
        {
            // Check if the store product already exist
            if (mCart.get(storeID).containsKey(storeProduct.getID()))
            {
                Integer currentAmount = mCart.get(storeID).get(storeProduct.getID());
                mCart.get(storeID).put(storeProduct.getID(), ++currentAmount);
            }
            else
            {
                mCart.get(storeID).put(storeProduct.getID(), 1);
            }
        }
        else
        {
            // store doesn`t exists in cart, add new entry:
            HashMap<String, Integer> storeProductAmountMap = new HashMap<>();
            storeProductAmountMap.put(storeProduct.getID(), 1);
            mCart.put(storeID, storeProductAmountMap);

            AnalyticsManager.getInstance().addToCart(storeProduct, 1);
        }

        EventsManager.getInstance().post(new CartUpdatedEvent(storeID, storeProduct.getID()));
    }

    public void removeFromCart(String storeID, String storeProductID, boolean removeAllAmount)
    {
        // If store exists in cart
        if (mCart.containsKey(storeID))
        {
            // if store product exist
            if (mCart.get(storeID).containsKey(storeProductID))
            {
                Integer currentAmount = mCart.get(storeID).get(storeProductID);
                if (currentAmount > 1)
                {
                    if(removeAllAmount)
                    {
                        mCart.get(storeID).remove(storeProductID);
                        if (mCart.get(storeID).isEmpty())
                        {
                            // if a specific store doesn't has any store products in the cart, remove this entry from cart
                            mCart.remove(storeID);
                            EventsManager.getInstance().post(new CartItemsRemovedEvent(storeID, storeProductID));
                        }
                        else
                        {
                            EventsManager.getInstance().post(new CartUpdatedEvent(storeID, storeProductID));
                        }
                    }
                    else
                    {
                        mCart.get(storeID).put(storeProductID, --currentAmount);
                        EventsManager.getInstance().post(new CartUpdatedEvent(storeID, storeProductID));
                    }
                }
                else if(currentAmount == 1)
                {
                    mCart.get(storeID).remove(storeProductID);
                    // if a specific store doesn't has any store products in the cart, remove this entry from cart
                    if (mCart.get(storeID).isEmpty())
                    {
                        mCart.remove(storeID);
                        EventsManager.getInstance().post(new CartItemsRemovedEvent(storeID, storeProductID));
                    }
                    else
                    {
                        EventsManager.getInstance().post(new CartUpdatedEvent(storeID, storeProductID));
                    }
                }


            }
        }
    }

    public HashMap<String, HashMap<String, Integer>> getCart()
    {
        return mCart;
    }

    public void setStoreList(ArrayList<Store> storeList)
    {
        mStores.clear();
        if(storeList != null)
        {
            for (Store store : storeList)
            {
                // Save the store to map
                mStores.put(store.getID(), store);

                // Add the store top products to map
                addStoreProducts(store.getID(), store.getStoreProducts());
            }
        }
    }

    public ArrayList<Store> getStoreList()
    {
        return new ArrayList<>(mStores.values());
    }

    public Store getStore(String storeID)
    {
        return mStores.get(storeID);
    }

    public StoreProduct getStoreProduct(String storeID, String storeProductID)
    {
        return mStores.get(storeID).getStoreProduct(storeProductID);
    }

    public void addStoreProducts(String storeID, ArrayList<StoreProduct> storeProducts)
    {
        mStores.get(storeID).addStoreProducts(storeProducts);
    }

    private void addStoreProduct(String storeID, StoreProduct storeProducts)
    {
        mStores.get(storeID).addStoreProduct(storeProducts);
    }

    public double getProductsPrice(String storeID)
    {
        double productsPrice = 0;
        HashMap<String, Integer> storeProductsAndAmount = mCart.get(storeID);
        for (Map.Entry<String, Integer> entry : storeProductsAndAmount.entrySet())
        {
            String storeProductID = entry.getKey();
            Integer amount = entry.getValue();

            StoreProduct storeProduct = getStoreProduct(storeID, storeProductID);
            productsPrice = productsPrice + storeProduct.getPrice() * amount;
        }

        return Utils.roundDouble(productsPrice);
    }
}
