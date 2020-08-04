/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.thenets.brisk.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.BarcodeGraphic;
import co.thenets.brisk.custom.BarcodeTrackerFactory;
import co.thenets.brisk.custom.CameraSource;
import co.thenets.brisk.custom.CameraSourcePreview;
import co.thenets.brisk.custom.GraphicOverlay;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.events.BarcodeDetectedEvent;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Product;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.AdvancedConfiguration;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;


/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class BarcodeActivity extends BaseActivity
{
    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ImageView mBarcodeImageView;
    private TextView mTitleTextView;
    private LinearLayout mProductFoundFrameLayout;
    private FrameLayout mScanProductFrameLayout;
    private ImageView mItemProductImageView;
    private TextView itemNameText;
    private TextView itemMarketPriceText;
    private static final String LOG_TAG = BarcodeActivity.class.getSimpleName();
    private Context mContext;
    private Barcode mBarcode = null;
    private long mDelayTime = TimeUnit.MILLISECONDS.toMillis(3000);

    private Handler mHandler = new Handler();

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.brisker_barcode_capture);
        mContext = this;

        EventsManager.getInstance().register(this);
        setViews();

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphicOverlay);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED)
        {
            createCameraSource(true, false);
        }
        else
        {
            requestCameraPermission();
        }
    }

    private void setViews()
    {
        mBarcodeImageView = (ImageView) findViewById(R.id.barcodeImage);
        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mProductFoundFrameLayout = (LinearLayout) findViewById(R.id.productFoundFrameLayout);
        mScanProductFrameLayout = (FrameLayout) findViewById(R.id.scanProductFrameLayout);

        mItemProductImageView = (ImageView) findViewById(R.id.itemProductImage);
        itemNameText = (TextView) findViewById(R.id.itemNameText);
        itemMarketPriceText = (TextView) findViewById(R.id.itemMarketPriceTextView);
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission()
    {
        Log.w(LOG_TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA))
        {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }
    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     * <p/>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash)
    {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());


        if (!barcodeDetector.isOperational())
        {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage)
            {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
            }
        }
        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            if (Build.MANUFACTURER.equals("samsung") && Build.MODEL.equals("GT-I9100"))
            {
                builder = builder.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO); //not working for LG2 but work for Samsung S2
            }
            else
            {
                builder = builder.setFocusMode(
                        autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
            }
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        if (mPreview != null)
        {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
        if (mPreview != null)
        {
            mPreview.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode != RC_HANDLE_CAMERA_PERM)
        {
            Log.d(LOG_TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Log.d(LOG_TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource(true, false);
            return;
        }

        Log.e(LOG_TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Brisker")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException
    {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS)
        {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null)
        {
            try
            {
                mPreview.start(mCameraSource, mGraphicOverlay);
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void barcodeFound()
    {
        BarcodeGraphic graphic = mGraphicOverlay.getFirstGraphic();
        mBarcode = null;
        if (graphic != null)
        {
            mBarcode = graphic.getBarcode();
            if (mBarcode != null)
            {
                checkIfTheProductAvailableOnOurServer(mBarcode);
                beginFirstUi();
            }
            else
            {
                Log.d(LOG_TAG, "barcode data is null");
            }
        }
        else
        {
            Log.d(LOG_TAG, "no barcode detected");
        }
    }

    private void beginFirstUi()
    {
        mProductFoundFrameLayout.setVisibility(View.INVISIBLE);
        mScanProductFrameLayout.setVisibility(View.VISIBLE);
        mTitleTextView.setText(getString(R.string.scan_to_add_product));
        mTitleTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_primary));
        mBarcodeImageView.setImageResource(R.drawable.ic_scan_product);
    }

    private void checkIfTheProductAvailableOnOurServer(Barcode barcode)
    {
        final HashMap<String, String> params = new HashMap<>();
        params.put(AdvancedConfiguration.BARCODE_KEY, barcode.displayValue);
        RestClientManager.getInstance().addProductsByBarcode(params, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                ArrayList<Product> products = ContentManager.getInstance().getProductList();
                if (!products.isEmpty())
                {
                    products.get(0);
                    Product product = products.get(0);
                    String imageLink = product.getPhotoGallery().getOriginal();
                    double marketPrice = product.getMarketPrice();
                    String name = product.getName();
                    itemNameText.setText(name);
                    UIManager.getInstance().loadImage(mContext, imageLink, mItemProductImageView, ImageType.PRODUCT);
                    itemMarketPriceText.setText("\u20AA" + String.valueOf(marketPrice));

                    mScanProductFrameLayout.setVisibility(View.INVISIBLE);
                    mProductFoundFrameLayout.setVisibility(View.VISIBLE);

                }
                else
                {
                    mTitleTextView.setText(R.string.product_not_found);
                    mTitleTextView.setTextSize(24);
                    mTitleTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    mBarcodeImageView.setImageResource(R.drawable.ic_product_not_found);
                    mBarcodeImageView.setPadding(0, 0, 0, 48);

                }
                //update the UI

                mHandler.postDelayed(new Runnable() {
                    public void run() {
                       beginFirstUi();
                    }
                }, mDelayTime);
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                Log.e(LOG_TAG, error + " Add Products By Barcode failed");
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                if(error.getResponse().getStatus() == AdvancedConfiguration.CONFLICT_CODE)
                {
                    Toast.makeText(mContext, getString(R.string.product_already_exists_in_your_store), Toast.LENGTH_SHORT).show();
                    Log.e(LOG_TAG, "Add Products By Barcode failed: " + error.toString());
                }
                else
                {
                    Log.e(LOG_TAG, "Add Products By Barcode failed: " + error.toString());
                }
            }
        });
    }

    @Subscribe
    public void handleDetectedBarcode(BarcodeDetectedEvent barcodeDetectedEvent)
    {
        barcodeFound();
    }
}

