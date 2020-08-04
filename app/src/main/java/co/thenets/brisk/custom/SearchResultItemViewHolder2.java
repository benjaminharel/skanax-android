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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import co.thenets.brisk.R;
import co.thenets.brisk.interfaces.SearchActionsListener;
import de.hdodenhof.circleimageview.CircleImageView;


public class SearchResultItemViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public View view;
    public LinearLayout itemContainer;
    public SearchActionsListener listener;

    // Header related views
    public CardView storeMiniHeader;
    public TextView storeNameTextView;
    public CircleImageView imageView;
    public RatingBar ratingBar;
    public TextView reviewsCounter;

    // Recycler View
    public RecyclerView productsRecyclerView;

    public SearchResultItemViewHolder2(View itemView, SearchActionsListener searchActionsListener)
    {
        super(itemView);
        view = itemView;
        listener = searchActionsListener;
        itemContainer = (LinearLayout) itemView.findViewById(R.id.itemContainer);

        storeMiniHeader = (CardView) itemView.findViewById(R.id.storeMiniHeader);
        storeMiniHeader.setOnClickListener(this);

        storeNameTextView = (TextView) itemView.findViewById(R.id.itemNameTextView);
        imageView = (CircleImageView) itemView.findViewById(R.id.imageView);
        ratingBar = (RatingBar) itemView.findViewById(R.id.storeRatingBar);
        reviewsCounter = (TextView) itemView.findViewById(R.id.reviewsCounterTextView);
        productsRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.storeMiniHeader:
                listener.onStorePressed(getAdapterPosition());
                break;
        }
    }
}