package co.thenets.brisk.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.thenets.brisk.R;
import co.thenets.brisk.custom.HashtagViewHolder;
import co.thenets.brisk.interfaces.OnHashtagClickListener;
import co.thenets.brisk.interfaces.OnItemClickListener;

/**
 * Created by DAVID BELOOSESKY on 09/12/2014.
 */
public class HashtagRecyclerAdapter extends RecyclerView.Adapter<HashtagViewHolder>
{
    private ArrayList<String> mHashtagList;
    private Context mContext;
    private OnHashtagClickListener mHashtagClickListener;

    public HashtagRecyclerAdapter(Context context, OnHashtagClickListener hashtagClickListener, ArrayList<String> hashtags)
    {
        mContext = context;
        mHashtagList = hashtags;
        mHashtagClickListener = hashtagClickListener;
    }

    @Override
    public HashtagViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hashtag_item_for_recycler, parent, false);
        return new HashtagViewHolder(v, new OnItemClickListener()
        {
            @Override
            public void onItemClicked(int position)
            {
                // TODO: Implementing a search by this hashtag!
                mHashtagClickListener.onHashtagClicked(mHashtagList.get(position));
            }
        });
    }

    @Override
    public void onBindViewHolder(HashtagViewHolder holder, final int position)
    {
        holder.hashTextView.setText("#" + mHashtagList.get(position));
        int[] hashtagColors = mContext.getResources().getIntArray(R.array.hashtag_colors);
        int color = hashtagColors[position % hashtagColors.length];
        holder.hashTextView.setBackgroundColor(color);
        holder.itemView.setTag(mHashtagList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return mHashtagList.size();
    }
}
