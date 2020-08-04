package co.thenets.brisk.rest.service;

import co.thenets.brisk.models.Order;
import co.thenets.brisk.rest.responses.ErrorResponse;
import retrofit.RetrofitError;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
public interface RequestCreateOrderListener
{
    void onSuccess(Order order);
    void onInternalServerFailure(ErrorResponse error);
    void onNetworkFailure(RetrofitError error);
}
