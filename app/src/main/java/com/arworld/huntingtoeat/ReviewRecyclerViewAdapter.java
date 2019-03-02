package com.arworld.huntingtoeat;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;


public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private final List<ReviewContent.ReviewItem> mValues;

    public ReviewRecyclerViewAdapter(List<ReviewContent.ReviewItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_review_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ReviewContent.ReviewItem review = mValues.get(position);
        holder.mItem = review;
        holder.mAuthorView.setText(review.author);
        holder.mReviewRatingView.setRating(review.rating);
        holder.mRelativeTimeView.setText(review.relativeTime);
        holder.mTextView.setText(review.text);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAuthorView;
        public final TextView mRelativeTimeView;
        public final TextView mTextView;
        public final RatingBar mReviewRatingView;
        public final CardView mCardView;
        public ReviewContent.ReviewItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCardView = (CardView) view.findViewById(R.id.card_view);
            mAuthorView = (TextView) view.findViewById(R.id.review_author);
            mRelativeTimeView = (TextView) view.findViewById(R.id.review_relative_time);
            mTextView = (TextView) view.findViewById(R.id.review_text);
            mReviewRatingView = (RatingBar) view.findViewById(R.id.review_rating);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAuthorView.getText() + "'";
        }
    }
}
