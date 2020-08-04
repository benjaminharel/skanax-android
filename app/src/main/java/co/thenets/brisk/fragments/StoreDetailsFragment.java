package co.thenets.brisk.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.BecomeBriskerActivity;
import co.thenets.brisk.dialogs.PickImageDialog;
import co.thenets.brisk.enums.SourceImageType;
import co.thenets.brisk.events.ImageCroppedEvent;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class StoreDetailsFragment extends Fragment implements View.OnClickListener
{
    private static final String LOG_TAG = StoreDetailsFragment.class.getSimpleName();
    private View mRootView;
    private Toolbar mToolbar;
    private FloatingActionButton mFAB;
    private BecomeBriskerActivity mBecomeBriskerActivity;
    private ImageView mCroppedImageView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventsManager.getInstance().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventsManager.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_store_details, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setViews();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity)
        {
            activity = (Activity) context;
            mBecomeBriskerActivity = (BecomeBriskerActivity) activity;
        }
    }

    private void setViews()
    {
        mCroppedImageView = (ImageView) mRootView.findViewById(R.id.croppedImageView);
        mCroppedImageView.setOnClickListener(this);

        mToolbar = (Toolbar) mRootView.findViewById(R.id.store_details_toolbar);
        mToolbar.setTitle(getString(R.string.store_details));
        mBecomeBriskerActivity.setSupportActionBar(mToolbar);
        mBecomeBriskerActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFAB = (FloatingActionButton) mRootView.findViewById(R.id.store_details_fab);
        fixFABMargin();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.croppedImageView:
                PickImageDialog pickImageDialog = PickImageDialog.newInstance(SourceImageType.FROM_OPEN_NEW_STORE);
                pickImageDialog.show(getFragmentManager(), PickImageDialog.class.getSimpleName());
                break;
        }
    }

    @Subscribe
    public void refreshCroppedImage(ImageCroppedEvent imageCroppedEvent)
    {
        byte[] byteArray = ContentManager.getInstance().getStoreCroppedImageByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        mCroppedImageView.setImageBitmap(bitmap);
    }

    public void fixFABMargin()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mFAB.getLayoutParams();
            // get rid of margins since shadow area is now the margin
            p.setMargins(0, 0, Utils.dpToPx(getActivity(), 8), 0);
            mFAB.setLayoutParams(p);
        }
    }
}
