package co.thenets.brisk.activities;

/**
 * Created by Roei on 24-Nov-15.
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.SmsBroadcastReceiver;
import co.thenets.brisk.events.CodeSmsArrivedEvent;
import co.thenets.brisk.events.CustomerProfileDetailEvent;
import co.thenets.brisk.fragments.RegisterPhoneNumberFragment;
import co.thenets.brisk.fragments.RegisterProfileFragment;
import co.thenets.brisk.fragments.RegisterVerifyCodeFragment;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.User;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.UpdateUserRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

public class RegisterActivity extends BaseActivity
{
    private FragmentTransaction mTransaction;
    private FloatingActionButton mFab;
    private EditText mPhoneNumberEditText;
    private String mPhoneNumber;
    private User mUser;
    private Context mContext;
    private EditText mVerificationCodeEditText;
    private int mCodeNumber;
    private TextInputLayout mFirstNameTextInput;
    private TextInputLayout mLastNameTextInput;
    private int mRetrySendSmsCounter = 1;
    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 1;
    private BroadcastReceiver mSmsBroadcastReceiver = new SmsBroadcastReceiver();
    private TextInputLayout mEmailTextInput;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;
        setContent(savedInstanceState);
        setViews();

        setToolbar();
        registerSmsReceived();
        EventsManager.getInstance().register(this);
    }

    private void setToolbar()
    {
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        mToolbar.setTitle(R.string.register);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerSmsReceived()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Params.ANDROID_PROVIDER_TELEPHONY_SMS_RECEIVED);
        registerReceiver(mSmsBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        this.unregisterReceiver(mSmsBroadcastReceiver);
        EventsManager.getInstance().unregister(this);
    }

    private void setViews()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof RegisterPhoneNumberFragment)
        {
            mPhoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        }
    }

    private void setContent(Bundle savedInstanceState)
    {
        // Check that the activity is using the layout version with the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null)
        {
            if (savedInstanceState != null)
            {
                return;
            }

            RegisterPhoneNumberFragment registerPhoneNumberFragment = new RegisterPhoneNumberFragment();
            registerPhoneNumberFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, registerPhoneNumberFragment).commit();
        }
    }

    public void fabRegisterClick(View view)
    {
        mTransaction = getSupportFragmentManager().beginTransaction();

        switch (view.getId())
        {
            case R.id.registration_fab:
                mPhoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
                mPhoneNumber = mPhoneNumberEditText.getText().toString();
                if (isValidPhoneNumber())
                {
                    askForReceiveSMSPermission();
                }
                else
                {
                    UIManager.getInstance().displaySnackBarError(view, getString(R.string.not_a_valid_phone_number), Snackbar.LENGTH_LONG);
                }
                break;

            case R.id.registration_code_verification_fab:
                openLogicVerifyCode();
                break;

            case R.id.registration_profile_fab:
                mFab = (FloatingActionButton) findViewById(R.id.registration_profile_fab);

                mFirstNameTextInput = (TextInputLayout) findViewById(R.id.firstNameTextInput);
                mLastNameTextInput = (TextInputLayout) findViewById(R.id.lastNameTextInput);
                mEmailTextInput = (TextInputLayout) findViewById(R.id.emailTextInput);

                if (isNameValid())
                {
                    mUser.setFirstName(mFirstNameTextInput.getEditText().getText().toString());
                    mUser.setLastName(mLastNameTextInput.getEditText().getText().toString());
                    mUser.setEmail(mEmailTextInput.getEditText().getText().toString());
                    if (ContentManager.getInstance().getUserCroppedImageByteArray() != null)
                    {
                        String encodedImage = Base64.encodeToString(ContentManager.getInstance().getUserCroppedImageByteArray(), Base64.DEFAULT);
                        mUser.setImageLink(encodedImage);
                    }
                    sendProfileDataToServer();
                }
                break;
        }
    }

    private void sendProfileDataToServer()
    {
        RestClientManager.getInstance().updateUser(new UpdateUserRequest(mUser), new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                Log.d(LOG_TAG, "Successes");
                EventsManager.getInstance().post(new CustomerProfileDetailEvent());
                AnalyticsManager.getInstance().completeRegistration();
                finish();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                Log.d(LOG_TAG, "onInternalServerFailure");
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                Log.d(LOG_TAG, "onNetworkFailure Code");
                UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.network_problem_please_try_again), Snackbar.LENGTH_LONG);
            }
        });

    }

    public boolean validCode()
    {
        mFab = (FloatingActionButton) findViewById(R.id.registration_code_verification_fab);
        mVerificationCodeEditText = (EditText) findViewById(R.id.codeNumberEditText);
        if (mVerificationCodeEditText.getText().toString().length() == 4)
        {
            mCodeNumber = Integer.parseInt(mVerificationCodeEditText.getText().toString());
            return true;
        }
        else
        {
            UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.not_a_valid_code_number), Snackbar.LENGTH_LONG);
            mVerificationCodeEditText.setText("");
            return false;
        }
    }

    public void sendPhoneNumberToServer()
    {
        mUser = ContentManager.getInstance().getUser();
        mUser.setPhone(mPhoneNumber);

        RestClientManager.getInstance().updateUser(new UpdateUserRequest(mUser), new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                RegisterVerifyCodeFragment registerVerifyCodeFragment = new RegisterVerifyCodeFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Params.COUNTER_OF_SEND_SMS_RETRIES, mRetrySendSmsCounter);
                registerVerifyCodeFragment.setArguments(bundle);
                mTransaction.replace(R.id.fragment_container, registerVerifyCodeFragment);
                mTransaction.addToBackStack(null);
                mTransaction.commitAllowingStateLoss();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                Log.d(LOG_TAG, "onInternalServerFailure");
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                Log.d(LOG_TAG, "onNetworkFailure");
            }
        });
    }

    public void sendVerifyCodeToServer()
    {
        RestClientManager.getInstance().verifyCode(ContentManager.getInstance().getUser().getID(), mCodeNumber, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                if (TextUtils.isEmpty(ContentManager.getInstance().getUser().getFirstName()) && TextUtils.isEmpty(ContentManager.getInstance().getUser().getLastName()))
                {
                    RegisterProfileFragment registerProfileFragment = new RegisterProfileFragment();
                    mTransaction.replace(R.id.fragment_container, registerProfileFragment);
                    mTransaction.addToBackStack(null);
                    mTransaction.commitAllowingStateLoss();
                }
                else
                {
                    Log.d(LOG_TAG, "Successes");
                    EventsManager.getInstance().post(new CustomerProfileDetailEvent());
                    finish();
                }
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                Log.d(LOG_TAG, "onInternalServerFailure Code");
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                Log.d(LOG_TAG, "onNetworkFailure Code" + error);
                UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.code_dosn_t_match), Snackbar.LENGTH_LONG);
                mVerificationCodeEditText.setText("");
            }
        });
    }

    public boolean isValidPhoneNumber()
    {
        if (mPhoneNumber != null)
        {
            if (mPhoneNumber.length() == 10 && mPhoneNumber.startsWith("05"))
            {
                mPhoneNumber.substring(1);
                return true;
            }
            else if (mPhoneNumber.length() == 9 && (mPhoneNumber.startsWith("5")))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isNameLongEnough(String name)
    {
        return name.length() > 1;
    }

    public boolean isNameValid()
    {
        if (!isNameLongEnough(mFirstNameTextInput.getEditText().getText().toString()))
        {
            UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.not_a_valid_name), Snackbar.LENGTH_LONG);
            return false;
        }
        else if (!isNameLongEnough(mLastNameTextInput.getEditText().getText().toString()))
        {
            UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.not_a_valid_last_name), Snackbar.LENGTH_LONG);
            return false;
        }
        else if (!Utils.isEmailValid(mEmailTextInput.getEditText().getText().toString()))
        {
            UIManager.getInstance().displaySnackBarError(mFab, getString(R.string.invalid_email_error), Snackbar.LENGTH_LONG);
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Subscribe
    public void handelSMSCodeEvent(CodeSmsArrivedEvent codeSmsArrivedEvent)
    {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        openLogicVerifyCode();
    }

    private void openLogicVerifyCode()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof RegisterVerifyCodeFragment)
        {
            boolean isTimeoutRunning = ((RegisterVerifyCodeFragment) fragment).isTimeoutRunning();
            if (isTimeoutRunning)
            {
                if (validCode())
                {
                    sendVerifyCodeToServer();
                }
            }
            else
            {
                if (mRetrySendSmsCounter == 1)
                {
                    sendPhoneNumberToServer();
                    mRetrySendSmsCounter--;
                }
                else
                {
                    if (validCode())
                    {
                        sendVerifyCodeToServer();
                    }
                }
            }
        }
    }

    private void askForReceiveSMSPermission()
    {
        int hasReceiveSMSPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if (hasReceiveSMSPermission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSIONS_REQUEST_RECEIVE_SMS);
            return;
        }
        sendPhoneNumberToServer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_RECEIVE_SMS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted!
                    sendPhoneNumberToServer();

                }
                else
                {
                    // permission denied! Disable the functionality that depends on this permission.
                    sendPhoneNumberToServer();

                }
                return;
            }
        }
    }

    public String getPhoneNumber()
    {
        return mPhoneNumber;
    }
}