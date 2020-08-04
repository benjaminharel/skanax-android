package co.thenets.brisk.rest.service;

import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.responses.GetStoreProductsResponse;
import retrofit.RetrofitError;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public interface RequestGetStoreProductsListener
{
    void onSuccess(GetStoreProductsResponse getStoreProductsResponse);
    void onInternalServerFailure(ErrorResponse error);
    void onNetworkFailure(RetrofitError error);
}
