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
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import co.thenets.brisk.R;
import co.thenets.brisk.interfaces.OnItemClickListener;
import co.thenets.brisk.managers.UIManager;


public class HashtagViewHolder extends RecyclerView.ViewHolder
{
    public View view;
    public final TextView hashTextView;
    public OnItemClickListener mListener;

    public HashtagViewHolder(View itemView, OnItemClickListener onItemClickListener)
    {
        super(itemView);
        view = itemView;
        mListener = onItemClickListener;
        hashTextView = (TextView) itemView.findViewById(R.id.hashtagTextView);

        itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListener.onItemClicked(getAdapterPosition());
            }
        });

        hashTextView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    UIManager.getInstance().applyTaDaAnimation(v);
                }
                return false;
            }
        });
    }
}