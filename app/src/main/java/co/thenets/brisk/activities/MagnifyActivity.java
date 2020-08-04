package co.thenets.brisk.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import co.thenets.brisk.R;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.StoreProduct;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MagnifyActivity extends AppCompatActivity
{
    private PhotoViewAttacher mAttacher;
    private StoreProduct mStoreProduct;
    private byte[] mPlaceHolderByteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_magnify);

        mStoreProduct = (StoreProduct) getIntent().getSerializableExtra(Params.STORE_PRODUCT);
        mPlaceHolderByteArray = (byte[]) getIntent().getSerializableExtra(Params.PLACE_HOLDER_IMAGE_BYTE_ARRAY);
        setViews();
    }

    private void setViews()
    {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        UIManager.getInstance().loadImage(this, mStoreProduct.getPhotoGallery().getOriginal(), imageView, ImageType.PRODUCT, mPlaceHolderByteArray);
        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(imageView);
    }

    protected void hideStatusBar()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
