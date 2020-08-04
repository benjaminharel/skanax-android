
package co.thenets.brisk.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.dialogs.PickImageDialog;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.SourceImageType;
import co.thenets.brisk.events.CustomerProfileDetailEvent;
import co.thenets.brisk.events.ImageCroppedEvent;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.User;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.UpdateUserRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

public final class EditProfileActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher
{
    private static final String LOG_TAG = EditProfileActivity.class.getSimpleName();
    private FloatingActionButton mFab;
    private TextInputLayout mFirstNameTextInput;
    private TextInputLayout mLastNameTextInput;
    private TextInputLayout mEmailTextInput;
    private ImageView mProfileImageView;
    private TextView mPhoneNumberTextInput;
    private Toolbar mToolbar;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private boolean mIsChangeOnProfile = false;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout mProgressBarLayout;

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.activity_edit_profile);

        EventsManager.getInstance().register(this);
        setViews();
        setToolbar();
    }

    private void setToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.edit_your_profile);
        setSupportActionBar(mToolbar);
        (this).setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onDestroy()
    {
        EventsManager.getInstance().unregister(this);
        super.onDestroy();
    }

    private void setViews()
    {
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFirstNameTextInput = (TextInputLayout) findViewById(R.id.firstNameTextInput);
        mLastNameTextInput = (TextInputLayout) findViewById(R.id.lastNameTextInput);
        mEmailTextInput = (TextInputLayout) findViewById(R.id.emailTextInput);
        mPhoneNumberTextInput = (TextView) findViewById(R.id.phoneTextInput);

        mProfileImageView = (ImageView) findViewById(R.id.profileImage);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mProgressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
        mProgressBarLayout.setOnClickListener(this);
        mProfileImageView.setOnClickListener(this);

        mFirstNameTextInput.getEditText().addTextChangedListener(this);
        mLastNameTextInput.getEditText().addTextChangedListener(this);
        mEmailTextInput.getEditText().addTextChangedListener(this);
        loadingData();
    }

    private void loadingData()
    {
        mFirstName = ContentManager.getInstance().getUser().getFirstName();

        mFirstNameTextInput.getEditText().setText(ContentManager.getInstance().getUser().getFirstName());
        mLastName = ContentManager.getInstance().getUser().getLastName();
        mLastNameTextInput.getEditText().setText(ContentManager.getInstance().getUser().getLastName());
        mEmail = ContentManager.getInstance().getUser().getEmail();
        mEmailTextInput.getEditText().setText(ContentManager.getInstance().getUser().getEmail());
        mPhoneNumberTextInput.setText(ContentManager.getInstance().getUser().getPhone());

        String imageLink = ContentManager.getInstance().getUser().getPhotoGallery().getMedium();
        UIManager.getInstance().loadImage(this, imageLink, mProfileImageView, ImageType.PROFILE);

    }

    public void fabEditProfileClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fab:
                checkLogic();
                break;
        }
    }

    private void checkLogic()
    {
        if (mIsChangeOnProfile)
        {
            if (isNameValid())
            {
                User user = ContentManager.getInstance().getUser();
                user.setFirstName(mFirstNameTextInput.getEditText().getText().toString());
                user.setLastName(mLastNameTextInput.getEditText().getText().toString());
                user.setEmail(mEmailTextInput.getEditText().getText().toString());

                if (ContentManager.getInstance().getUserCroppedImageByteArray() != null)
                {
                    String encodedImage = Base64.encodeToString(ContentManager.getInstance().getUserCroppedImageByteArray(), Base64.DEFAULT);
                    user.setImageLink(encodedImage);
                }
                sendProfileDataToServer(user);
            }
        }
        else
        {
            finish();
        }
    }

    public boolean isNameValid()
    {
        if (!isNameLongEnough(mFirstNameTextInput.getEditText().getText().toString()))
        {
            Utils.hideKeyboardWithContext(getApplicationContext(), mFirstNameTextInput);
            UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.not_a_valid_name), Snackbar.LENGTH_LONG);
            return false;
        }
        else if (!isNameLongEnough(mLastNameTextInput.getEditText().getText().toString()))
        {
            Utils.hideKeyboardWithContext(getApplicationContext(), mLastNameTextInput);
            UIManager.getInstance().displaySnackBarError(mCoordinatorLayout, getString(R.string.not_a_valid_last_name), Snackbar.LENGTH_LONG);
            return false;
        }
        else if (!Utils.isEmailValid(mEmailTextInput.getEditText().getText().toString()))
        {
            Utils.hideKeyboardWithContext(getApplicationContext(), mEmailTextInput);

            UIManager.getInstance().displaySnackBarError(mCoordinatorLayout, getString(R.string.invalid_email_error), Snackbar.LENGTH_LONG);
            return false;
        }
        return true;
    }

    public boolean isNameLongEnough(String name)
    {
        return name.length() > 1;
    }

    private void sendProfileDataToServer(User user)
    {
        mProgressBarLayout.setVisibility(View.VISIBLE);
        RestClientManager.getInstance().updateUser(new UpdateUserRequest(user), new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                mProgressBarLayout.setVisibility(View.GONE);
                Log.d(LOG_TAG, "Successes");
                EventsManager.getInstance().post(new CustomerProfileDetailEvent());
                finish();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                Log.d(LOG_TAG, "onInternalServerFailure");
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                mProgressBarLayout.setVisibility(View.GONE);
                Log.d(LOG_TAG, "onNetworkFailure Code");
                Utils.hideKeyboard(mCoordinatorLayout, getParent());
                UIManager.getInstance().displaySnackBarError(mCoordinatorLayout, getString(R.string.network_problem_please_try_again), Snackbar.LENGTH_LONG);
            }
        });

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.profileImage:
                PickImageDialog pickImageDialog = PickImageDialog.newInstance(SourceImageType.FROM_SIGN_UP);
                pickImageDialog.show(getSupportFragmentManager(), PickImageDialog.class.getSimpleName());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                checkLogic();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void refreshCroppedImage(ImageCroppedEvent imageCroppedEvent)
    {
        byte[] byteArray = ContentManager.getInstance().getUserCroppedImageByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        ImageView croppedImageView = (ImageView) findViewById(R.id.profileImage);
        croppedImageView.setImageBitmap(bitmap);

        mIsChangeOnProfile = true;
    }

    @Override
    public void onBackPressed()
    {
        checkLogic();
        super.onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        if (mFirstNameTextInput.getEditText().getText().hashCode() == s.hashCode())
        {
            if (!mFirstName.equals(s.toString()))
            {
                mIsChangeOnProfile = true;
                Log.i(LOG_TAG, " change the first name");
            }
        }
        else if (mLastNameTextInput.getEditText().getText().hashCode() == s.hashCode())
        {
            if (!mLastName.equals(s.toString()))
            {
                mIsChangeOnProfile = true;
                Log.i(LOG_TAG, " change the last name");

            }
        }
        else if (mEmailTextInput.getEditText().getText().hashCode() == s.hashCode())
        {
            if (!mEmail.equals(s.toString()))
            {
                mIsChangeOnProfile = true;
                Log.i(LOG_TAG, " change the email");
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }
}

