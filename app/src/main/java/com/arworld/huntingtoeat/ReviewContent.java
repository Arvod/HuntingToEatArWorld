package com.arworld.huntingtoeat;


import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewContent {

    public final List<ReviewContent.ReviewItem> ITEMS = new ArrayList<ReviewContent.ReviewItem>();

    public void addItem(ReviewItem item) {
        ITEMS.add(item);
    }

    public int clear(){
        int prev_len = ITEMS.size();
        ITEMS.clear();
        return prev_len;
    }

    public int size(){
        return ITEMS.size();
    }

    public static class ReviewItem {
        public String author;
        public Float rating;
        public String relativeTime;
        public String text;

        public ReviewItem() {}

        public ReviewItem(String author, Float rating, String relativeTime, String text) {
            this.author = author;
            this.rating = rating;
            this.relativeTime = relativeTime;
            this.text = text;
        }

        @Override
        public String toString() {
            return this.author;
        }
    }

}
