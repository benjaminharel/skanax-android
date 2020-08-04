package co.thenets.brisk.managers;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Locale;
import java.util.UUID;

import co.thenets.brisk.general.Params;
import co.thenets.brisk.models.DeviceData;

/**
 * Created by DAVID BELOOSESKY on 09/11/2015
 */
public class DeviceManager
{
    private final Context mContext;
    private static DeviceManager msInstance;
    public static final String LOG_TAG = DeviceManager.class.getSimpleName();

    private DeviceManager(Context context)
    {
        mContext = context;
    }

    public static DeviceManager init(Context context)
    {
        if (msInstance == null)
        {
            msInstance = new DeviceManager(context);
        }

        return msInstance;
    }

    public static DeviceManager getInstance()
    {
        return msInstance;
    }

    public String getDeviceManufacturer()
    {
        return android.os.Build.MANUFACTURER.toLowerCase(Locale.ENGLISH);
    }

    public String getDeviceModel()
    {
        return android.os.Build.MODEL.toLowerCase(Locale.ENGLISH);
    }

    public String getDeviceOsVersion()
    {
        return Build.VERSION.RELEASE;
    }


    /**
     * Get unique id for this device
     *
     * @param context
     * @return unique id
     */
    public static String getDeviceUniqueID(Context context)
    {
        UUID mDeviceUuid;
        String androidId;

        androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        mDeviceUuid = new UUID(androidId.hashCode(), 0);
        return mDeviceUuid.toString().toUpperCase();
    }

    /**
     * Get unique id for this device
     *
     * @param context
     * @return unique id
     */
    public String getDeviceUniqueIDUsingReadStatePermission(Context context)
    {
        UUID mDeviceUuid;
        String tmDevice = "";
        String tmSerial = "";
        String androidId;

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE)
        {
            // Mobile or Tablet with Telephony
            tmDevice = telephonyManager.getDeviceId();
            // Check for SimSerialNumber if exists
            tmSerial = TextUtils.isEmpty(telephonyManager.getSimSerialNumber()) ? "" : telephonyManager.getSimSerialNumber();
        }

        androidId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(tmDevice) || TextUtils.isEmpty(tmSerial))
        {
            // Generate device uuid for special cases (like emulator)
            mDeviceUuid = new UUID(androidId.hashCode(), 0);
        }
        else
        {
            mDeviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        }

        return mDeviceUuid.toString().toUpperCase();
    }

    public DeviceData getDeviceData()
    {
        DeviceData deviceData = new DeviceData();
        deviceData.setOS(Params.ANDROID_OS);
        deviceData.setOsVersion(getDeviceOsVersion());
        deviceData.setDeviceManufacturer(getDeviceManufacturer());
        deviceData.setDeviceModel(getDeviceModel());
        deviceData.setPushNotificationToken(ContentManager.getInstance().getPushNotificationToken());
        return deviceData;
    }
}


