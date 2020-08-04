package co.thenets.brisk.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.net.SocketTimeoutException;

import co.thenets.brisk.R;
import co.thenets.brisk.enums.PushType;
import co.thenets.brisk.enums.RoleType;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.interfaces.GcmRegisterListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.DeviceManager;
import co.thenets.brisk.models.DeviceData;
import co.thenets.brisk.models.User;
import co.thenets.brisk.rest.AdvancedConfiguration;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateUserRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

public class MainActivity extends AppCompatActivity implements GcmRegisterListener
{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private Intent mIntent;
    private Context mContext;

    // Google cloud messaging fields
    private GoogleCloudMessaging mGcm;
    private String mGcmRegID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        setGoogleCloudMessaging(this);
        AppsFlyerLib.getInstance().startTracking(getApplication(),Params.APPS_FLYER_DEV_KEY);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // FB: Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        // FB: support deep link
        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler()
                {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData)
                    {
                        // Process app link data
                    }
                }
        );

        AdWordsConversionReporter.registerReferrer(this.getApplicationContext(),this.getIntent().getData());
        /** AdWords: Your code that parses deep links and routes users to the right place. **/

        // AdWord: report event
//        AdWordsConversionReporter.reportWithConversionId(this.getApplicationContext(), "1038185027", "aqUCHIerhAgQw-SF7wM", "0", true);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // FB: Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    private void createUserIfNeeded()
    {
        if (!ContentManager.getInstance().isUserCreated())
        {
            createUser();
        }
        else
        {
            // User already created
            try
            {
                Log.i(LOG_TAG, "User already exists, userID: " + ContentManager.getInstance().getUser().getID());
                handleNavigation();
            }
            catch (Exception e)
            {
                createUser();
            }
        }
    }

    private void createUser()
    {
        DeviceData deviceData = DeviceManager.getInstance().getDeviceData();
        User user = new User();
        user.setDeviceData(deviceData);
        RestClientManager.getInstance().createUser(new CreateUserRequest(user), new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                handleNavigation();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                // TODO: display error message
                Log.e(LOG_TAG, error.toString());
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                if (error.isNetworkError())
                {
                    if (error.getCause() instanceof SocketTimeoutException)
                    {
                        Log.e(LOG_TAG, "TimeoutException");
                    }
                    else
                    {
                        Intent intent = new Intent(mContext, NoInternetActivity.class);
                        startActivity(intent);
                    }
                }
                Log.e(LOG_TAG, error.toString());
            }
        });
    }

    private void handleNavigation()
    {
        setAppStateAccordingToPushIfNeeded();
        if (ContentManager.getInstance().isAppInSellerMode())
        {
            mIntent = new Intent(this, StoreMainActivity.class);
        }
        else
        {
            mIntent = new Intent(this, SplashActivity.class);
        }

        if (getIntent().getExtras() != null)
        {
            mIntent.putExtras(getIntent().getExtras());
        }
        startActivity(mIntent);
        finish();
    }

    private void setAppStateAccordingToPushIfNeeded()
    {
        PushType pushType = (PushType) getIntent().getSerializableExtra(Params.GCM_TYPE);
        if(pushType != null)
        {
            switch (pushType)
            {
                case ORDER:
                    RoleType roleType = (RoleType) getIntent().getSerializableExtra(Params.ORDER_ROLE);
                    if (roleType != null)
                    {
                        switch (roleType)
                        {
                            case CUSTOMER:
                                ContentManager.getInstance().setIsAppInSellerMode(false);
                                break;
                            case STORE:
                                ContentManager.getInstance().setIsAppInSellerMode(true);
                                break;
                        }
                    }
                    break;
                case STORE_STATE:
                    ContentManager.getInstance().setIsAppInSellerMode(true);
                    break;
            }
        }
    }

    /**
     * Should be called also in onResume!!
     */
    private void setGoogleCloudMessaging(GcmRegisterListener gcmRegisterListener)
    {
        // Check device for Play Services APK.
        if (checkForGooglePlayServices())
        {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            mGcm = GoogleCloudMessaging.getInstance(this);
            mGcmRegID = getRegistrationId(this);

            if (mGcmRegID.isEmpty())
            {
                registerInBackground(gcmRegisterListener);
            }
            else
            {
                gcmRegisterListener.onGcmRegistrationCompleted(mGcmRegID);
            }
        }
        else
        {
            Log.e(LOG_TAG, getString(R.string.google_play_services_error));
            Toast.makeText(this, getString(R.string.google_play_services_error), Toast.LENGTH_SHORT).show();
            gcmRegisterListener.onGcmRegistrationFailed();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkForGooglePlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, Params.GOOGLE_PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(LOG_TAG, "This device is not supported.");
                // The original google sample code is to finish the app in this step,
                // But in our app, we still want to continue.
                // finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(Params.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty())
        {
            Log.i(LOG_TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Params.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion)
        {
            Log.i(LOG_TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context)
    {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     *
     * @param gcmRegisterListener
     */
    private void registerInBackground(final GcmRegisterListener gcmRegisterListener)
    {
        new AsyncTask()
        {
            @Override
            protected String doInBackground(Object[] params)
            {
                String msg = "";
                try
                {
                    if (mGcm == null)
                    {
                        mGcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    mGcmRegID = mGcm.register(AdvancedConfiguration.GCM_PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + mGcmRegID;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
//                    sendRegistrationIdToBackend();
                    Log.d(LOG_TAG, "Registered mGcmRegID " + mGcmRegID);


                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(mContext, mGcmRegID);

                    // Notify listeners for GCM registration
                    gcmRegisterListener.onGcmRegistrationCompleted(mGcmRegID);
                }
                catch (IOException ex)
                {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.

                    gcmRegisterListener.onGcmRegistrationFailed();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(Object o)
            {
                super.onPostExecute(o);
                Log.d(LOG_TAG, "Registered to GCM");
            }

        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Params.PROPERTY_REG_ID, regId);
        editor.putInt(Params.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    @Override
    public void onGcmRegistrationCompleted(String regID)
    {
        ContentManager.getInstance().setPushNotificationToken(regID);
        createUserIfNeeded();
    }

    @Override
    public void onGcmRegistrationFailed()
    {
        ContentManager.getInstance().setPushNotificationToken("");
        Log.e(LOG_TAG, "No GCM register ID");
        createUserIfNeeded();
    }
}
