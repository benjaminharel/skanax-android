package co.thenets.brisk.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import co.thenets.brisk.general.Params;

/**
 * Created by DAVID-WORK on 07/03/2016.
 */
public class FetchAddressIntentService extends IntentService
{
    private ResultReceiver mReceiver;
    private Location mLocation;

    public static final String LOG_TAG = FetchAddressIntentService.class.getSimpleName();

    public FetchAddressIntentService()
    {
        super(FetchAddressIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        // Get data from intent
        mReceiver = intent.getParcelableExtra(Params.INTENT_RECEIVER);
        mLocation = intent.getParcelableExtra(Params.LOCATION_DATA);

        if (mReceiver != null && mLocation != null)
        {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;

            try
            {
                // In this sample, get just a single address.
                addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
            }
            catch (IOException ioException)
            {
                // Catch network or other I/O problems.
                Log.e(LOG_TAG, "Service not available", ioException);
            }
            catch (IllegalArgumentException illegalArgumentException)
            {
                // Catch invalid latitude or longitude values.
                Log.e(LOG_TAG, "invalid lat long used " + "Lat = " + mLocation.getLatitude() + ", Long = " + mLocation.getLongitude(), illegalArgumentException);
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0)
            {
                Log.e(LOG_TAG, "No address found");
                deliverResultToReceiver(Params.FAILURE_RESULT, null);
            }
            else
            {
                deliverResultToReceiver(Params.SUCCESS_RESULT, addresses.get(0));
            }
        }
    }

    private void deliverResultToReceiver(int resultCode, Address address)
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Params.ADDRESS_DATA, address);
        mReceiver.send(resultCode, bundle);
    }
}
