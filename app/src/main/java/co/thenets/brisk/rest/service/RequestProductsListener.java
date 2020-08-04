package co.thenets.brisk.rest.service;

import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.responses.GetProductsResponse;
import retrofit.RetrofitError;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public interface RequestProductsListener
{
    void onSuccess(GetProductsResponse getProductsResponse);
    void onSearchWithoutValue();
    void onInternalServerFailure(ErrorResponse error);
    void onNetworkFailure(RetrofitError error);
}
