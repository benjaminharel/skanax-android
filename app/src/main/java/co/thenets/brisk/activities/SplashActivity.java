package co.thenets.brisk.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.skyfishjy.library.RippleBackground;

import co.thenets.brisk.R;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RetrofitError;

public class SplashActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
{
    private static final String LOG_TAG = SplashActivity.class.getSimpleName();
    private static final int REQUEST_CHECK_LOCATION_SETTINGS = 900;
    private static final int PERMISSIONS_REQUEST_LOCATION = 901;
    private static final long LOADING_DELAY_TIME = 3000;
    private RippleBackground mSearchingRipple;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private Button mEnableLocationBtn;
    private TextView mTitleTextView;
    private CircleImageView mCircleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        mContext = this;
        setViews();
        runLogic();
    }

    private void setViews()
    {
        mCircleImageView = (CircleImageView) findViewById(R.id.imageView);
        if(ContentManager.getInstance().getUser() != null && ContentManager.getInstance().getUser().getPhotoGallery() != null)
        {
            UIManager.getInstance().loadImage(mContext, ContentManager.getInstance().getUser().getPhotoGallery().getOriginal(), mCircleImageView, ImageType.SPLASH_IMAGE);
        }
        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mEnableLocationBtn = (Button) findViewById(R.id.enableLocationBtn);
        assert mEnableLocationBtn != null;
        mEnableLocationBtn.setOnClickListener(this);
        mSearchingRipple = (RippleBackground) findViewById(R.id.searchingRipple);
    }

    private void runLogic()
    {
        if (Utils.isLocationPermissionGiven(this))
        {
            // Set views visibility
            if (!TextUtils.isEmpty(getIntent().getStringExtra(Params.ORDER_ID)))
            {
                UIManager.getInstance().displaySnackBarWithPrimary(mEnableLocationBtn, getString(R.string.loading_data), Snackbar.LENGTH_INDEFINITE);
            }
            else
            {
                UIManager.getInstance().displaySnackBarWithPrimary(mEnableLocationBtn, getString(R.string.searching_for_stores_around_you), Snackbar.LENGTH_INDEFINITE);
            }

            mTitleTextView.setVisibility(View.INVISIBLE);
            mEnableLocationBtn.setVisibility(View.INVISIBLE);
            mSearchingRipple.startRippleAnimation();

            // Get the device location
            getDeviceLocation();
        }
        else
        {
            // Set views visibility
            mTitleTextView.setText(getString(R.string.we_are_looking_for_available_stores_next_to_you_please_enable_location_services));
            mEnableLocationBtn.setVisibility(View.VISIBLE);
            mSearchingRipple.stopRippleAnimation();
        }
    }


    private void askForLocationPermissionsAndSetContent()
    {
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED && hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        // permission was granted!
        getDeviceLocation();
    }


    private void getDeviceLocation()
    {
        setGoogleApiClient();
    }

    protected synchronized void setGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null)
        {
            ContentManager.getInstance().setCurrentLocation(lastLocation);
            Log.d(LOG_TAG, "Lat: " + String.valueOf(lastLocation.getLatitude()));
            Log.d(LOG_TAG, "Lon: " + String.valueOf(lastLocation.getLongitude()));
            getLocalStores();
        }
        else
        {
            createLocationRequest();
        }
    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }


    private void createLocationRequest()
    {
        // Create the location request
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        // Get the current location settings of a user's device
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> locationSettings = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        // Check whether the current location settings are satisfied
        locationSettings.setResultCallback(new ResultCallback<LocationSettingsResult>()
        {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult)
            {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here.
                        askForLocationPermissionsAndSetContent();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                        try
                        {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the locationSettings in onActivityResult().
                            status.startResolutionForResult((Activity) mContext, REQUEST_CHECK_LOCATION_SETTINGS);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        // For example the user select to never ask him again to enable location services
                        UIManager.getInstance().displayNoLocationDialog();
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted!
                    runLogic();
                }
                else
                {
                    // permission denied! Disable the functionality that depends on this permission.
                    UIManager.getInstance().displayNoLocationDialog();
                }
                return;
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.enableLocationBtn:
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                break;
        }
    }


    private void getLocalStores()
    {
        RestClientManager.getInstance().getStores(new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                if (!TextUtils.isEmpty(getIntent().getStringExtra(Params.ORDER_ID)))
                {
                    Intent intent = new Intent(mContext, CustomerMainActivity.class);
                    intent.putExtras(getIntent().getExtras());
                    mContext.startActivity(intent);
                    finish();
                }
                else
                {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Intent intent = new Intent(mContext, CustomerMainActivity.class);
                            mContext.startActivity(intent);
                            finish();
                        }
                    }, LOADING_DELAY_TIME);
                }
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
            }
        });
    }
}
