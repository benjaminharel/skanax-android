package co.thenets.brisk.managers;

import android.content.Context;
import android.os.Bundle;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AFInAppEventType;
import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;
import java.util.Map;

import co.thenets.brisk.BuildConfig;
import co.thenets.brisk.R;
import co.thenets.brisk.enums.PaymentMethodType;
import co.thenets.brisk.models.StoreProduct;

/**
 * Created by DAVID BELOOSESKY on 11/11/2015
 */
public class AnalyticsManager
{
    private static final String SELECTED_CATEGORY = "SelectedCategory";
    private static final String SELECTED_SUB_CATEGORY = "SelectedSubCategory";
    private static final String CATEGORY = "category";
    private static final String SUB_CATEGORY = "sub_category";
    private static final String RECOMMENDED_PRODUCTS = "recommended_products";
    private static final String ACTIVE_PRODUCTS = "active_products";
    private static final String CART_SCREEN = "cart_screen";
    private static final String COMPARE_CARTS_SCREEN = "compare_carts_screen";
    private static final String SELECTED_CART_SCREEN = "selected_cart_screen";
    private static final String STORE_SCREEN = "store_screen";
    private static final String ADDRESS_SCREEN = "address_screen";
    private static final String ADD_PAYMENT_SCREEN = "add_payment_screen";
    private static final String CREDIT_CARD_ADDED = "add_payment_info";
    private static final String ORDER_FLOW_SCREEN = "order_flow_screen";
    private static final String SIGNUP_ENTER_PHONE_SCREEN = "signup_enter_phone_screen";
    private static final String SIGNUP_VERIFY_PHONE_SCREEN = "signup_verify_phone_screen";
    private static final String SIGNUP_USER_DETAILS_SCREEN = "signup_user_details_screen";
    private static final String SIGN_UP = "sign_up";

    private final Context mContext;
    private static AnalyticsManager msInstance;
    private Tracker mGoogleAnalyticsTracker;
    private FirebaseAnalytics mFirebaseAnalytics;

    private AnalyticsManager(Context context)
    {
        mContext = context;
        setGoogleAnalyticsTracker();
        setFirebaseInstance();
    }

    private void setFirebaseInstance()
    {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    public static AnalyticsManager init(Context context)
    {
        if (msInstance == null)
        {
            msInstance = new AnalyticsManager(context);
        }

        return msInstance;
    }

    public static AnalyticsManager getInstance()
    {
        return msInstance;
    }

    public void completeRegistration()
    {
        Map<String, Object> values = new HashMap<>();
        values.put(AFInAppEventParameterName.REGSITRATION_METHOD, 0);
        AppsFlyerLib.getInstance().trackEvent(mContext, AFInAppEventType.COMPLETE_REGISTRATION, values);

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, null);
    }

    public void addPaymentInfo(PaymentMethodType paymentMethodType)
    {
        Map<String, Object> values = new HashMap<>();
        values.put(AFInAppEventParameterName.SUCCESS, paymentMethodType.getValue());
        AppsFlyerLib.getInstance().trackEvent(mContext, AFInAppEventType.ADD_PAYMENT_INFO, values);

        mFirebaseAnalytics.logEvent(CREDIT_CARD_ADDED, null);
    }

    public void purchase(float totalPrice)
    {
        Map<String, Object> values = new HashMap<>();
        values.put(AFInAppEventParameterName.PRICE, totalPrice);
        AppsFlyerLib.getInstance().trackEvent(mContext, AFInAppEventType.PURCHASE, values);
    }

    private void setGoogleAnalyticsTracker()
    {
        if (mGoogleAnalyticsTracker == null)
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mGoogleAnalyticsTracker = analytics.newTracker(R.xml.global_tracker);
        }
    }

    public void setScreen(String screenName)
    {
        // don't track debug builds
        if (!BuildConfig.DEBUG)
        {
            mGoogleAnalyticsTracker.setScreenName(screenName);
            mGoogleAnalyticsTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    // Log Events

    public void categorySelected(String categoryName)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CATEGORY);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, categoryName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void subCategorySelected(String subCategoryName)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, SUB_CATEGORY);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, subCategoryName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void recommendedPressed()
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, RECOMMENDED_PRODUCTS);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void mainScreenItemsCounter(int counter)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.QUANTITY, counter);
        mFirebaseAnalytics.logEvent(ACTIVE_PRODUCTS, bundle);
    }

    public void searchEvent(String query)
    {
        if (query.length() > 2)
        {
            Map<String, Object> values = new HashMap<>();
            values.put(AFInAppEventParameterName.SEARCH_STRING, query);
            AppsFlyerLib.getInstance().trackEvent(mContext, AFInAppEventType.SEARCH, values);

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
        }
    }

    public void addToCart(StoreProduct storeProduct, int quantity)
    {
        Map<String, Object> values = new HashMap<>();
        values.put(AFInAppEventParameterName.CONTENT_ID, storeProduct.getID());
        values.put(AFInAppEventParameterName.QUANTITY, quantity);
        AppsFlyerLib.getInstance().trackEvent(mContext, AFInAppEventType.ADD_TO_CART, values);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, storeProduct.getID());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, storeProduct.getName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }

    public void cartScreen()
    {
        mFirebaseAnalytics.logEvent(CART_SCREEN, null);
    }

    public void compareCarts()
    {
        mFirebaseAnalytics.logEvent(COMPARE_CARTS_SCREEN, null);
    }

    public void selectedCartScreen()
    {
        mFirebaseAnalytics.logEvent(SELECTED_CART_SCREEN, null);
    }

    public void storeScreen()
    {
        mFirebaseAnalytics.logEvent(STORE_SCREEN, null);
    }

    public void addAddressScreen()
    {
        mFirebaseAnalytics.logEvent(ADDRESS_SCREEN, null);
    }

    public void addPaymentMethodScreen()
    {
        mFirebaseAnalytics.logEvent(ADD_PAYMENT_SCREEN, null);
    }

    public void orderFlowScreen()
    {
        mFirebaseAnalytics.logEvent(ORDER_FLOW_SCREEN, null);
    }

    public void insertPhoneScreen()
    {
        mFirebaseAnalytics.logEvent(SIGNUP_ENTER_PHONE_SCREEN, null);
    }

    public void verifyPhoneScreen()
    {
        mFirebaseAnalytics.logEvent(SIGNUP_VERIFY_PHONE_SCREEN, null);
    }

    public void insertUserDetailsScreen()
    {
        mFirebaseAnalytics.logEvent(SIGNUP_USER_DETAILS_SCREEN, null);
    }
}


