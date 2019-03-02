package com.arworld.huntingtoeat;


import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import com.arworld.huntingtoeat.PlaceContent.PlaceItem;

import java.util.List;

public class    MyPlaceRecyclerViewAdapter extends RecyclerView.Adapter<MyPlaceRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceItem> mValues;
    private final FragmentInteractionListener mListener;

    public MyPlaceRecyclerViewAdapter(List<PlaceItem> items, FragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_place_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PlaceItem place = mValues.get(position);
        holder.mItem = place;
        holder.mCardTitleView.setText(place.title);
        holder.mCardLocationView.setText(place.address);
        holder.mCardRatingView.setRating(place.rating);

        if (API.imageLoader == null) {
            Log.d("ViewAdapter", "----- image loader is null");
        }

        if (place.image_reference != null) {
            API.imageLoader.get(API.getGetImageUrl(place.image_reference, 400), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    holder.mCardImageView.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
        }

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.openDetailsActivity(place.id);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCardTitleView;
        public final TextView mCardLocationView;
        public final ImageView mCardImageView;
        public final RatingBar mCardRatingView;
        public final CardView mCardView;
        public PlaceItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCardView = (CardView) view.findViewById(R.id.card_view);
            mCardImageView = (ImageView) view.findViewById(R.id.card_image_view);
            mCardTitleView = (TextView) view.findViewById(R.id.card_title);
            mCardLocationView = (TextView) view.findViewById(R.id.card_location);
            mCardRatingView = (RatingBar) view.findViewById(R.id.card_ratingbar);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCardTitleView.getText() + "'";
        }
    }
}

