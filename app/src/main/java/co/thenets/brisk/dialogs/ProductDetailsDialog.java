package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.MagnifyActivity;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.enums.ProductView;
import co.thenets.brisk.events.ItemAddedMyInventoryEvent;
import co.thenets.brisk.events.ItemEditedMyInventoryEvent;
import co.thenets.brisk.events.RefreshInventoryEvent;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.StoreProduct;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestListener;
import retrofit.RetrofitError;

import static co.thenets.brisk.general.Params.STORE_ID;

/**
 * Created by DAVID-WORK on 06/12/2015.
 */
public class ProductDetailsDialog extends DialogFragment implements View.OnClickListener
{
    private String mStoreID;
    private StoreProduct mStoreProduct;
    private ProductView mProductView;
    private double mPrice;
    private static final String STORE_PRODUCT = "product";
    private static final String PRODUCT_VIEW = "product_view";
    private View mRootView;
    private TextView mQuantityTextView;
    private TextView mPriceTextView;
    private int mQuantityCounter;
    private Button mActionButton;
    private EditText mStorePriceEditText;
    private ImageView mImageView;
    private TextView mNotesTextView;
    private TextView mContentTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mStoreID = (String) getArguments().getSerializable(STORE_ID);
        mStoreProduct = (StoreProduct) getArguments().getSerializable(STORE_PRODUCT);
        mProductView = (ProductView) getArguments().getSerializable(PRODUCT_VIEW);
        mPrice = mStoreProduct.getPrice();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogCustomTheme);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        mRootView = inflater.inflate(R.layout.product_details_layout, null);
        setViews();

        builder.setView(mRootView);
        return builder.create();
    }

    public static ProductDetailsDialog newInstance(String storeID, StoreProduct storeProduct, ProductView productView)
    {
        ProductDetailsDialog dialogFragment = new ProductDetailsDialog();
        Bundle args = new Bundle();
        args.putSerializable(STORE_ID, storeID);
        args.putSerializable(STORE_PRODUCT, storeProduct);
        args.putSerializable(PRODUCT_VIEW, productView);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        UIManager.getInstance().loadImage(getActivity(), mStoreProduct.getPhotoGallery().getMedium(), mImageView, ImageType.PRODUCT);
    }

    private void setViews()
    {
        // Set buttons
        setButtons();

        // Set quantityView and priceView according to the Dialog view type
        setOtherViews();

        // Set image view
        mImageView = (ImageView) mRootView.findViewById(R.id.itemImageView);
        UIManager.getInstance().loadImage(getActivity(), mStoreProduct.getPhotoGallery().getMedium(), mImageView, ImageType.PRODUCT);
        mImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), MagnifyActivity.class);
                byte[] byteArrayFromImageView = Utils.getByteArrayFromImageView(mImageView);
                intent.putExtra(Params.STORE_PRODUCT, mStoreProduct);
                intent.putExtra(Params.PLACE_HOLDER_IMAGE_BYTE_ARRAY, byteArrayFromImageView);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        // For each shared element, add to this method a new Pair item,
                        // which contains the reference of the view we are transitioning *from*,
                        // and the value of the transitionName attribute
                        new Pair<View, String>(mImageView, getActivity().getString(R.string.tr_image_view)));
                ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
            }
        });

        // Set notes
        setNotes();
    }

    private void setNotes()
    {
        mNotesTextView = (TextView) mRootView.findViewById(R.id.notesTextView);
        if (TextUtils.isEmpty(mStoreProduct.getNotes()))
        {
            mNotesTextView.setVisibility(View.GONE);
        }
        else
        {
            mNotesTextView.setVisibility(View.VISIBLE);
            mNotesTextView.setText(mStoreProduct.getNotes());
        }
    }

    private void setButtons()
    {
        mActionButton = (Button) mRootView.findViewById(R.id.actionButton);
        ImageButton addButton = (ImageButton) mRootView.findViewById(R.id.addButton);
        ImageButton removeButton = (ImageButton) mRootView.findViewById(R.id.removeButton);
        mActionButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
        switch (mProductView)
        {
            case INVENTORY_VIEW:
                mActionButton.setText(getString(R.string.add_to_stock));
                break;
            case MY_INVENTORY_VIEW:
                mActionButton.setText(getString(R.string.update_price));
                break;
        }
    }

    private void setOtherViews()
    {
        LinearLayout quantityView = (LinearLayout) mRootView.findViewById(R.id.quantityView);
        LinearLayout priceView = (LinearLayout) mRootView.findViewById(R.id.priceView);

        switch (mProductView)
        {
            case INVENTORY_VIEW:
            case MY_INVENTORY_VIEW:
                mActionButton.setVisibility(View.VISIBLE);
                quantityView.setVisibility(View.INVISIBLE);
                priceView.setVisibility(View.VISIBLE);
                break;
        }

        // Set product details
        TextView nameTextView = (TextView) mRootView.findViewById(R.id.itemNameTextView);
        TextView brandTextView = (TextView) mRootView.findViewById(R.id.itemBrandTextView);
        mContentTextView = (TextView) mRootView.findViewById(R.id.contentTextView);
        mPriceTextView = (TextView) mRootView.findViewById(R.id.itemMarketPriceTextView);
        mQuantityTextView = (TextView) mRootView.findViewById(R.id.itemQuantityTextView);
        mStorePriceEditText = (EditText) mRootView.findViewById(R.id.storePrice);

        nameTextView.setText(mStoreProduct.getName());
        brandTextView.setText(mStoreProduct.getBrand());
        if (!TextUtils.isEmpty(mStoreProduct.getContent()))
        {
            mContentTextView.setVisibility(View.VISIBLE);
            mContentTextView.setText(mStoreProduct.getContent());
        }
        else
        {
            mContentTextView.setVisibility(View.GONE);
        }

        switch (mProductView)
        {
            case BUYER_VIEW:
                mPriceTextView.setText(String.format(getString(R.string.price_value), Utils.formatDouble(mPrice)));
                mQuantityCounter = ContentManager.getInstance().getItemCounterInCart(mStoreID, mStoreProduct.getID());
                mQuantityTextView.setText(String.valueOf(mQuantityCounter));
                break;
            case INVENTORY_VIEW:
                mPriceTextView.setText(String.format(getString(R.string.average_price), Params.CURRENCY, Utils.formatDouble(mStoreProduct.getMarketPrice())));
                mStorePriceEditText.setText(String.valueOf(mStoreProduct.getMarketPrice()));
                mStorePriceEditText.setSelection(mStorePriceEditText.getText().length());
            case MY_INVENTORY_VIEW:
                mPriceTextView.setText(String.format(getString(R.string.average_price), Params.CURRENCY, Utils.formatDouble(mStoreProduct.getMarketPrice())));
                mPriceTextView.setVisibility(View.GONE);
                mStorePriceEditText.setText(String.valueOf(mPrice));
                mStorePriceEditText.setSelection(mStorePriceEditText.getText().length());
                break;
        }
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.actionButton:
                switch (mProductView)
                {
                    case INVENTORY_VIEW:
                        addProductToStore();
                        break;
                    case MY_INVENTORY_VIEW:
                        updateProductPrice();
                        break;
                }
                break;
            case R.id.addButton:
                mQuantityTextView.setText(String.valueOf(++mQuantityCounter));
                ContentManager.getInstance().addToCart(mStoreID, mStoreProduct);
                UIManager.getInstance().applyRollOutDropOutAnimation(mImageView);
                break;
            case R.id.removeButton:
                if (mQuantityCounter > 0)
                {
                    mQuantityTextView.setText(String.valueOf(--mQuantityCounter));
                    ContentManager.getInstance().removeFromCart(mStoreID, mStoreProduct.getID(), false);
                    UIManager.getInstance().applyHingeDropOutAnimation(mImageView);
                }
                else
                {
                    UIManager.getInstance().applyTaDaAnimation(mImageView);
                }

                break;
        }
    }

    private void addProductToStore()
    {
        StoreProduct storeProduct = new StoreProduct(mStoreProduct);
        storeProduct.setPrice(Double.valueOf(mStorePriceEditText.getText().toString()));
        RestClientManager.getInstance().addNewProductToStore(storeProduct, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                EventsManager.getInstance().post(new RefreshInventoryEvent());
                EventsManager.getInstance().post(new ItemAddedMyInventoryEvent());
                dismiss();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {

            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {

            }
        });
    }

    private void updateProductPrice()
    {
        StoreProduct storeProduct = new StoreProduct(mStoreProduct);
        storeProduct.setPrice(Double.valueOf(mStorePriceEditText.getText().toString()));
        final String storeProductID = ContentManager.getInstance().getStoreProductByProductID(mStoreProduct.getID()).getID();
        storeProduct.setID(storeProductID);
        RestClientManager.getInstance().updateProductPrice(storeProduct, new RequestListener()
        {
            @Override
            public void onSuccess()
            {
                EventsManager.getInstance().post(new ItemEditedMyInventoryEvent(storeProductID));
                dismiss();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {

            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {

            }
        });
    }
}
