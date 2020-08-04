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
public class CheckIfInTLVIntentService extends IntentService
{
    private ResultReceiver mReceiver;
    private Location mLocation;
    private Location mTLVLocation = new Location("TLVSampleProvider");


    public static final String LOG_TAG = CheckIfInTLVIntentService.class.getSimpleName();

    public CheckIfInTLVIntentService()
    {
        super(CheckIfInTLVIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        // Get data from intent
        mReceiver = intent.getParcelableExtra(Params.INTENT_RECEIVER);
        mLocation = intent.getParcelableExtra(Params.LOCATION_DATA);
        mTLVLocation.setLatitude(Params.TLV_SAMPLE_LATITUDE);
        mTLVLocation.setLongitude(Params.TLV_SAMPLE_LONGITUDE);

        if (mReceiver != null && mLocation != null)
        {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> currentLocationAddresses = null;
            List<Address> TLVLocationAddresses = null;

            try
            {
                // In this sample, get just a single address.
                currentLocationAddresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                TLVLocationAddresses = geocoder.getFromLocation(mTLVLocation.getLatitude(), mTLVLocation.getLongitude(), 1);
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
            if (currentLocationAddresses == null || currentLocationAddresses.size() == 0
                    || TLVLocationAddresses == null || TLVLocationAddresses.size() == 0)
            {
                Log.e(LOG_TAG, "No address found");
                deliverResultToReceiver(Params.FAILURE_RESULT, false);
            }
            else
            {
                String currentCity = currentLocationAddresses.get(0).getLocality();
                String tlvCity = TLVLocationAddresses.get(0).getLocality();

                if(currentCity.equals(tlvCity))
                {
                    deliverResultToReceiver(Params.SUCCESS_RESULT, true);
                }
                else
                {
                    deliverResultToReceiver(Params.SUCCESS_RESULT, false);
                }
            }
        }
    }

    private void deliverResultToReceiver(int resultCode, boolean isWithinTLV)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Params.IS_WITHIN_TLV, isWithinTLV);
        mReceiver.send(resultCode, bundle);
    }
}
