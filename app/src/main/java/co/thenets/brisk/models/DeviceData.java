package co.thenets.brisk.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by DAVID-WORK on 08/11/2015.
 */
@Parcel
public class DeviceData implements Serializable
{
    @SerializedName("os")
    private String mOS = "Android";

    @SerializedName("os_version")
    private String mOsVersion;

    @SerializedName("device")
    private String mDeviceManufacturer;

    @SerializedName("model")
    private String mDeviceModel;

    @SerializedName("push_notification_token")
    private String mPushNotificationToken;

    public String getOS()
    {
        return mOS;
    }

    public void setOS(String OS)
    {
        mOS = OS;
    }

    public String getOsVersion()
    {
        return mOsVersion;
    }

    public void setOsVersion(String osVersion)
    {
        mOsVersion = osVersion;
    }

    public String getDeviceManufacturer()
    {
        return mDeviceManufacturer;
    }

    public void setDeviceManufacturer(String deviceManufacturer)
    {
        mDeviceManufacturer = deviceManufacturer;
    }

    public String getDeviceModel()
    {
        return mDeviceModel;
    }

    public void setDeviceModel(String deviceModel)
    {
        mDeviceModel = deviceModel;
    }

    public String getPushNotificationToken()
    {
        return mPushNotificationToken;
    }

    public void setPushNotificationToken(String pushNotificationToken)
    {
        mPushNotificationToken = pushNotificationToken;
    }

}
