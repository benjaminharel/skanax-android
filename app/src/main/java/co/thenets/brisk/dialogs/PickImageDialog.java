package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.CropActivity;
import co.thenets.brisk.enums.CropType;
import co.thenets.brisk.enums.SourceImageType;
import co.thenets.brisk.general.Params;

/**
 * Created by DAVID on 16/02/2016.
 */
public class PickImageDialog extends DialogFragment implements View.OnClickListener
{
    private View mRootView;
    private ImageButton mAddFromCameraButton;
    private ImageButton mPickFromGalleryButton;
    private SourceImageType mSourceImageType;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSourceImageType = (SourceImageType) getArguments().getSerializable(Params.IMAGE_SOURCE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        mRootView = inflater.inflate(R.layout.dialog_pick_image, null);
        setViews();

        builder.setView(mRootView);
        return builder.create();
    }

    public static PickImageDialog newInstance(SourceImageType sourceImageType)
    {
        PickImageDialog dialogFragment = new PickImageDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Params.IMAGE_SOURCE, sourceImageType);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    private void setViews()
    {
        mAddFromCameraButton = (ImageButton) mRootView.findViewById(R.id.addImageFromCamera);
        mPickFromGalleryButton = (ImageButton) mRootView.findViewById(R.id.pickImageFromGallery);

        mAddFromCameraButton.setOnClickListener(this);
        mPickFromGalleryButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent(getActivity(), CropActivity.class);

        switch (v.getId())
        {
            case R.id.addImageFromCamera:
                intent.putExtra(Params.CROP_TYPE, CropType.CAMERA);
                break;
            case R.id.pickImageFromGallery:
                intent.putExtra(Params.CROP_TYPE, CropType.GALLERY);
                break;
        }

        intent.putExtra(Params.IMAGE_SOURCE, mSourceImageType);
        startActivity(intent);
        dismiss();
    }
}
