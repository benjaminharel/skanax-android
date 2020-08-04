package co.thenets.brisk.general;

import java.util.concurrent.TimeUnit;

/**
 * Created by DAVID-WORK on 09/11/2015.
 */
public class Params
{
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String APP_NAME = "Brisk";
    public static final String ANDROID_OS = "Android";
    public static final String SELECTED_DAY = "selectedDay";
    public static final String FROM_OR_TO = "fromOrTo";
    public static final String TIME_PICKER = "timePicker";
    public static final String ANDROID_PROVIDER_TELEPHONY_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public static final String IMAGE_SOURCE = "ImageSource";
    public static final String CROP_TYPE = "CropType";
    public static final String ORDER = "Order";
    public static final String ORDER_ID = "OrderID";
    public static final String ORDER_ROLE = "OrderRole";
    public static final String COUNTER_OF_SEND_SMS_RETRIES = "counterOfSendSmsRetries";
    public static final String CC_FORM_EXTRA_DATA = "ccFormExtraData";
    public static final String GCM_EXTRA_MESSAGE = "message";
    public static final String GCM_EXTRA_PAYLOAD = "payload";
    public static final String GCM_TYPE = "type";
    public static final String ACTIVE_CATEGORY = "activeCategory";
    public static final String ACTIVE_SUB_CATEGORY = "activeSubCategory";
    public static final String STORE_PRODUCT = "storeProduct";
    public static final String STORE = "store";
    public static final String STORE_ID = "storeID";
    public static final String PLACE_HOLDER_IMAGE_BYTE_ARRAY = "placeHolderImage";
    public static final int AVAILABILITY_START_HOUR = 0;
    public static final int AVAILABILITY_END_HOUR = 23;
    public static final int DEFAULT_AVAILABILITY_START_HOUR = 7;
    public static final int DEFAULT_AVAILABILITY_END_HOUR = 22;
    public static final float DEFAULT_SELLER_MAP_ZOOM = 17;
    public static final int MAIN_SCREEN_HASHTAGS_LIMIT = 16;
    public static final int PAGINATION_LIMIT_NORMAL = 20;
    public static final int PAGINATION_LIMIT_SMALL = 10;
    public static final int ADD_NEW_PRODUCT_SUGGESTION_LIMIT = 20;
    public static final int TIP_OPTION_1 = 5;
    public static final int TIP_OPTION_2 = 10;
    public static final int TIP_OPTION_3 = 20;
    public static final int NO_INTERNET_TIMEOUT = 10000;
    public static final long REFRESH_STORES_TIMEOUT = TimeUnit.MINUTES.toMillis(10);
    public static final int DEFAULT_NUMBER_OF_TOP_PRODUCTS_FOR_STORE = 3;
    public static final int SEARCH_LENGTH_THRESHOLD = 2;

    public static final String URL_TO_OPEN = "urlToOpen";

    // Legal urls
    public static final String PRIVACY_POLICY_URL = "https://api.brisk-app.com/docs/legal/pp";
    public static final String TERMS_AND_CONDITIONS_URL = "https://api.brisk-app.com/docs/legal/tac";
    public static final String TERMS_AND_CONDITIONS_BRISKER_URL = "https://api.brisk-app.com/docs/legal/tac_brisker";

    // Google cloud messaging params
    public static final int GOOGLE_PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int PROFILE_REQUEST = 9001;
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PUSH_NOTIFICATION_TOKEN = "pushNotificationToken";

    public static final String CUSTOMER_SERVICE_PHONE = "054-6887927";
    public static final String SUPPORT_EMAIL = "support@brisk-app.com";
    public static final String APP_DOWNLOAD_LINK = "http://bit.ly/1Sst5in";
    public static final String APP_LINK_ON_PLAY = "https://play.google.com/store/apps/details?id=co.thenets.brisk";

    public static final String ADDRESS_DATA = "addressData";
    public static final String LOCATION_DATA = "locationData";
    public static final String INTENT_RECEIVER = "intentReceiver";
    public static final String IS_WITHIN_TLV = "intentReceiver";
    public static final double TLV_SAMPLE_LATITUDE = 32.086782;
    public static final double TLV_SAMPLE_LONGITUDE = 34.789804;

    public static final String CURRENCY = "₪";
    public static final String CURRENCY_FORMAT = "%.02f";



    public static final String APPS_FLYER_DEV_KEY = "5bBAdbWbb3ozecxxu75SoD";
}
