package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

import co.thenets.brisk.enums.PaymentMethodType;

/**
 * Created by DAVID-WORK on 22/03/2016.
 */

@Parcel
public class PaymentMethod implements Serializable
{
    @SerializedName("id")
    protected String mID;

    @SerializedName("name")
    protected String mName;

    @SerializedName("method")
    protected PaymentMethodType mMethodType;

    // AuthorizationCode if it's a PayPal or AuthorizationToken if it's a CC via Zooz Payments
    @SerializedName("auth_token")
    protected String mAuthorizationCodeOrToken;

    public PaymentMethod()
    {
    }

    public PaymentMethod(PaymentMethodType methodType, String authorizationCode)
    {
        mMethodType = methodType;
        mAuthorizationCodeOrToken = authorizationCode;
    }

    public PaymentMethodType getMethodType()
    {
        return mMethodType;
    }

    public String getID()
    {
        return mID;
    }

    public String getName()
    {
        return mName;
    }

    @Override
    public String toString()
    {
        return "PaymentMethod{" +
                "mMethodType=" + mMethodType +
                ", mAuthorizationCodeOrToken='" + mAuthorizationCodeOrToken + '\'' +
                '}';
    }
}
