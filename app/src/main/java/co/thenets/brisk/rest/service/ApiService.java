package co.thenets.brisk.rest.service;

import java.util.Map;

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
import co.thenets.brisk.rest.responses.GetActiveCategoriesResponse;
import co.thenets.brisk.rest.responses.GetActiveHashtagsResponse;
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
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public interface ApiService
{
    // User related calls
    @GET("/users/{id}")
    void getUser(@Path("id") String userID, Callback<GetUserResponse> callback);

    @GET("/users/{id}")
    void getVerificationCode(@Path("id") String userID, @Query("verification_code") int verificationCode, Callback<GetUserResponse> callback);

    @POST("/users")
    void createUser(@Body CreateUserRequest createUserRequest, Callback<GetUserResponse> callback);

    @POST("/users/{id}/locations")
    void updateUserLocation(@Path("id") String userID, @Body UpdateUserLocationRequest updateUserLocationRequest, Callback<Response> callback);

    @PUT("/users/{id}")
    void updateUser(@Path("id") String userID, @Body UpdateUserRequest updateUserRequest, Callback<GetUserResponse> callback);

    @GET("/customers/{id}/customer_token")
    void getPaymentToken(@Path("id") String customerID, Callback<GetPaymentTokenResponse> callback);

    // Customer related calls
    @GET("/customers/{id}/local_stores")
    void getLocalStores(@Path("id") String customerID, @QueryMap Map<String, String> params, Callback<GetStoresResponse> callback);

    @GET("/customers/{id}/active_categories")
    void getActiveCategories(@Path("id") String customerID, @QueryMap Map<String, String> params, Callback<GetActiveCategoriesResponse> callback);

//    @GET("/customers/{id}/active_products")
//    void getAvailableProducts(@Path("id") String customerID, @QueryMap Map<String, String> params, Callback<GetAvailableProductsResponse> callback);

    @GET("/customers/{id}/active_hashtags")
    void getActiveHashtags(@Path("id") String customerID, @QueryMap Map<String, String> params, Callback<GetActiveHashtagsResponse> callback);

    @POST("/customers/{id}/cart_list")
    void createCartList(@Path("id") String customerID, @Body CreateCartListRequest createCartListRequest, Callback<GetCartListResponse> callback);

    @GET("/customers/{id}/payment_methods")
    void getPaymentMethods(@Path("id") String customerID, Callback<GetPaymentMethodResponse> callback);

    @POST("/customers/{id}/payment_methods")
    void addPaymentMethod(@Path("id") String customerID, @Body AddPaymentMethodRequest addPaymentMethodRequest, Callback<AddPaymentMethodResponse> callback);

    // Store related calls
    @POST("/stores")
    void createStore(@Body CreateOrUpdateStoreRequest createOrUpdateStoreRequest, Callback<GetStoreResponse> callback);

    @PUT("/stores/{id}")
    void updateStore(@Path("id") String storeID, @Body CreateOrUpdateStoreRequest createOrUpdateStoreRequest, Callback<GetStoreResponse> callback);

    @GET("/stores/{id}")
    void getStore(@Path("id") String storeID, Callback<GetStoreResponse> callback);

    // Categories related calls
    @GET("/categories")
    void getCategories(Callback<GetCategoriesResponse> callback);

    // Products related calls
    @GET("/products")
    void getProducts(Callback<GetProductsResponse> callback);

    @GET("/products")
    void getProducts(@QueryMap Map<String, String> params, Callback<GetProductsResponse> callback);

    // Store Products related calls
    @GET("/stores/{id}/dashboard")
    void getDashboard(@Path("id") String storeID, Callback<GetDashboardResponse> callback);

    @GET("/stores/{id}/store_products")
    void getStoreProducts(@Path("id") String storeID, @QueryMap Map<String, String> params, Callback<GetStoreProductsResponse> callback);

    @POST("/stores/{id}/store_products")
    void addProductToStore(@Path("id") String storeID, @Body AddProductToStoreRequest addProductToStoreRequest, Callback<AddProductToStoreResponse> callback);

    @POST("/stores/{id}/store_products")
    void addExistingProductToStore(@Path("id") String storeID, @Body AddExistingProductToStoreRequest addExistingProductToStoreRequest, Callback<AddProductToStoreResponse> callback);

    @PUT("/stores/{id}/store_products/{store_product_id}")
    void updateProductPrice(
            @Path("id") String storeID, @Path("store_product_id") String storeProductID,
            @Body UpdateProductPriceRequest updateProductPriceRequest, Callback<AddProductToStoreResponse> callback
    );

    @DELETE("/stores/{id}/store_products/{store_product_id}")
    void removeProductFromStore(@Path("id") String storeID, @Path("store_product_id") String storeProductID, Callback<Response> callback);

    // Order related calls
    @POST("/orders")
    void createOrder(@Body CreateOrderRequest createOrderRequest, Callback<CrateOrderResponse> callback);

    @GET("/orders")
    void getOrders(@QueryMap Map<String, String> params, Callback<GetOrdersResponse> callback);

    @GET("/orders/{id}")
    void getOrder(@Path("id") String orderID, Callback<GetOrderResponse> callback);

    @POST("/orders/{id}/events")
    void updateOrderState(@Path("id") String orderID, @Body UpdateOrderStateRequest updateOrderStateRequest, Callback<BaseResponse> callback);

    @DELETE("/orders/{id}/order_items/{order_item_id}")
    void removeItemFromOrder(@Path("id") String orderID, @Path("order_item_id") String orderItemID, Callback<Response> callback);

    @POST("/orders/{id}/rating")
    void rateOrder(@Path("id") String orderID, @Body SetOrderRatingRequest setOrderRatingRequest, Callback<GetOrderResponse> callback);

    @PUT("/orders/{id}")
    void tipOrder(@Path("id") String orderID, @Body UpdateOrderRequest updateOrderRequest, Callback<GetOrderResponse> callback);

    @PUT("/orders/{id}/transfer_delivery")
    void transferDelivery(@Path("id") String orderID, @Body TransferOrderRequest transferOrderRequest, Callback<BaseResponse> callback);
}
