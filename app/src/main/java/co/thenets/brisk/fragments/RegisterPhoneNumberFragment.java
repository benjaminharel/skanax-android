package co.thenets.brisk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.thenets.brisk.R;
import co.thenets.brisk.managers.AnalyticsManager;

/**
 * Created by Roei on 24-Nov-15.
 */

public class RegisterPhoneNumberFragment extends Fragment
{
    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AnalyticsManager.getInstance().insertPhoneScreen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_phone_number_register, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }
}
