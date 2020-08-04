package co.thenets.brisk.gcm;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import co.thenets.brisk.enums.PushType;
import co.thenets.brisk.enums.RoleType;

/**
 * Created by DAVID BELOOSESKY on 03/12/2014 - 17:15.
 */
public abstract class BasePushPayload implements Serializable
{
    @SerializedName("role")
    protected String mRole;

    @SerializedName("type")
    protected PushType mType;

    public PushType getType()
    {
        return mType;
    }

    public RoleType getRole()
    {
        if(mRole.equals("customer"))
        {
            return RoleType.CUSTOMER;
        }
        else
        {
            return RoleType.STORE;
        }
    }

    @Override
    public String toString()
    {
        return "BasePushPayload{" +
                "mRole='" + mRole + '\'' +
                ", mType=" + mType +
                '}';
    }
}
