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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.thenets.brisk.R;
import co.thenets.brisk.interfaces.OnProductClickListener;


public class MyInventoryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public View view;
    public final ImageView itemImageView;
    public final TextView nameTextView;
    public final TextView brandTextView;
    public final TextView contentTextView;
    public final TextView priceTextView;
    public final LinearLayout actionLayout;
    public OnProductClickListener mListener;

    public MyInventoryItemViewHolder(View itemView, OnProductClickListener onProductClickListener)
    {
        super(itemView);
        view = itemView;
        mListener = onProductClickListener;
        itemImageView = (ImageView) itemView.findViewById(R.id.itemImageView);
        nameTextView = (TextView) itemView.findViewById(R.id.itemNameTextView);
        brandTextView = (TextView) itemView.findViewById(R.id.itemBrandTextView);
        contentTextView = (TextView) itemView.findViewById(R.id.itemContentTextView);
        priceTextView = (TextView) itemView.findViewById(R.id.itemMarketPriceTextView);
        actionLayout = (LinearLayout) itemView.findViewById(R.id.actionLayout);

        itemView.setOnClickListener(this);
        actionLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.actionLayout:
                mListener.onActionClicked(getAdapterPosition());
                break;
            default:
                mListener.onProductClicked(getAdapterPosition());
                break;
        }
    }
}