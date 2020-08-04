
package co.thenets.brisk.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.dialogs.ConfirmationDialog;
import co.thenets.brisk.dialogs.PickImageDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.SourceImageType;
import co.thenets.brisk.events.CustomerProfileDetailEvent;
import co.thenets.brisk.events.ImageCroppedEvent;
import co.thenets.brisk.events.SignOutProfileDetailEvent;
import co.thenets.brisk.interfaces.ConfirmationListener;
import co.thenets.brisk.interfaces.SignOutListener;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;


/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class UserProfileActivity extends BaseActivity implements View.OnClickListener
{
    private static final String LOG_TAG = UserProfileActivity.class.getSimpleName();
    private TextView mEmailTextView;
    private ImageView mProfileImageView;
    private TextView mPhoneNumberTextView;
    private Toolbar mToolbar;
    private TextView mFirstAndLastNamesTextView;
    private TextView mSignoutTextView;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.activity_user_profile);

        setViews();
        setData();

        EventsManager.getInstance().register(this);
    }

    private void setToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.your_profile);
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
        setOtherViews();
    }

    private void setOtherViews()
    {
        mEmailTextView = (TextView) findViewById(R.id.emailTextView);
        mPhoneNumberTextView = (TextView) findViewById(R.id.phoneTextView);
        mProfileImageView = (ImageView) findViewById(R.id.profileImage);
        mFirstAndLastNamesTextView = (TextView) findViewById(R.id.headerName);
        mSignoutTextView = (TextView) findViewById(R.id.signOutTextView);
        mSignoutTextView.setOnClickListener(this);
    }

    private void setData()
    {
        if(ContentManager.getInstance().isUserCompletelyRegistered())
        {
            mFirstAndLastNamesTextView.setText(ContentManager.getInstance().getUser().getFirstName() + " " + ContentManager.getInstance().getUser().getLastName());
        }
        else
        {
            mFirstAndLastNamesTextView.setText("");
        }

        mEmailTextView.setText(ContentManager.getInstance().getUser().getEmail());
        mPhoneNumberTextView.setText(ContentManager.getInstance().getUser().getPhone());

        String imageLink = ContentManager.getInstance().getUser().getPhotoGallery().getOriginal();

        if (imageLink != null)
        {
            UIManager.getInstance().loadImage(this, imageLink, mProfileImageView, ImageType.PROFILE);
        }
    }

    public void fabProfileClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fab:
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void updateUi()
    {
        mFirstAndLastNamesTextView.setText(ContentManager.getInstance().getUser().getFirstName() + " " + ContentManager.getInstance().getUser().getLastName());
        mEmailTextView.setText(ContentManager.getInstance().getUser().getEmail());
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.profileImage:
                PickImageDialog pickImageDialog = PickImageDialog.newInstance(SourceImageType.FROM_REGISTRATION);
                pickImageDialog.show(getSupportFragmentManager(), PickImageDialog.class.getSimpleName());
                break;
            case R.id.signOutTextView:
                ConfirmationDialog confirmationDialog = ConfirmationDialog.newInstance("",getString(R.string.are_you_sure_you_want_to_sign_out), new ConfirmationListener()
                {
                    @Override
                    public void onApprove()
                    {
                        signOut();
                    }

                    @Override
                    public void onCancel()
                    {
                        // Do nothing
                    }
                });
                confirmationDialog.show(getFragmentManager(), ConfirmationDialog.class.getSimpleName());
                break;
        }
    }

    private void signOut()
    {
        ContentManager.getInstance().signOut(new SignOutListener()
        {
            @Override
            public void onSignOutSucceed()
            {
                EventsManager.getInstance().post(new SignOutProfileDetailEvent());
                finish();
            }

            @Override
            public void onSignOutFailed()
            {
                // Can't create new user, so exit the app
                Toast.makeText(getApplicationContext(), getString(R.string.cant_create_new_user_error), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Subscribe
    public void refreshCroppedImage(ImageCroppedEvent imageCroppedEvent)
    {
        byte[] byteArray = ContentManager.getInstance().getUserCroppedImageByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        ImageView croppedImageView = (ImageView) findViewById(R.id.profileImage);
        croppedImageView.setImageBitmap(bitmap);
    }


    @Subscribe
    public void handleCustomerProfileDetailEvent(CustomerProfileDetailEvent customerProfileDetailEvent)
    {
        updateUi();
    }
}

