package co.thenets.brisk.custom;

/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import co.thenets.brisk.R;
import co.thenets.brisk.interfaces.OnCartItemClickListener;


public class CartItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public View view;
    public final ImageView storeImageView;
    public final TextView storeNameTextView;
    public final TextView storeEtaTextView;
    public final TextView storeDeliveryPriceTextView;
    public final TextView totalPriceTextView;
    public final TextView availableItemsTextView;
    public final RatingBar ratingBar;
    public final TextView reviewsTextView;
    public final Button selectCartButton;
    public CardView itemContainer;
    public OnCartItemClickListener mListener;

    public CartItemViewHolder(View itemView, OnCartItemClickListener onCartItemClickListener)
    {
        super(itemView);
        view = itemView;
        mListener = onCartItemClickListener;
        itemContainer = (CardView) itemView.findViewById(R.id.itemContainer);
        storeImageView = (ImageView) itemView.findViewById(R.id.itemImageView);
        storeNameTextView = (TextView) itemView.findViewById(R.id.itemNameTextView);
        storeEtaTextView = (TextView) itemView.findViewById(R.id.storeEtaTextView);
        reviewsTextView = (TextView) itemView.findViewById(R.id.reviewsCounterTextView);
        storeDeliveryPriceTextView = (TextView) itemView.findViewById(R.id.deliveryPriceEditText);
        totalPriceTextView = (TextView) itemView.findViewById(R.id.totalPriceTextView);
        availableItemsTextView = (TextView) itemView.findViewById(R.id.availableItemsTextView);
        ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
        selectCartButton = (Button) itemView.findViewById(R.id.selectCartButton);
        selectCartButton.setOnClickListener(this);
        itemView.setOnClickListener(this);
        itemContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.selectCartButton:
                mListener.onCartSelected(getAdapterPosition());
                break;
            case R.id.itemContainer:
                mListener.onStorePressed(getAdapterPosition());
                break;
        }
    }
}