package co.thenets.brisk.custom;

import android.annotation.SuppressLint;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import co.thenets.brisk.general.Params;

/**
 * Created by DAVID-WORK on 07/03/2016.
 */
@SuppressLint("ParcelCreator")
public class AddressResultReceiver extends ResultReceiver
{
    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public AddressResultReceiver(Handler handler)
    {
        super(handler);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string
        // or an error message sent from the intent service.
        if(resultCode == Params.SUCCESS_RESULT)
        {
            Address address = resultData.getParcelable(Params.ADDRESS_DATA);
            Log.e("DAVID", address.toString());
        }
    }
}
