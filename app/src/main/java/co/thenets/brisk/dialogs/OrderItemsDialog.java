package co.thenets.brisk.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import co.thenets.brisk.R;
import co.thenets.brisk.adapters.OrderRecyclerAdapter;
import co.thenets.brisk.custom.LinearLayoutManager;
import co.thenets.brisk.enums.RoleType;
import co.thenets.brisk.general.Params;
import co.thenets.brisk.models.Order;

/**
 * Created by DAVID on 11/01/2016.
 */
public class OrderItemsDialog extends DialogFragment
{
    private View mRootView;
    private Order mOrder;
    private RecyclerView mRecyclerView;
    private TextView mProductsPriceTextView;
    private TextView mDeliveryPriceTextView;
    private TextView mTotalPriceTextView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOrder = (Order) getArguments().getSerializable(Params.ORDER);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        mRootView = inflater.inflate(R.layout.dialog_order_items_layout, null);
        setViews();

        builder.setView(mRootView);
        return builder.create();
    }

    public static OrderItemsDialog newInstance(Order order)
    {
        OrderItemsDialog dialogFragment = new OrderItemsDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Params.ORDER, order);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    private void setViews()
    {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        OrderRecyclerAdapter orderRecyclerAdapter = new OrderRecyclerAdapter(getActivity(), mOrder, RoleType.CUSTOMER);
        mRecyclerView.setAdapter(orderRecyclerAdapter);

        mProductsPriceTextView = (TextView) mRootView.findViewById(R.id.productsPriceTextView);
        mDeliveryPriceTextView = (TextView) mRootView.findViewById(R.id.deliveryPriceEditText);
        mTotalPriceTextView = (TextView) mRootView.findViewById(R.id.totalPriceTextView);

        mProductsPriceTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mOrder.getProductsSubtotal())));
        mDeliveryPriceTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mOrder.getDeliveryPrice())));
        mTotalPriceTextView.setText(String.format(getString(R.string.price_value), String.valueOf(mOrder.getTotalPriceAsString())));
    }
}
