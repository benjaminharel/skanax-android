package co.thenets.brisk.rest;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import co.thenets.brisk.adapters.GsonTypeAdapters;
import co.thenets.brisk.enums.AcceptedPaymentType;
import co.thenets.brisk.enums.OrderState;
import co.thenets.brisk.enums.PaymentMethodType;
import co.thenets.brisk.events.CustomerProfileDetailEvent;
import co.thenets.brisk.events.ItemAddedMyInventoryEvent;
import co.thenets.brisk.events.ItemDeletedFromMyInventoryEvent;
import co.thenets.brisk.events.OrdersStateChangedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.models.PaymentMethod;
import co.thenets.brisk.models.Product;
import co.thenets.brisk.models.StoreProduct;
import co.thenets.brisk.models.StoreProductForUpload;
import co.thenets.brisk.models.Tip;
import co.thenets.brisk.rest.requests.AddExistingProductToStoreRequest;
import co.thenets.brisk.rest.requests.AddPaymentMethodRequest;
import co.thenets.brisk.rest.requests.AddProductToStoreRequest;
import co.thenets.brisk.rest.requests.CreateCartListRequest;
import co.thenets.brisk.rest.requests.CreateOrUpdateStoreRequest;
import co.thenets.brisk.rest.requests.CreateOrderRequest;
import co.thenets.brisk.rest.requests.CreateUserRequest;
import co.thenets.brisk.rest.requests.SetOrderRatingRequest;
import co.thenets.brisk.rest.requests.TransferOrderRequest;
import co.thenets.brisk.rest.requests.UpdateOrderRequest;
import co.thenets.brisk.rest.requests.UpdateOrderStateRequest;
import co.thenets.brisk.rest.requests.UpdateProductPriceRequest;
import co.thenets.brisk.rest.requests.UpdateUserLocationRequest;
import co.thenets.brisk.rest.requests.UpdateUserRequest;
import co.thenets.brisk.rest.responses.AddPaymentMethodResponse;
import co.thenets.brisk.rest.responses.AddProductToStoreResponse;
import co.thenets.brisk.rest.responses.BaseResponse;
import co.thenets.brisk.rest.responses.CrateOrderResponse;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.responses.GetActiveCategoriesResponse;
import co.thenets.brisk.rest.responses.GetCartListResponse;
import co.thenets.brisk.rest.responses.GetCategoriesResponse;
import co.thenets.brisk.rest.responses.GetDashboardResponse;
import co.thenets.brisk.rest.responses.GetOrderResponse;
import co.thenets.brisk.rest.responses.GetOrdersResponse;
import co.thenets.brisk.rest.responses.GetPaymentMethodResponse;
import co.thenets.brisk.rest.responses.GetPaymentTokenResponse;
import co.thenets.brisk.rest.responses.GetProductsResponse;
import co.thenets.brisk.rest.responses.GetStoreProductsResponse;
import co.thenets.brisk.rest.responses.GetStoreResponse;
import co.thenets.brisk.rest.responses.GetStoresResponse;
import co.thenets.brisk.rest.responses.GetUserResponse;
import co.thenets.brisk.rest.service.ApiService;
import co.thenets.brisk.rest.service.RequestCreateOrderListener;
import co.thenets.brisk.rest.service.RequestGetStoreProductsListener;
import co.thenets.brisk.rest.service.RequestListener;
import co.thenets.brisk.rest.service.RequestProductsListener;
import co.thenets.brisk.rest.service.RequestSearchListener;
import co.thenets.brisk.rest.service.RequestStoreProductsListener;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by DAVID BELOOSESKY on 08/11/2015.
 */
public class RestClientManager
{
    private final Context mContext;
    private ApiService mApiService;
    private static RestClientManager msInstance;
    public static final String LOG_TAG = RestClientManager.class.getSimpleName();
    private String mEndPoint;
    private boolean mIsOrderInProcess = false;


    private RestClientManager(Context context)
    {

//        if (BuildConfig.DEBUG)
//        {
//            mEndPoint = ServerParams.DEV_SERVER_URL;
//        }
//        else
//        {
//            mEndPoint = ServerParams.PRODUCTION_SERVER_URL;
//        }

        mContext = context;
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .registerTypeAdapter(OrderState.class, GsonTypeAdapters.numberAsOrderStateAdapter)
                .registerTypeAdapter(PaymentMethodType.class, GsonTypeAdapters.numberAsPaymentMethodTypeAdapter)
                .registerTypeAdapter(AcceptedPaymentType.class, GsonTypeAdapters.numberAscceptedPaymentTypeAdapter)
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(AdvancedConfiguration.SERVER_URL)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(new RequestInterceptor()
                {
                    @Override
                    public void intercept(RequestFacade request)
                    {
                        if (ContentManager.getInstance().isUserCreated() && ContentManager.getInstance().getUser() != null)
                        {
                            request.addHeader(AdvancedConfiguration.USER_TOKEN, ContentManager.getInstance().getUser().getID());
                        }
                    }
                })
                .build();

        mApiService = restAdapter.create(ApiService.class);
    }

    public static RestClientManager init(Context context)
    {
        if (msInstance == null)
        {
            msInstance = new RestClientManager(context);
        }

        return msInstance;
    }

    public static RestClientManager getInstance()
    {
        return msInstance;
    }


