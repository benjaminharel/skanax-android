package co.thenets.brisk.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lyft.android.scissors.CropView;
import com.lyft.android.scissors.GlideBitmapLoader;

import java.io.File;

import co.thenets.brisk.R;
import co.thenets.brisk.enums.CropType;
import co.thenets.brisk.enums.SourceImageType;
import co.thenets.brisk.events.ImageCroppedEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.interfaces.RequestCodes;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static android.graphics.Bitmap.CompressFormat.PNG;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

/**
 * Created by DAVID-WORK on 16/02/2016.
 */
public class CropActivity extends Activity implements View.OnClickListener, View.OnTouchListener
{
    private Bundle mBundle;
    private SourceImageType mSourceImageType;
    private Uri mImageUri;
    private CropView mCropView;
    private FloatingActionButton mCropFab;
    private FloatingActionButton mPickMiniFab;
    private FloatingActionButton mPickFab;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    private CropType mCropType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        setViews();
        getExtras();
        pickImage();
    }

    private void setViews()
    {
        mCropView = (CropView) findViewById(R.id.crop_view);
        mCropFab = (FloatingActionButton) findViewById(R.id.crop_fab);
        mPickMiniFab = (FloatingActionButton) findViewById(R.id.pick_mini_fab);
        mPickFab = (FloatingActionButton) findViewById(R.id.pick_fab);

        mCropView.setOnTouchListener(this);
        mCropFab.setOnClickListener(this);
        mPickMiniFab.setOnClickListener(this);
        mPickFab.setOnClickListener(this);
    }

    private void getExtras()
    {
        mBundle = getIntent().getExtras();
        if (mBundle != null)
        {
            mSourceImageType = (SourceImageType) mBundle.getSerializable(Params.IMAGE_SOURCE);
            mCropType = (CropType) mBundle.getSerializable(Params.CROP_TYPE);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.pick_fab:
            case R.id.pick_mini_fab:
                pickImage();
                break;
            case R.id.crop_fab:
                onCropClicked();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case RequestCodes.PICK_IMAGE_FROM_GALLERY:
                if (resultCode == Activity.RESULT_OK)
                {
                    Uri galleryPictureUri = data.getData();

                    mCropView.extensions()
                            .using(GlideBitmapLoader.createUsing(mCropView))
                            .load(galleryPictureUri);

                    updateButtons();
                }
                else
                {
                    finish();
                }
                break;
            case RequestCodes.PICK_IMAGE_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK)
                {
                    Glide.with(this)
                            .load(mImageUri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(mCropView);

                    updateButtons();
                }
                else
                {
                    finish();
                }
                break;
        }
    }

    private void onCropClicked()
    {
        final File croppedFile = new File(getCacheDir(), "cropped.jpg");

        // Crop image into file
        Observable<Void> onSave = Observable.from(mCropView.extensions()
                .crop()
                .quality(70)
                .format(PNG)
                .into(croppedFile))
                .subscribeOn(io())
                .observeOn(mainThread());

        //  listen to the crop finished, and than convert it to byteArray, and notify listeners
        mSubscription.add(onSave
                .subscribe(new Action1<Void>()
                {
                    @Override
                    public void call(Void nothing)
                    {
                        byte[] byteArray = Utils.fileToByteArray(croppedFile);

                        // save byte array in relevant field of the ContentManager
                        if (mBundle != null)
                        {
                            switch (mSourceImageType)
                            {
                                case FROM_REGISTRATION:
                                    ContentManager.getInstance().setUserCroppedImageByteArray(byteArray);
                                    break;
                                case FROM_OPEN_NEW_STORE:
                                case FROM_EDIT_STORE:
                                    ContentManager.getInstance().setStoreCroppedImageByteArray(byteArray);
                                    break;
                                case FROM_ADD_NEW_PRODUCT:
                                    ContentManager.getInstance().setNewProductCroppedImageByteArray(byteArray);
                                    break;
                                case FROM_SIGN_UP:
                                    ContentManager.getInstance().setUserCroppedImageByteArray(byteArray);
                                    break;
                            }
                        }

                        EventsManager.getInstance().post(new ImageCroppedEvent());
                        finish();
                    }
                }));
    }

    private void pickImage()
    {
        switch (mCropType)
        {
            case CAMERA:
                pickPhotoFromCamera();
                break;
            case GALLERY:
                mCropView.extensions().pickUsing(this, RequestCodes.PICK_IMAGE_FROM_GALLERY);
                break;
        }
    }

    private void pickPhotoFromCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "BriskImage.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        mImageUri = Uri.fromFile(photo);
        startActivityForResult(intent, RequestCodes.PICK_IMAGE_FROM_CAMERA);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mSubscription.unsubscribe();
    }

    private void updateButtons()
    {
        mCropFab.setVisibility(View.VISIBLE);
        mPickMiniFab.setVisibility(View.VISIBLE);
        mPickFab.setVisibility(View.GONE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getPointerCount() > 1 || mCropView.getImageBitmap() == null)
        {
            return true;
        }

        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mCropFab.setVisibility(View.INVISIBLE);
                mPickMiniFab.setVisibility(View.INVISIBLE);
                break;
            default:
                mCropFab.setVisibility(View.VISIBLE);
                mPickMiniFab.setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }
}
