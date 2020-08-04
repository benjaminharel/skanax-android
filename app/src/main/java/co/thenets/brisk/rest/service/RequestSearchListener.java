package co.thenets.brisk.rest.service;

import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.responses.GetStoresResponse;
import retrofit.RetrofitError;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public interface RequestSearchListener
{
    void onSuccess(GetStoresResponse getStoresResponse);
    void onInternalServerFailure(ErrorResponse error);
    void onNetworkFailure(RetrofitError error);
}
