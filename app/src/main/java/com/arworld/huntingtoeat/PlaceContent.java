package com.arworld.huntingtoeat;


import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaceContent {

    public static final List<PlaceItem> ITEMS = new ArrayList<PlaceItem>();

    public static final Map<String, PlaceItem> ITEM_MAP = new HashMap<String, PlaceItem>();

    public static void addItem(PlaceItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static int clear(){
        int prev_len = ITEMS.size();
        ITEMS.clear();
        ITEM_MAP.clear();
        return prev_len;
    }

    public static class PlaceItem {
        public String id;
        public String title;
        public String address;
        public Double latitude;
        public Double longitude;
        public Float rating;
        public String image_reference;
        public String image_url;
        public Marker marker;
        public Bitmap image;

        public PlaceItem() {}

        public PlaceItem(String id, String title, String location, Float rating, String image_url) {
            this.id = id;
            this.title = title;
            this.address = location;
            this.rating = rating;
            this.image_url = image_url;
        }

        @Override
        public String toString() {
            return this.title;
        }
    }
}
