package co.thenets.brisk.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;

import co.thenets.brisk.R;
import co.thenets.brisk.dialogs.PickImageDialog;
import co.thenets.brisk.enums.SourceImageType;
import co.thenets.brisk.events.ImageCroppedEvent;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;

/**
 * Created by Roei on 24-Nov-15.
 */

public class RegisterProfileFragment extends Fragment implements View.OnClickListener
{
    private View mRootView;
    private ImageView mProfileImageView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventsManager.getInstance().register(this);
        setToolbarTitle();
        AnalyticsManager.getInstance().insertUserDetailsScreen();
    }

    private void setToolbarTitle()
    {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.complete_profile_details);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        EventsManager.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_profile_register, container, false);
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
        mProfileImageView = (ImageView) mRootView.findViewById(R.id.profileImage);
        mProfileImageView.setOnClickListener(this);
        Utils.hideKeyboard(mProfileImageView, getActivity());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.profileImage:
                PickImageDialog pickImageDialog = PickImageDialog.newInstance(SourceImageType.FROM_REGISTRATION);
                pickImageDialog.show(getFragmentManager(), PickImageDialog.class.getSimpleName());
                break;
        }
    }

    @Subscribe
    public void refreshCroppedImage(ImageCroppedEvent imageCroppedEvent)
    {
        byte[] byteArray = ContentManager.getInstance().getUserCroppedImageByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        ImageView croppedImageView = (ImageView) mRootView.findViewById(R.id.profileImage);
        croppedImageView.setImageBitmap(bitmap);
    }

}
