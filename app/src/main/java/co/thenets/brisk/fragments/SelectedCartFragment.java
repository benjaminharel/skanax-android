package co.thenets.brisk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.activities.CustomerOrderActivity;
import co.thenets.brisk.activities.EditProfileActivity;
import co.thenets.brisk.activities.GetAddressAndPaymentActivity;
import co.thenets.brisk.activities.RegisterActivity;
import co.thenets.brisk.adapters.SelectedCartRecyclerAdapter;
import co.thenets.brisk.enums.ImageType;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.general.Utils;
import co.thenets.brisk.managers.AnalyticsManager;
import co.thenets.brisk.managers.ContentManager;
import co.thenets.brisk.managers.EventsManager;
import co.thenets.brisk.managers.UIManager;
import co.thenets.brisk.models.Cart;
import co.thenets.brisk.models.Order;
import co.thenets.brisk.models.OrderForUpload;
import co.thenets.brisk.models.OrderItemForUpload;
import co.thenets.brisk.rest.RestClientManager;
import co.thenets.brisk.rest.requests.CreateOrderRequest;
import co.thenets.brisk.rest.responses.ErrorResponse;
import co.thenets.brisk.rest.service.RequestCreateOrderListener;
import retrofit.RetrofitError;

/**
 * Created by DAVID-WORK on 05/11/2015.
 */
public class SelectedCartFragment extends BasicFragment implements View.OnClickListener
{
    private RecyclerView mRecyclerView;
    private Button mOrderButton;
    private TextView mStoreNameTextView;
    private ImageView mStoreImageView;
    private TextView mProductsPriceTextView;
    private TextView mTotalPriceTextView;
    private TextView mEtaTextView;
    private TextView mDeliveryPriceTextView;
    private TextView mDeliveryPriceBottomTextView;
    private TextView mReviewsTextView;
    private RatingBar mRatingBar;
    private Cart mCart;
    private CardView mItemContainer;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventsManager.getInstance().register(this);

