package co.thenets.brisk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.thenets.brisk.BuildConfig;
import co.thenets.brisk.R;
import co.thenets.brisk.activities.WebViewActivity;
import co.thenets.brisk.general.Params;

/**
 * Created by DAVID-WORK on 07/11/2015.
 */
public class AppInfoFragment extends BasicFragment implements View.OnClickListener
{
    private TextView mPrivacyPolicyTextView;
    private TextView mTacTextView;
    private TextView mTacBriskerTextView;
    private TextView mAppVersionTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_app_info, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
    }

    private void setViews()
    {
        setToolBar();
        setOtherViews();
    }

    private void setOtherViews()
    {
        mPrivacyPolicyTextView = (TextView) mRootView.findViewById(R.id.privacyPolicyTextView);
        mTacTextView = (TextView) mRootView.findViewById(R.id.tacTextView);
        mTacBriskerTextView = (TextView) mRootView.findViewById(R.id.tacBriskerTextView);
        mAppVersionTextView = (TextView) mRootView.findViewById(R.id.appVersionTextView);

        mPrivacyPolicyTextView.setOnClickListener(this);
        mTacTextView.setOnClickListener(this);
        mTacBriskerTextView.setOnClickListener(this);

        mAppVersionTextView.setText(getString(R.string.version) + " "  + BuildConfig.VERSION_NAME);
    }

    private void setToolBar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_info));
        if (getActivity() instanceof AppCompatActivity)
        {
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        }
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);

        switch (v.getId())
        {
            case R.id.privacyPolicyTextView:
                intent.putExtra(Params.URL_TO_OPEN, Params.PRIVACY_POLICY_URL);
                break;
            case R.id.tacTextView:
                intent.putExtra(Params.URL_TO_OPEN, Params.TERMS_AND_CONDITIONS_URL);
                break;
            case R.id.tacBriskerTextView:
                intent.putExtra(Params.URL_TO_OPEN, Params.TERMS_AND_CONDITIONS_BRISKER_URL);
                break;
        }

        startActivity(intent);
    }
}
