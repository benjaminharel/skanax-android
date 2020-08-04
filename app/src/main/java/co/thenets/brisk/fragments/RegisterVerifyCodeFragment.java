package co.thenets.brisk.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.RegisterActivity;
import co.thenets.brisk.events.CodeDetectedInSmsEvent;
import co.thenets.brisk.events.CodeSmsArrivedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.EventsManager;

/**
 * Created by Roei on 24-Nov-15.
 */

public class RegisterVerifyCodeFragment extends Fragment
{
    private View mRootView;
    private EditText mVerificationCodeEditText;
    private TextView mCounterTimerTextView;
    private static final int ONE_SEC = (int) TimeUnit.SECONDS.toMillis(1);
    private static final int SIXTY_SEC = (int) TimeUnit.SECONDS.toMillis(60);
    private static final int FINISH_TIME = (int) TimeUnit.SECONDS.toMillis(60);
    private FloatingActionButton mFab;
    private int mCount = 1;

    private CounterTimer mCounterTimer;
    private boolean mIsTimeoutRunning = false;
    private long remainMilli = 0;

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mCount = getArguments().getInt(Params.COUNTER_OF_SEND_SMS_RETRIES);
        }
        EventsManager.getInstance().register(this);

        AnalyticsManager.getInstance().verifyPhoneScreen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_verfiy_code, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
        if (mIsTimeoutRunning)
        {
            mCounterTimer.cancel();
            mCounterTimer = null;
            mIsTimeoutRunning = false;
            changeFab();
        }
        else
        {
            if (remainMilli == 0)
            {
                mCounterTimer = new CounterTimer(FINISH_TIME, ONE_SEC);
            }
            else
            {
                mCounterTimer = new CounterTimer(remainMilli, ONE_SEC);
            }
            mCounterTimer.start();
            mIsTimeoutRunning = true;
        }
    }

    private void changeFab()
    {
        mFab.setImageResource(R.drawable.ic_loop_white_24dp);
    }

    private void setViews()
    {
        TextView titleTextView = (TextView) mRootView.findViewById(R.id.titleTextView);
        String phoneNumber = ((RegisterActivity) getActivity()).getPhoneNumber();
        titleTextView.setText(String.format(getString(R.string.the_code_is_on_the_way_to), phoneNumber));
        mVerificationCodeEditText = (EditText) mRootView.findViewById(R.id.codeNumberEditText);
        mCounterTimerTextView = (TextView) mRootView.findViewById(R.id.counterTimerTextView);
        mFab = (FloatingActionButton) mRootView.findViewById(R.id.registration_code_verification_fab);
    }

    @Override
    public void onDestroy()
    {
        mCounterTimer.cancel();
        EventsManager.getInstance().unregister(this);

        super.onDestroy();
    }

    public boolean isTimeoutRunning()
    {
        return mIsTimeoutRunning;
    }

    class CounterTimer extends CountDownTimer
    {

        public CounterTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish()
        {
            mCounterTimerTextView.setText("00:00");
            mIsTimeoutRunning = false;
            remainMilli = 0;
            if (mCount == 1)
            {
                changeFab();
            }
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            remainMilli = millisUntilFinished;
            String minute = "0" + (millisUntilFinished / ONE_SEC) / SIXTY_SEC;
            String second = "" + (millisUntilFinished / ONE_SEC) % SIXTY_SEC;

            if ((millisUntilFinished / ONE_SEC) / SIXTY_SEC < SIXTY_SEC)
            {
                minute = "0" + (millisUntilFinished / ONE_SEC) / SIXTY_SEC;
            }
            if ((millisUntilFinished / ONE_SEC) % SIXTY_SEC < SIXTY_SEC)
            {
                if ((millisUntilFinished / ONE_SEC)   < 10)
                {
                    second = "0" + (millisUntilFinished / ONE_SEC) % SIXTY_SEC;
                }
                else
                {
                    second = "" + (millisUntilFinished / ONE_SEC) % SIXTY_SEC;
                }
            }

            mCounterTimerTextView.setText(minute + ":" + second);
        }
    }

    public void updateUi(final String newSmsArrive)
    {
        mVerificationCodeEditText.setText(newSmsArrive);
        EventsManager.getInstance().post(new CodeSmsArrivedEvent());
    }

    @Subscribe
    public void handleDetectedInSms(CodeDetectedInSmsEvent codeDetectedInSmsEvent)
    {
        updateUi(codeDetectedInSmsEvent.getValue());
    }
}