        AnalyticsManager.getInstance().selectedCartScreen();
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
        mRootView = inflater.inflate(R.layout.fragment_selected_cart, container, false);
//        mCart = ContentManager.getInstance().getSelectedCart();
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setCartItems();
        setViews();
        setToolBar();
    }

    private void setViews()
    {
        setHeaderViews();

        ImageButton smsButton = (ImageButton) mRootView.findViewById(R.id.smsButton);
        ImageButton callButton = (ImageButton) mRootView.findViewById(R.id.callButton);
        smsButton.setOnClickListener(this);
        callButton.setOnClickListener(this);

        mOrderButton = (Button) mRootView.findViewById(R.id.orderCartButton);
        mOrderButton.setOnClickListener(this);

        mStoreNameTextView = (TextView) mRootView.findViewById(R.id.itemNameTextView);
        mStoreImageView = (ImageView) mRootView.findViewById(R.id.imageView);

        mStoreNameTextView.setText(mCart.getStore().getName());
        UIManager.getInstance().loadImage(getActivity(), mCart.getStore().getPhotoGallery().getMedium(), mStoreImageView, ImageType.STORE);

        mProductsPriceTextView = (TextView) mRootView.findViewById(R.id.productsPriceTextView);
        mDeliveryPriceBottomTextView = (TextView) mRootView.findViewById(R.id.deliveryPriceBottomEditText);
        mTotalPriceTextView = (TextView) mRootView.findViewById(R.id.totalPriceTextView);

        mProductsPriceTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mCart.getProductsPrice())));
        mDeliveryPriceBottomTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mCart.getStore().getDeliveryPrice())));
        mTotalPriceTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mCart.getTotalPrice())));
    }

    private void setHeaderViews()
    {
        mItemContainer = (CardView) mRootView.findViewById(R.id.itemContainer);
        mEtaTextView = (TextView) mRootView.findViewById(R.id.storeEtaTextView);
        mReviewsTextView = (TextView) mRootView.findViewById(R.id.reviewsCounterTextView);
        mDeliveryPriceTextView = (TextView) mRootView.findViewById(R.id.deliveryPriceEditText);
        mRatingBar = (RatingBar) mRootView.findViewById(R.id.storeRatingBar);

        mEtaTextView.setText(String.format(getString(R.string.avg_eta), mCart.getStore().getETA()));
        mDeliveryPriceTextView.setText(String.format(getActivity().getString(R.string.delivery_price_with_value), String.valueOf((int) mCart.getStore().getDeliveryPrice())));
        mRatingBar.setRating(mCart.getStore().getRating());
        setRatingReviews();

        mItemContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: Implement later
//                ContentManager.getInstance().setCurrentStore(mCart.getStore());
//                Intent intent = new Intent(getActivity(), StoreActivity.class);
//                startActivity(intent);
            }
        });
    }

    private void setRatingReviews()
    {
        if (mCart.getStore().getReviewsCounter() > 0)
        {
            mReviewsTextView.setText(String.format(getString(R.string.number_of_reviews), mCart.getStore().getReviewsCounter()));
        }
        else
        {
            mReviewsTextView.setVisibility(View.GONE);
        }
    }

    private void setCartItems()
    {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        SelectedCartRecyclerAdapter selectedCartRecyclerAdapter = new SelectedCartRecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(selectedCartRecyclerAdapter);
    }

    private void setToolBar()
    {
        mToolbar = (Toolbar) mRootView.findViewById(R.id.selected_cart_toolbar);
        mToolbar.setTitle(getString(R.string.title_activity_cart));
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.callButton:
                Utils.openDialer(getActivity(), mCart.getStore().getPhone());
                break;
            case R.id.smsButton:
                Utils.sendSMS(getActivity(), mCart.getStore().getPhone(), "");
                break;
            case R.id.orderCartButton:
                if (ContentManager.getInstance().isUserRegistered())
                {
                    if (ContentManager.getInstance().isUserCompletelyRegistered())
                    {
                        if ((int) mCart.getProductsPrice() >= mCart.getStore().getMinimumOrderPrice())
                        {
                            // Save orderForUpload for later use
                            ContentManager.getInstance().setOrderForUpload(initOrderForUpload());
                            startActivity(new Intent(getActivity(), GetAddressAndPaymentActivity.class));
                        }
                        else
                        {
                            String minPriceString = String.format(getString(R.string.min_price_error), String.valueOf(mCart.getStore().getMinimumOrderPrice()));
                            UIManager.getInstance().displaySnackBarError(mOrderButton, minPriceString, Snackbar.LENGTH_LONG);
                        }
                    }
                    else
                    {
                        Snackbar.make(v, getString(R.string.edit_your_profile_and_add_first_and_last_name), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.update), new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();
                    }
                }
                else
                {
                    Snackbar.make(v, getString(R.string.please_register_before_order), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.register), new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent mIntent = new Intent(getActivity(), RegisterActivity.class);
                                    startActivity(mIntent);
                                }
                            }).show();
                }
                break;
        }
    }

    private OrderForUpload initOrderForUpload()
    {
        OrderForUpload orderForUpload = new OrderForUpload();
        orderForUpload.setCustomerID(ContentManager.getInstance().getUser().getCustomerID());
//        orderForUpload.setStoreID(ContentManager.getInstance().getSelectedCart().getStore().getID());

        ArrayList<OrderItemForUpload> orderItems = new ArrayList<>();
//        ArrayList<CartItem> cartItems = ContentManager.getInstance().getSelectedCart().getCartItems();
//        for (CartItem cartItem : cartItems)
//        {
//            // Enter the order only items that available in this store
//            if (!TextUtils.isEmpty(cartItem.getStoreProduct().getID()))
//            {
//                OrderItemForUpload orderItem = new OrderItemForUpload();
//                orderItem.setStoreProductID(cartItem.getStoreProduct().getID());
//                orderItem.setAmount(cartItem.getAmount());
//                orderItems.add(orderItem);
//            }
//        }
        orderForUpload.setOrderItemList(orderItems);
        return orderForUpload;
    }

    private void orderThisCart(final View v)
    {
        mOrderButton.setClickable(false);
        mOrderButton.setEnabled(false);

        OrderForUpload orderForUpload = new OrderForUpload();
        orderForUpload.setCustomerID(ContentManager.getInstance().getUser().getCustomerID());
//        orderForUpload.setStoreID(ContentManager.getInstance().getSelectedCart().getStore().getID());

        ArrayList<OrderItemForUpload> orderItems = new ArrayList<>();
//        ArrayList<CartItem> cartItems = ContentManager.getInstance().getSelectedCart().getCartItems();
//        for (CartItem cartItem : cartItems)
//        {
//            // Enter the order only items that available in this store
//            if (!TextUtils.isEmpty(cartItem.getStoreProduct().getID()))
//            {
//                OrderItemForUpload orderItem = new OrderItemForUpload();
//                orderItem.setStoreProductID(cartItem.getStoreProduct().getID());
//                orderItem.setAmount(cartItem.getAmount());
//                orderItems.add(orderItem);
//            }
//        }
        orderForUpload.setOrderItemList(orderItems);
        RestClientManager.getInstance().createOrder(new CreateOrderRequest(orderForUpload), new RequestCreateOrderListener()
        {
            @Override
            public void onSuccess(Order order)
            {
                UIManager.getInstance().displaySnackBar(v, getString(R.string.order_completed), Snackbar.LENGTH_LONG);

                // Move to CustomerOrderActivity
                Intent intent = new Intent(getActivity(), CustomerOrderActivity.class);
                intent.putExtra(Params.ORDER_ID, order.getID());
                startActivity(intent);

                // Finish CartActivity
                getActivity().finish();
            }

            @Override
            public void onInternalServerFailure(ErrorResponse error)
            {
                UIManager.getInstance().displaySnackBar(v, getString(R.string.order_failed), Snackbar.LENGTH_LONG);
                mOrderButton.setClickable(true);
                mOrderButton.setEnabled(true);
            }

            @Override
            public void onNetworkFailure(RetrofitError error)
            {
                UIManager.getInstance().displaySnackBar(v, getString(R.string.order_failed), Snackbar.LENGTH_LONG);
                mOrderButton.setClickable(true);
                mOrderButton.setEnabled(true);
            }
        });
    }
}
