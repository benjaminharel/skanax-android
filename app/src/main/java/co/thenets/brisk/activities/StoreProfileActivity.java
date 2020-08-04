
package co.thenets.brisk.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.dialogs.PickImageDialog;
import co.thenets.brisk.enums.AcceptedPaymentType;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.SourceImageType;
import co.thenets.brisk.events.CustomerProfileDetailEvent;
import co.thenets.brisk.events.ImageCroppedEvent;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Store;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateOrUpdateStoreRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;


public final class StoreProfileActivity extends BaseActivity implements View.OnClickListener
{
    private static final String LOG_TAG = StoreProfileActivity.class.getSimpleName();
    private EditText mStoreNameEditText;
    private CheckBox mCashCheckBox;
    private CheckBox mBriskCheckBox;
    private EditText mDeliveryPriceEditText;
    private ImageView mStoreImageView;
    private LinearLayout mProgressBarLayout;
    private Toolbar mToolbar;
    private Store mStore;
    private Activity mActivityContext;
    ArrayList<AcceptedPaymentType> mAcceptedPaymentTypeList = new ArrayList<>();

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.activity_store_profile);

        mActivityContext = this;
        mStore = ContentManager.getInstance().getStore();
        setViews();
        setData();

        EventsManager.getInstance().register(this);
    }

    private void setToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.store_details);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onBackPressed()
    {
        EventsManager.getInstance().post(new CustomerProfileDetailEvent());
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    private void setViews()
    {
        setToolbar();
        setFab();
        setOtherViews();
    }

    private void setFab()
    {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (allFieldsOk())
                {
                    updateStoreDetails();
                }
                else
                {
                    UIManager.getInstance().hideKeyboard(fab, mActivityContext);
                    UIManager.getInstance().displaySnackBarError(fab, getString(R.string.missing_fields_error), Snackbar.LENGTH_SHORT);
                }
            }
        });
    }

    private boolean allFieldsOk()
    {
        if ((!TextUtils.isEmpty(mStoreNameEditText.getText()))
                && (((mCashCheckBox.isChecked() && !mBriskCheckBox.isChecked()) || (!mCashCheckBox.isChecked() && mBriskCheckBox.isChecked())) || (mCashCheckBox.isChecked() && mBriskCheckBox.isChecked()))
                && (!TextUtils.isEmpty(mDeliveryPriceEditText.getText())))
        {
            return true;
        }

        return false;
    }

    private void setOtherViews()
    {
        mStoreNameEditText = (EditText) findViewById(R.id.storeNameEditText);
        mCashCheckBox = (CheckBox) findViewById(R.id.cashCheckBox);
        mBriskCheckBox = (CheckBox) findViewById(R.id.briskCheckBox);
        mDeliveryPriceEditText = (EditText) findViewById(R.id.deliveryPriceEditText);
        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
        mStoreImageView = (ImageView) findViewById(R.id.storeImage);
        mStoreImageView.setOnClickListener(this);
    }

    private void setData()
    {
        mStoreNameEditText.setText(mStore.getName());
        mCashCheckBox.setChecked(mStore.isCashPaymentAccepted());
        mBriskCheckBox.setChecked(mStore.isBriskPaymentAccepted());
        mDeliveryPriceEditText.setText(String.valueOf((int) mStore.getDeliveryPrice()));
        UIManager.getInstance().loadImage(this, mStore.getPhotoGallery().getOriginal(), mStoreImageView, ImageType.STORE);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.storeImage:
                PickImageDialog pickImageDialog = PickImageDialog.newInstance(SourceImageType.FROM_EDIT_STORE);
                pickImageDialog.show(getSupportFragmentManager(), PickImageDialog.class.getSimpleName());
                break;
        }
    }

    private void updateStoreDetails()
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        Store store = ContentManager.getInstance().getStore();
        Store storeForUpload = store;
        storeForUpload.setName(mStoreNameEditText.getText().toString());
        if(mCashCheckBox.isChecked())
        {
            mAcceptedPaymentTypeList.add(AcceptedPaymentType.CASH);
        }
        if(mBriskCheckBox.isChecked())
        {
            mAcceptedPaymentTypeList.add(AcceptedPaymentType.BRISK_PAYMENT);
        }

        storeForUpload.setAcceptedPaymentList(mAcceptedPaymentTypeList);
        storeForUpload.setDeliveryPrice(Float.valueOf(mDeliveryPriceEditText.getText().toString()));

        byte[] byteArrayFromImageView = Utils.getByteArrayFromImageView(mStoreImageView);
        String encodedImage = Base64.encodeToString(byteArrayFromImageView, Base64.DEFAULT);
        storeForUpload.setImageLink(encodedImage);

        RestClientManager.getInstance().updateStore(new CreateOrUpdateStoreRequest(storeForUpload), new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                mProgressBarLayout.setVisibility(View.GONE);
                finish();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mProgressBarLayout, getString(R.string.error_in_update_store_details), Snackbar.LENGTH_LONG);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                UIManager.getInstance().displaySnackBarError(mProgressBarLayout, getString(R.string.error_in_update_store_details), Snackbar.LENGTH_LONG);
            }
        });
    }

    @Subscribe
    public void refreshCroppedImage(ImageCroppedEvent imageCroppedEvent)
    {
        byte[] byteArray = ContentManager.getInstance().getStoreCroppedImageByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        mStoreImageView.setImageBitmap(bitmap);
    }
}

