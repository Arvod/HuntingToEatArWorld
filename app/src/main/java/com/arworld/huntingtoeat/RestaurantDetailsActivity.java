package com.arworld.huntingtoeat;


import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private String TAG = "RestaurantDetailsAct";

    private String mTitle;
    private String mAddress;
    private String mPlaceId;
    private String mContact;
    private float mRating;
    private ArrayList<String> imageReferences;

    private TextView mTitleView, mAddressView, mContactView;
    private RatingBar mRatingBar;
    private ViewPager mImagePager;
    private RecyclerView mReviewRecyclerView;
    private SlidingImageAdapter mSlidingImageAdapter;
    private ReviewRecyclerViewAdapter mReviewRecyclerViewAdapter;
    private ReviewContent mReviewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        API.setRequestQueue(Volley.newRequestQueue(this));
        API.initImageLoader();
        imageReferences = new ArrayList<>();

        Intent intent = getIntent();
        mTitle = intent.getStringExtra("title");
        mAddress = intent.getStringExtra("address");
        mPlaceId = intent.getStringExtra("placeId");
        mRating = intent.getFloatExtra("rating", 0);

        mTitleView = (TextView) findViewById(R.id.detail_title);
        mAddressView = (TextView) findViewById(R.id.detail_address);
        mContactView = (TextView) findViewById(R.id.detail_contact);
        mRatingBar = (RatingBar) findViewById(R.id.detail_rating);
        mImagePager = (ViewPager) findViewById(R.id.detail_image_pager);
        mReviewRecyclerView = (RecyclerView) findViewById(R.id.detail_review_recycler_view);

        mSlidingImageAdapter = new SlidingImageAdapter(mImagePager.getContext(), null);
        mImagePager.setAdapter(mSlidingImageAdapter);

        mReviewContent = new ReviewContent();
        mReviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(mReviewContent.ITEMS);
        mReviewRecyclerView.setAdapter(mReviewRecyclerViewAdapter);
        mReviewRecyclerView.setNestedScrollingEnabled(false);

        mContactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialContactPhone(mContactView.getText().toString());
            }
        });

        getDetails();
    }

    public void onBackPressed() {
        super.onBackPressed();

    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))); }


    private void getDetails() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("key", API.places_key);
        headers.put("placeid", mPlaceId);

        Response.Listener listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    response = response.getJSONObject(API.jResult);
                    mAddress = response.getString(API.jFormattedAddress);
                    mContact = response.getString(API.jFormattedPhoneNumber);
                    mTitle = response.getString(API.jTitle);

                    if (response.has(API.jRating)) {
                        mRating = Float.parseFloat(response.getString(API.jRating));
                    } else {
                        mRating = 0.0f;
                    }

                    if (response.has(API.jPhotos)) {
                        JSONArray jsonPhotoList = response.getJSONArray(API.jPhotos);
                        for (int i = 0; i < jsonPhotoList.length(); i++){
                            imageReferences.add(jsonPhotoList.getJSONObject(i).getString(API.jPhotoReference));
                            mSlidingImageAdapter.addItem( jsonPhotoList.getJSONObject(i).getString(API.jPhotoReference) );
                            mSlidingImageAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // TODO: add no photo image
                        mImagePager.setBackground(getDrawable(R.drawable.ic_photo_black_24dp));
                    }


                    mTitleView.setText(mTitle);
                    mAddressView.setText(mAddress);
                    mContactView.setText(mContact);
                    mRatingBar.setRating(mRating);

                    mReviewContent.clear();
                    JSONArray jsonReviewArray = response.getJSONArray(API.jReviews);

                    for (int i = 0; i < jsonReviewArray.length(); i++) {
                        JSONObject jsonReview = jsonReviewArray.getJSONObject(i);
                        final ReviewContent.ReviewItem reviewItem = new ReviewContent.ReviewItem();

                        reviewItem.author = jsonReview.getString(API.jAuthor);
                        reviewItem.rating = Float.parseFloat(jsonReview.getString(API.jRating));
                        reviewItem.relativeTime = jsonReview.getString(API.jRelativeTime);
                        reviewItem.text = jsonReview.getString(API.jText);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("RestaurantDetails", "----- size: " + mReviewContent.size() + " added review by" + reviewItem.toString());
                                mReviewContent.addItem(reviewItem);
                                mReviewRecyclerView.getAdapter().notifyItemInserted(mReviewContent.ITEMS.size()-1);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };

        API.call(API.DETAILS, headers, null, listener, errorListener, Request.Method.GET);
    }

}
