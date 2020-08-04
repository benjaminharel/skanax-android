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
import io.techery.properratingbar.ProperRatingBar;


public class StoreItemViewHolder extends RecyclerView.ViewHolder
{
    public View view;
    public CardView itemContainer;
    public final ImageView profileImageView;
    public final TextView nameTextView;
    public final ImageView etaImageView;
    public final TextView etaTextView;
    public final TextView reviewsCounterTextView;
    public final ProperRatingBar ratingBar;
    public final RecyclerView topProductsRecyclerView;
    public final View closedNowView;

    public StoreItemViewHolder(View itemView)
    {
        super(itemView);
        view = itemView;
        itemContainer = (CardView) itemView.findViewById(R.id.itemContainer);
        profileImageView = (ImageView) itemView.findViewById(R.id.profileImageView);
        nameTextView = (TextView) itemView.findViewById(R.id.itemNameTextView);
        etaImageView = (ImageView) itemView.findViewById(R.id.etaImageView);
        etaTextView = (TextView) itemView.findViewById(R.id.etaTextView);
        reviewsCounterTextView = (TextView) itemView.findViewById(R.id.reviewsCounterTextView);
        ratingBar = (ProperRatingBar) itemView.findViewById(R.id.ratingBar);
        topProductsRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        closedNowView = itemView.findViewById(R.id.closedNowView);
    }
}