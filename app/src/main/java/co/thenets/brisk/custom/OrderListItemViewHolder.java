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
import android.widget.ImageView;
import android.widget.TextView;

import co.thenets.brisk.R;


public class OrderListItemViewHolder extends RecyclerView.ViewHolder
{
    public View view;
    public final ImageView itemImageView;
    public final TextView nameTextView;
    public final TextView dateTextView;
    public final TextView priceTextView;
    public final TextView itemCanceledTextView;
    public CardView itemContainer;

    public OrderListItemViewHolder(View itemView)
    {
        super(itemView);
        view = itemView;
        itemContainer = (CardView) itemView.findViewById(R.id.itemContainer);
        nameTextView = (TextView) itemView.findViewById(R.id.itemNameTextView);
        itemImageView = (ImageView) itemView.findViewById(R.id.itemImageView);
        dateTextView = (TextView) itemView.findViewById(R.id.itemDateTextView);
        priceTextView = (TextView) itemView.findViewById(R.id.itemTotalTextView);
        itemCanceledTextView = (TextView) itemView.findViewById(R.id.itemCanceledTextView);
    }
}