    public void createUser(final CreateUserRequest createUserRequest, final RequestListener requestListener)
    {
        mApiService.createUser(createUserRequest, new Callback<GetUserResponse>()
        {
            @Override
            public void success(GetUserResponse getUserResponse, Response response)
            {
                if (getUserResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "User created!, userID: " + getUserResponse.getUser().getID());
                    ContentManager.getInstance().setUser(getUserResponse.getUser());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in creating User object, reason: " + getUserResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getUserResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in creating User object, reason: " + error.toString());
            }
        });
    }

    public void updateUser(final UpdateUserRequest updateUserRequest, final RequestListener requestListener)
    {
        String userID = ContentManager.getInstance().getUser().getID();
        mApiService.updateUser(userID, updateUserRequest, new Callback<GetUserResponse>()
        {
            @Override
            public void success(GetUserResponse getUserResponse, Response response)
            {
                if (getUserResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "User update!, userID: " + getUserResponse.getUser().getID());
                    ContentManager.getInstance().setUser(getUserResponse.getUser());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in update User object, reason: " + getUserResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getUserResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in update User object, reason: " + error.toString());
            }
        });
    }

    public void updateUserLocation(final UpdateUserLocationRequest updateUserLocationRequest)
    {
        String userID = ContentManager.getInstance().getUser().getID();
        mApiService.updateUserLocation(userID, updateUserLocationRequest, new Callback<Response>()
        {
            @Override
            public void success(Response response, Response response2)
            {
                Log.i(LOG_TAG, "update user location succeeded");
            }

            @Override
            public void failure(RetrofitError error)
            {
                Log.e(LOG_TAG, "update user location failed");
            }
        });
    }

    public void getUser(String userID, final RequestListener requestListener)
    {
        mApiService.getUser(userID, new Callback<GetUserResponse>()
        {
            @Override
            public void success(GetUserResponse getUserResponse, Response response)
            {
                if (getUserResponse.isSucceeded())
                {
                    ContentManager.getInstance().setUser(getUserResponse.getUser());
                    requestListener.onSuccess();
                }
                else
                {
                    requestListener.onInternalServerFailure(getUserResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
            }
        });
    }

    public void verifyCode(String userID, int verificationCode, final RequestListener requestListener)
    {
        mApiService.getVerificationCode(userID, verificationCode, new Callback<GetUserResponse>()
        {
            @Override
            public void success(GetUserResponse getUserResponse, Response response)
            {
                if (getUserResponse.isSucceeded())
                {
                    ContentManager.getInstance().setUser(getUserResponse.getUser());
                    if (!TextUtils.isEmpty(getUserResponse.getUser().getStoreID()))
                    {
                        // There is store to this user, bring the store from server
                        getStore(new RequestListener()
                        {
                            @Override
                            public void onSuccess()
                            {
                                requestListener.onSuccess();
                            }

                            @Override
                            public void onInternalServerFailure(ErrorResponse error)
                            {
                                requestListener.onSuccess();
                            }

                            @Override
                            public void onNetworkFailure(RetrofitError error)
                            {
                                requestListener.onSuccess();
                            }
                        });
                    }
                    else
                    {
                        // There is no store to this user, continue as usual
                        ContentManager.getInstance().setUser(getUserResponse.getUser());
                        requestListener.onSuccess();
                    }

                    EventsManager.getInstance().post(new CustomerProfileDetailEvent());
                }
                else
                {
                    requestListener.onInternalServerFailure(getUserResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
            }
        });
    }

    public void createStore(final CreateOrUpdateStoreRequest createOrUpdateStoreRequest, final RequestListener requestListener)
    {
        mApiService.createStore(createOrUpdateStoreRequest, new Callback<GetStoreResponse>()
        {
            @Override
            public void success(GetStoreResponse getStoreResponse, Response response)
            {
                if (getStoreResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Store created!, storeID: " + getStoreResponse.getStore().getID());
                    ContentManager.getInstance().setStore(getStoreResponse.getStore(), true);
                    ContentManager.getInstance().getUser().setStoreID(getStoreResponse.getStore().getID());
                    // Save User object to disk
                    ContentManager.getInstance().resaveUserToFile();
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in creating Store object, reason: " + getStoreResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getStoreResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in creating Store object, reason: " + error.toString());
            }
        });
    }

    public void updateStore(final CreateOrUpdateStoreRequest createOrUpdateStoreRequest, final RequestListener requestListener)
    {
        String storeID = ContentManager.getInstance().getStore().getID();
        mApiService.updateStore(storeID, createOrUpdateStoreRequest, new Callback<GetStoreResponse>()
        {
            @Override
            public void success(GetStoreResponse getStoreResponse, Response response)
            {
                if (getStoreResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Store updated!, storeID: " + getStoreResponse.getStore().getID());
                    ContentManager.getInstance().setStore(getStoreResponse.getStore(), true);
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in updating Store object, reason: " + getStoreResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getStoreResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in updating Store object, reason: " + error.toString());
            }
        });
    }

    public void getStore(final RequestListener requestListener)
    {
        String storeID = ContentManager.getInstance().getUser().getStoreID();
        mApiService.getStore(storeID, new Callback<GetStoreResponse>()
        {
            @Override
            public void success(GetStoreResponse getStoreResponse, Response response)
            {
                if (getStoreResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got Store!, storeID: " + getStoreResponse.getStore().getID());
                    ContentManager.getInstance().setStore(getStoreResponse.getStore(), false);
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting Store object, reason: " + getStoreResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getStoreResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting Store object, reason: " + error.toString());
            }
        });
    }

    public void getProducts(HashMap<String, String> params, final RequestListener requestListener)
    {
        if (params.containsKey(AdvancedConfiguration.QUERY_KEY))
        {
            String query = params.get(AdvancedConfiguration.QUERY_KEY);
            if (TextUtils.isEmpty(query))
            {
                params.remove(AdvancedConfiguration.QUERY_KEY);
            }
        }

        params.put(AdvancedConfiguration.LIMIT_KEY, String.valueOf(Params.PAGINATION_LIMIT_NORMAL));

        mApiService.getProducts(params, new Callback<GetProductsResponse>()
        {
            @Override
            public void success(GetProductsResponse getProductsResponse, Response response)
            {
                if (getProductsResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got products, storeID: " + getProductsResponse.getProductList().toString());
                    ContentManager.getInstance().setProductList(getProductsResponse.getProductList());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting products, reason: " + getProductsResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getProductsResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting products, reason: " + error.toString());
            }
        });
    }

    public void getProducts(HashMap<String, String> params, final RequestProductsListener requestProductsListener)
    {
        if (params.containsKey(AdvancedConfiguration.QUERY_KEY))
        {
            String query = params.get(AdvancedConfiguration.QUERY_KEY);
            if (TextUtils.isEmpty(query))
            {
                requestProductsListener.onSearchWithoutValue();
            }
            else
            {
                mApiService.getProducts(params, new Callback<GetProductsResponse>()
                {
                    @Override
                    public void success(GetProductsResponse getProductsResponse, Response response)
                    {
                        if (getProductsResponse.isSucceeded())
                        {
                            Log.i(LOG_TAG, "Got products, storeID: " + getProductsResponse.getProductList().toString());
                            requestProductsListener.onSuccess(getProductsResponse);
                        }
                        else
                        {
                            Log.e(LOG_TAG, "Internal server error in getting products, reason: " + getProductsResponse.getErrorResponse().getMessage());
                            requestProductsListener.onInternalServerFailure(getProductsResponse.getErrorResponse());
                        }
                    }

                    @Override
                    public void failure(RetrofitError error)
                    {
                        requestProductsListener.onNetworkFailure(error);
                        Log.e(LOG_TAG, "Retrofit-Error in getting products, reason: " + error.toString());
                    }
                });
            }
        }
    }

    public void getCategories(final RequestListener requestListener)
    {
        mApiService.getCategories(new Callback<GetCategoriesResponse>()
        {
            @Override
            public void success(GetCategoriesResponse getCategoriesResponse, Response response)
            {
                if (getCategoriesResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got categories: " + getCategoriesResponse.getCategoryList().toString());
                    ContentManager.getInstance().setCategoryList(getCategoriesResponse.getCategoryList());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting categories, reason: " + getCategoriesResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getCategoriesResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting categories, reason: " + error.toString());
            }
        });
    }

    public void getStoreProducts(final String storeID, String query, final RequestGetStoreProductsListener requestListener)
    {
        HashMap<String, String> params = new HashMap<>();

        if (!TextUtils.isEmpty(query))
        {
            params.put(AdvancedConfiguration.QUERY_KEY, query);
        }

        mApiService.getStoreProducts(storeID, params, new Callback<GetStoreProductsResponse>()
        {
            @Override
            public void success(GetStoreProductsResponse getStoreProductsResponse, Response response)
            {
                if (getStoreProductsResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got store products, storeID: " + storeID);
                    ContentManager.getInstance().setStoreProductList(getStoreProductsResponse.getStoreProductList());
                    requestListener.onSuccess(getStoreProductsResponse);
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting store products, reason: " + getStoreProductsResponse.getErrorResponse().getMessage());
                    Log.e(LOG_TAG, "StoreID: " + storeID);
                    requestListener.onInternalServerFailure(getStoreProductsResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting store products, reason: " + error.toString());
                Log.e(LOG_TAG, "StoreID: " + storeID);
            }
        });
    }

    public void getStoreProducts(final String storeID, String query, int skip, final RequestStoreProductsListener requestStoreProductsListener)
    {
        HashMap<String, String> params = new HashMap<>();

        if (!TextUtils.isEmpty(query))
        {
            params.put(AdvancedConfiguration.QUERY_KEY, query);
        }

        params.put(AdvancedConfiguration.LIMIT_KEY, String.valueOf(Params.PAGINATION_LIMIT_SMALL));
        params.put(AdvancedConfiguration.SKIP_KEY, String.valueOf(skip));

        mApiService.getStoreProducts(storeID, params, new Callback<GetStoreProductsResponse>()
        {
            @Override
            public void success(GetStoreProductsResponse getStoreProductsResponse, Response response)
            {
                if (getStoreProductsResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got store products, storeID: " + storeID);
                    ContentManager.getInstance().addStoreProducts(storeID, getStoreProductsResponse.getStoreProductList());
                    requestStoreProductsListener.onSuccess(getStoreProductsResponse);
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting store products, reason: " + getStoreProductsResponse.getErrorResponse().getMessage());
                    Log.e(LOG_TAG, "StoreID: " + storeID);
                    requestStoreProductsListener.onInternalServerFailure(getStoreProductsResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestStoreProductsListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting store products, reason: " + error.toString());
                Log.e(LOG_TAG, "StoreID: " + storeID);
            }
        });
    }

    public void getStores(final RequestListener requestListener)
    {
        final String customerID = ContentManager.getInstance().getUser().getCustomerID();
        HashMap<String, String> params = new HashMap<>();

        // Sending GPS location in this request, refresh the active products for this user on server
        Location currentLocation = ContentManager.getInstance().getCurrentLocation();
        if (currentLocation != null)
        {
            String locationAsString = String.valueOf(currentLocation.getLatitude()) + "," + String.valueOf(currentLocation.getLongitude());
            params.put(AdvancedConfiguration.LOCATION_KEY, locationAsString);
        }

        params.put(AdvancedConfiguration.LIMIT_KEY, String.valueOf(Params.PAGINATION_LIMIT_NORMAL));

        mApiService.getLocalStores(customerID, params, new Callback<GetStoresResponse>()
        {
            @Override
            public void success(GetStoresResponse getStoresResponse, Response response)
            {
                if (getStoresResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got available stores, for customerID: " + customerID);
                    ContentManager.getInstance().setStoreList(getStoresResponse.getStoreList());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting available stores, reason: " + getStoresResponse.getErrorResponse().getMessage());
                    Log.e(LOG_TAG, "CustomerID: " + customerID);
                    requestListener.onInternalServerFailure(getStoresResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting available stores, reason: " + error.toString());
                Log.e(LOG_TAG, "CustomerID: " + customerID);
            }
        });
    }

    public void searchStoreProduct(String query, final RequestSearchListener requestListener)
    {
        final String customerID = ContentManager.getInstance().getUser().getCustomerID();
        HashMap<String, String> params = new HashMap<>();

        if (!TextUtils.isEmpty(query))
        {
            params.put(AdvancedConfiguration.QUERY_KEY, query);
        }

        // Sending GPS location in this request, refresh the active products for this user on server
        Location currentLocation = ContentManager.getInstance().getCurrentLocation();
        if (currentLocation != null)
        {
            String locationAsString = String.valueOf(currentLocation.getLatitude()) + "," + String.valueOf(currentLocation.getLongitude());
            params.put(AdvancedConfiguration.LOCATION_KEY, locationAsString);
        }

        mApiService.getLocalStores(customerID, params, new Callback<GetStoresResponse>()
        {
            @Override
            public void success(GetStoresResponse getStoresResponse, Response response)
            {
                if (getStoresResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got stores with the relevant search query");
                    requestListener.onSuccess(getStoresResponse);
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting available stores, reason: " + getStoresResponse.getErrorResponse().getMessage());
                    Log.e(LOG_TAG, "CustomerID: " + customerID);
                    requestListener.onInternalServerFailure(getStoresResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting available stores, reason: " + error.toString());
                Log.e(LOG_TAG, "CustomerID: " + customerID);
            }
        });
    }

    public void getActiveCategories(final RequestListener requestListener)
    {
        final String customerID = ContentManager.getInstance().getUser().getCustomerID();
        HashMap<String, String> params = new HashMap<>();

        // Sending GPS location in this request, refresh the active products for this user on server
        Location currentLocation = ContentManager.getInstance().getCurrentLocation();
        if (currentLocation != null)
        {
            String locationAsString = String.valueOf(currentLocation.getLatitude()) + "," + String.valueOf(currentLocation.getLongitude());
            params.put(AdvancedConfiguration.LOCATION_KEY, locationAsString);
        }

        mApiService.getActiveCategories(customerID, params, new Callback<GetActiveCategoriesResponse>()
        {
            @Override
            public void success(GetActiveCategoriesResponse getActiveCategoriesResponse, Response response)
            {
                if (getActiveCategoriesResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got active categories, for customerID: " + customerID);
                    ContentManager.getInstance().setActiveCategoryList(getActiveCategoriesResponse.getActiveCategoriesList());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting active categories, reason: " + getActiveCategoriesResponse.getErrorResponse().getMessage());
                    Log.e(LOG_TAG, "CustomerID: " + customerID);
                    requestListener.onInternalServerFailure(getActiveCategoriesResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting active categories, reason: " + error.toString());
                Log.e(LOG_TAG, "CustomerID: " + customerID);
            }
        });
    }

//    public void getActiveProducts(String query, boolean refreshLocation, final RequestListener requestListener)
//    {
//        final String customerID = ContentManager.getInstance().getUser().getCustomerID();
//        HashMap<String, String> params = new HashMap<>();
//
//        if (!TextUtils.isEmpty(query))
//        {
//            params.put(AdvancedConfiguration.QUERY_KEY, query);
//        }
//
//        if (refreshLocation)
//        {
//            // Sending GPS location in this request, refresh the active products for this user on server
//            Location currentLocation = ContentManager.getInstance().getCurrentLocation();
//            if (currentLocation != null)
//            {
//                String locationAsString = String.valueOf(currentLocation.getLatitude()) + "," + String.valueOf(currentLocation.getLongitude());
//                params.put(AdvancedConfiguration.LOCATION_KEY, locationAsString);
//            }
//        }
//
//        // Add filter by category and subcategory if needed
//        if (ContentManager.getInstance().getSelectedActiveCategory() != null)
//        {
//            params.put(AdvancedConfiguration.CATEGORY_KEY, String.valueOf(ContentManager.getInstance().getSelectedActiveCategory().getCode()));
//            if (ContentManager.getInstance().getSelectedActiveSubCategory() != null)
//            {
//                params.put(AdvancedConfiguration.SUB_CATEGORY_KEY, String.valueOf(ContentManager.getInstance().getSelectedActiveSubCategory().getCode()));
//            }
//        }
//
//        params.put(AdvancedConfiguration.LIMIT_KEY, String.valueOf(Params.PAGINATION_LIMIT_NORMAL));
//
//
//        mApiService.getAvailableProducts(customerID, params, new Callback<GetAvailableProductsResponse>()
//        {
//            @Override
//            public void success(GetAvailableProductsResponse getAvailableProductsResponse, Response response)
//            {
//                if (getAvailableProductsResponse.isSucceeded())
//                {
//                    Log.i(LOG_TAG, "Got available products, for customerID: " + customerID);
//                    ContentManager.getInstance().setAvailableProductList(getAvailableProductsResponse.getAvailableProductList());
//                    requestListener.onSuccess();
//                }
//                else
//                {
//                    Log.e(LOG_TAG, "Internal server error in getting available products, reason: " + getAvailableProductsResponse.getErrorResponse().getMessage());
//                    Log.e(LOG_TAG, "CustomerID: " + customerID);
//                    requestListener.onInternalServerFailure(getAvailableProductsResponse.getErrorResponse());
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error)
//            {
//                requestListener.onNetworkFailure(error);
//                Log.e(LOG_TAG, "Retrofit-Error in getting available products, reason: " + error.toString());
//                Log.e(LOG_TAG, "CustomerID: " + customerID);
//            }
//        });
//    }

//    public void getMoreAvailableProducts(String query, int skip, final RequestMoreAvailableProductsListener requestListener)
//    {
//        final String customerID = ContentManager.getInstance().getUser().getCustomerID();
//        HashMap<String, String> params = new HashMap<>();
//
//        if (!TextUtils.isEmpty(query))
//        {
//            params.put(AdvancedConfiguration.QUERY_KEY, query);
//        }
//
//        // Add filter by category and subcategory if needed
//        if (ContentManager.getInstance().getSelectedActiveCategory() != null)
//        {
//            params.put(AdvancedConfiguration.CATEGORY_KEY, String.valueOf(ContentManager.getInstance().getSelectedActiveCategory().getCode()));
//            if (ContentManager.getInstance().getSelectedActiveSubCategory() != null)
//            {
//                params.put(AdvancedConfiguration.SUB_CATEGORY_KEY, String.valueOf(ContentManager.getInstance().getSelectedActiveSubCategory().getCode()));
//            }
//        }
//
//        params.put(AdvancedConfiguration.LIMIT_KEY, String.valueOf(Params.PAGINATION_LIMIT_NORMAL));
//        params.put(AdvancedConfiguration.SKIP_KEY, String.valueOf(skip));
//
//        mApiService.getAvailableProducts(customerID, params, new Callback<GetAvailableProductsResponse>()
//        {
//            @Override
//            public void success(GetAvailableProductsResponse getAvailableProductsResponse, Response response)
//            {
//                if (getAvailableProductsResponse.isSucceeded())
//                {
//                    Log.i(LOG_TAG, "Got available products, for customerID: " + customerID);
//                    requestListener.onSuccess(getAvailableProductsResponse);
//                }
//                else
//                {
//                    Log.e(LOG_TAG, "Internal server error in getting available products, reason: " + getAvailableProductsResponse.getErrorResponse().getMessage());
//                    Log.e(LOG_TAG, "CustomerID: " + customerID);
//                    requestListener.onInternalServerFailure(getAvailableProductsResponse.getErrorResponse());
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error)
//            {
//                requestListener.onNetworkFailure(error);
//                Log.e(LOG_TAG, "Retrofit-Error in getting available products, reason: " + error.toString());
//                Log.e(LOG_TAG, "CustomerID: " + customerID);
//            }
//        });
//    }

//    public void getStoreAvailableProducts(String query, String storeID, final RequestListener requestListener)
//    {
//        final String customerID = ContentManager.getInstance().getUser().getCustomerID();
//        HashMap<String, String> params = new HashMap<>();
//        params.put(AdvancedConfiguration.STORE_ID_KEY, storeID);
//
//        if (!TextUtils.isEmpty(query))
//        {
//            params.put(AdvancedConfiguration.QUERY_KEY, query);
//        }
//
//        mApiService.getAvailableProducts(customerID, params, new Callback<GetAvailableProductsResponse>()
//        {
//            @Override
//            public void success(GetAvailableProductsResponse getAvailableProductsResponse, Response response)
//            {
//                if (getAvailableProductsResponse.isSucceeded())
//                {
//                    Log.i(LOG_TAG, "Got available products, for customerID: " + customerID);
//                    ContentManager.getInstance().setAvailableProductList(getAvailableProductsResponse.getAvailableProductList());
//                    requestListener.onSuccess();
//                }
//                else
//                {
//                    Log.e(LOG_TAG, "Internal server error in getting available products, reason: " + getAvailableProductsResponse.getErrorResponse().getMessage());
//                    Log.e(LOG_TAG, "CustomerID: " + customerID);
//                    requestListener.onInternalServerFailure(getAvailableProductsResponse.getErrorResponse());
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error)
//            {
//                requestListener.onNetworkFailure(error);
//                Log.e(LOG_TAG, "Retrofit-Error in getting available products, reason: " + error.toString());
//                Log.e(LOG_TAG, "CustomerID: " + customerID);
//            }
//        });
//    }

    public void addNewProductToStore(StoreProduct storeProduct, final RequestListener requestListener)
    {
        final String storeID = ContentManager.getInstance().getStore().getID();
        mApiService.addProductToStore(storeID, new AddProductToStoreRequest(storeProduct), new Callback<AddProductToStoreResponse>()
        {
            @Override
            public void success(AddProductToStoreResponse addProductToStoreResponse, Response response)
            {
                if (addProductToStoreResponse.isSucceeded())
                {
                    // Add item to ContentManager
                    ContentManager.getInstance().addStoreProduct(addProductToStoreResponse.getStoreProduct());

                    // Notify listeners about item added
                    EventsManager.getInstance().post(new ItemAddedMyInventoryEvent());

                    Log.i(LOG_TAG, "Product added to store, store_product_id: " + addProductToStoreResponse.getStoreProduct().getID());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in adding storeProduct to store, reason: " + addProductToStoreResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(addProductToStoreResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in adding storeProduct to store, reason: " + error.toString());
            }
        });
    }

    public void addExistingProductToStore(StoreProductForUpload storeProductForUpload, final RequestListener requestListener)
    {
        final String storeID = ContentManager.getInstance().getStore().getID();
        mApiService.addExistingProductToStore(storeID, new AddExistingProductToStoreRequest(storeProductForUpload), new Callback<AddProductToStoreResponse>()
        {
            @Override
            public void success(AddProductToStoreResponse addProductToStoreResponse, Response response)
            {
                if (addProductToStoreResponse.isSucceeded())
                {
                    // Add item to ContentManager
                    ContentManager.getInstance().addStoreProduct(addProductToStoreResponse.getStoreProduct());

                    // Notify listeners about item added
                    EventsManager.getInstance().post(new ItemAddedMyInventoryEvent());

                    Log.i(LOG_TAG, "Product added to store, store_product_id: " + addProductToStoreResponse.getStoreProduct().getID());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in adding storeProduct to store, reason: " + addProductToStoreResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(addProductToStoreResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in adding storeProduct to store, reason: " + error.toString());
            }
        });
    }


    public void updateProductPrice(StoreProduct storeProduct, final RequestListener requestListener)
    {
        final String storeID = ContentManager.getInstance().getStore().getID();
        mApiService.updateProductPrice(storeID, storeProduct.getID(), new UpdateProductPriceRequest(storeProduct), new Callback<AddProductToStoreResponse>()
        {
            @Override
            public void success(AddProductToStoreResponse addProductToStoreResponse, Response response)
            {
                if (addProductToStoreResponse.isSucceeded())
                {
                    // Update store product price in ContentManager
                    ContentManager.getInstance().updateStoreProductPrice(addProductToStoreResponse.getStoreProduct().getID(), addProductToStoreResponse.getStoreProduct().getPrice());

                    Log.i(LOG_TAG, "Store Product price updated, store_product_id: " + addProductToStoreResponse.getStoreProduct().getID());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in updating storeProduct price, reason: " + addProductToStoreResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(addProductToStoreResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in updating storeProduct price, reason: " + error.toString());
            }
        });
    }

    public void removeProductFromStore(final String storeProductID, final RequestListener requestListener)
    {
        final String storeID = ContentManager.getInstance().getStore().getID();
        mApiService.removeProductFromStore(storeID, storeProductID, new Callback<Response>()
        {
            @Override
            public void success(Response response, Response response2)
            {
                requestListener.onSuccess();
                Log.i(LOG_TAG, "Product removed from store!");
                Log.i(LOG_TAG, "StoreID: " + storeID + " storeProductID: " + storeProductID);

                // Remove item from ContentManager
                ContentManager.getInstance().removeStoreProduct(storeProductID);

                // Notify listeners about item deleted
                EventsManager.getInstance().post(new ItemDeletedFromMyInventoryEvent());
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in removing storeProduct from store, reason: " + error.toString());
                Log.e(LOG_TAG, "StoreID:" + storeID + " storeProductID: " + storeProductID);
            }
        });
    }

    public void addProductsByBarcode(final HashMap<String, String> params, final RequestListener requestListener)
    {
        mApiService.getProducts(params, new Callback<GetProductsResponse>()
        {
            @Override
            public void success(GetProductsResponse getProductsResponse, Response response)
            {
                if (getProductsResponse.isSucceeded())
                {
                    ArrayList<Product> productList = getProductsResponse.getProductList();
                    if (!productList.isEmpty())
                    {
                        Product product = productList.get(0);

                        StoreProductForUpload storeProductForUpload = new StoreProductForUpload();
                        storeProductForUpload.setID(product.getID());
                        storeProductForUpload.setPrice(product.getMarketPrice());

                        addExistingProductToStore(storeProductForUpload, new RequestListener()
                        {
                            @Override
                            public void onSuccess()
                            {
                                requestListener.onSuccess();
                            }

                            @Override
                            public void onInternalServerFailure(ErrorResponse error)
                            {
                                requestListener.onInternalServerFailure(error);
                            }

                            @Override
                            public void onNetworkFailure(RetrofitError error)
                            {
                                requestListener.onNetworkFailure(error);
                            }
                        });
                    }
                    ContentManager.getInstance().setProductList(getProductsResponse.getProductList());
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting products, reason: " + getProductsResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getProductsResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting products, reason: " + error.toString());
            }
        });
    }

    public void compareShoppingListItems(final CreateCartListRequest createCartListRequest, final RequestListener requestListener)
    {
        String customerID = ContentManager.getInstance().getUser().getCustomerID();
        mApiService.createCartList(customerID, createCartListRequest, new Callback<GetCartListResponse>()
        {
            @Override
            public void success(GetCartListResponse getCartListResponse, Response response)
            {
                if (getCartListResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Cart list created!");
                    ContentManager.getInstance().setCartList(getCartListResponse.getCartList());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in creating CartList object, reason: " + getCartListResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getCartListResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in creating CartList object, reason: " + error.toString());
            }
        });
    }


    public void createOrder(final CreateOrderRequest createOrderRequest, final RequestCreateOrderListener requestCreateOrderListener)
    {
        if(!mIsOrderInProcess)
        {
            // Update user location for this order
            if (ContentManager.getInstance().getLastOrderAddress() != null)
            {
                createOrderRequest.getOrderForUpload().setBriskAddress(ContentManager.getInstance().getLastOrderAddress());
            }

            mApiService.createOrder(createOrderRequest, new Callback<CrateOrderResponse>()
            {
                @Override
                public void success(CrateOrderResponse crateOrderResponse, Response response)
                {
                    if (crateOrderResponse.isSucceeded())
                    {
                        // Empty cart from the content of the specific store
                        String storeID = crateOrderResponse.getOrder().getStore().getID();
                        ContentManager.getInstance().clearCart(storeID);

                        // reset selected payment method for order
                        ContentManager.getInstance().setPaymentMethodForCurrentOrder(null);

                        Log.i(LOG_TAG, "Order created on server!");
                        requestCreateOrderListener.onSuccess(crateOrderResponse.getOrder());
                    }
                    else
                    {
                        Log.e(LOG_TAG, "Internal server error in creating an order, reason: " + crateOrderResponse.getErrorResponse().getMessage());
                        requestCreateOrderListener.onInternalServerFailure(crateOrderResponse.getErrorResponse());
                    }

                    mIsOrderInProcess = false;
                }

                @Override
                public void failure(RetrofitError error)
                {
                    mIsOrderInProcess = false;
                    requestCreateOrderListener.onNetworkFailure(error);
                    Log.e(LOG_TAG, "Retrofit-Error in creating an order, reason: " + error.toString());
                }
            });
        }
    }

    public void updateOrderState(String orderID, final UpdateOrderStateRequest updateOrderStateRequest, final RequestListener requestListener)
    {
        mApiService.updateOrderState(orderID, updateOrderStateRequest, new Callback<BaseResponse>()
        {
            @Override
            public void success(BaseResponse response, Response response2)
            {
                if (response.isSucceeded())
                {
                    Log.i(LOG_TAG, "updated order state succeeded");
                    requestListener.onSuccess();
                    // Notify listeners about the cancellation or paid
                    EventsManager.getInstance().post(new OrdersStateChangedEvent());
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in updated order state, reason: " + response.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(response.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in updated order state, reason: " + error.toString());
            }
        });
    }

    public void getOrders(final RequestListener requestListener)
    {
        HashMap<String, String> params = new HashMap<>();
        if (ContentManager.getInstance().isAppInSellerMode())
        {
            params.put(AdvancedConfiguration.STORE_ID_KEY, ContentManager.getInstance().getStore().getID());
        }
        else
        {
            params.put(AdvancedConfiguration.CUSTOMER_ID_KEY, ContentManager.getInstance().getUser().getCustomerID());
        }

        mApiService.getOrders(params, new Callback<GetOrdersResponse>()
        {
            @Override
            public void success(GetOrdersResponse getOrdersResponse, Response response)
            {
                if (getOrdersResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got order list from server!");
                    ContentManager.getInstance().setOrderList(getOrdersResponse.getOrderList());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting order list, reason: " + getOrdersResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getOrdersResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting order list, reason: " + error.toString());
            }
        });
    }

    public void getOrder(String orderID, final RequestListener requestListener)
    {
        mApiService.getOrder(orderID, new Callback<GetOrderResponse>()
        {
            @Override
            public void success(GetOrderResponse getOrderResponse, Response response)
            {
                if (getOrderResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got order from server!");
                    ContentManager.getInstance().setCurrentOrder(getOrderResponse.getOrder());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting order, reason: " + getOrderResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getOrderResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting order, reason: " + error.toString());
            }
        });
    }

    public void removeItemFromOrder(String orderID, String orderItemID, final RequestListener requestListener)
    {
        mApiService.removeItemFromOrder(orderID, orderItemID, new Callback<Response>()
        {
            @Override
            public void success(Response response, Response response2)
            {
                Log.i(LOG_TAG, "Remove order item from order!");
                requestListener.onSuccess();
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in removing order item from order, reason: " + error.toString());
            }
        });
    }


    public void rateOrder(String orderID, int rating, final RequestCreateOrderListener requestCreateOrderListener)
    {
        SetOrderRatingRequest setOrderRatingRequest = new SetOrderRatingRequest(rating);
        mApiService.rateOrder(orderID, setOrderRatingRequest, new Callback<GetOrderResponse>()
        {
            @Override
            public void success(GetOrderResponse getOrderResponse, Response response)
            {
                if (getOrderResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Rated succeeded!");
                    requestCreateOrderListener.onSuccess(getOrderResponse.getOrder());
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in rating an order, reason: " + getOrderResponse.getErrorResponse().getMessage());
                    requestCreateOrderListener.onInternalServerFailure(getOrderResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestCreateOrderListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in creating an order, reason: " + error.toString());
            }
        });
    }

    public void tipOrder(String orderID, int tipValue, final RequestCreateOrderListener requestUpdateOrderListener)
    {
        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest(new Tip(tipValue));
        mApiService.tipOrder(orderID, updateOrderRequest, new Callback<GetOrderResponse>()
        {
            @Override
            public void success(GetOrderResponse getOrderResponse, Response response)
            {
                if (getOrderResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Tip succeeded!");
                    ContentManager.getInstance().setCurrentOrder(getOrderResponse.getOrder());
                    requestUpdateOrderListener.onSuccess(getOrderResponse.getOrder());
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in add tip to order, reason: " + getOrderResponse.getErrorResponse().getMessage());
                    requestUpdateOrderListener.onInternalServerFailure(getOrderResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestUpdateOrderListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in add tip to order, reason: " + error.toString());
            }
        });
    }

    public void transferOrder(String orderID, String messengerPhone, final RequestListener requestListener)
    {
        TransferOrderRequest transferOrderRequest = new TransferOrderRequest(messengerPhone);
        mApiService.transferDelivery(orderID, transferOrderRequest, new Callback<BaseResponse>()
        {
            @Override
            public void success(BaseResponse response, Response response2)
            {
                if (response.isSucceeded())
                {
                    Log.i(LOG_TAG, "Order transferred");
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in Order transferred, reason: " + response.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(response.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in Order transferred, reason: " + error.toString());
            }
        });
    }


    public void getDashboard(final RequestListener requestListener)
    {
        String storeID = ContentManager.getInstance().getStore().getID();
        mApiService.getDashboard(storeID, new Callback<GetDashboardResponse>()
        {
            @Override
            public void success(GetDashboardResponse getDashboardResponse, Response response)
            {
                if (getDashboardResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got dashboard from server!");
                    ContentManager.getInstance().setDashboard(getDashboardResponse.getDashboard());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting dashboard, reason: " + getDashboardResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getDashboardResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting dashboard, reason: " + error.toString());
            }
        });
    }

    public void getPaymentToken(final RequestListener requestListener)
    {
        String customerID = ContentManager.getInstance().getUser().getCustomerID();
        mApiService.getPaymentToken(customerID, new Callback<GetPaymentTokenResponse>()
        {
            @Override
            public void success(GetPaymentTokenResponse getPaymentTokenResponse, Response response)
            {
                if (getPaymentTokenResponse.isSucceeded())
                {
                    ContentManager.getInstance().setPaymentToken(getPaymentTokenResponse.getPaymentToken());
                    requestListener.onSuccess();
                }
                else
                {
                    requestListener.onInternalServerFailure(getPaymentTokenResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
            }
        });
    }

    public void addPaymentMethod(PaymentMethodType methodType, String authorizationCode, final RequestListener requestListener)
    {
        PaymentMethod newPaypalPaymentMethod = new PaymentMethod(methodType, authorizationCode);
        AddPaymentMethodRequest addPaymentMethodRequest = new AddPaymentMethodRequest(newPaypalPaymentMethod);

        String customerID = ContentManager.getInstance().getUser().getCustomerID();
        mApiService.addPaymentMethod(customerID, addPaymentMethodRequest, new Callback<AddPaymentMethodResponse>()
        {
            @Override
            public void success(AddPaymentMethodResponse getPaymentMethodResponse, Response response)
            {
                if (getPaymentMethodResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Payment method added!");
                    ContentManager.getInstance().addPaymentMethodToList(getPaymentMethodResponse.getPaymentMethod());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in adding payment method, reason: " + getPaymentMethodResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getPaymentMethodResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in adding payment method, reason: " + error.toString());
            }
        });
    }

    public void getPaymentMethods(final RequestListener requestListener)
    {
        String customerID = ContentManager.getInstance().getUser().getCustomerID();
        mApiService.getPaymentMethods(customerID, new Callback<GetPaymentMethodResponse>()
        {
            @Override
            public void success(GetPaymentMethodResponse getPaymentMethodResponse, Response response)
            {
                if (getPaymentMethodResponse.isSucceeded())
                {
                    Log.i(LOG_TAG, "Got customer payment methods");
                    ContentManager.getInstance().setPaymentMethodList(getPaymentMethodResponse.getPaymentMethodList());
                    requestListener.onSuccess();
                }
                else
                {
                    Log.e(LOG_TAG, "Internal server error in getting customer payment methods, reason: " + getPaymentMethodResponse.getErrorResponse().getMessage());
                    requestListener.onInternalServerFailure(getPaymentMethodResponse.getErrorResponse());
                }
            }

            @Override
            public void failure(RetrofitError error)
            {
                requestListener.onNetworkFailure(error);
                Log.e(LOG_TAG, "Retrofit-Error in getting customer payment methods, reason: " + error.toString());
            }
        });
    }
}
