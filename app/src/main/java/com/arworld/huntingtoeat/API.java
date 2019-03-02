package com.arworld.huntingtoeat;


import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class API {

    //    static String cdn = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    static String cdn = "https://maps.googleapis.com/maps/api/place/";
    static String image_url = "https://maps.googleapis.com/maps/api/place/photo";
    static String places_key = "AIzaSyDS7TIRB3odqBM9ny6mB9ULISHzP9_IFyU";


    static Double curLatitude;
    static Double curLongitude;

    static RequestQueue requestQueue;
    static ImageLoader imageLoader;
    private static ImageLoader.ImageCache imageCache;

    /* JSON tags for Array of places */
    static String jNextPageToken = "next_page_token";
    static String jResults = "results";
    static String jTitle = "name";
    static String jGeometry = "geometry";
    static String jLocation = "location";
    static String jLatitude = "lat";
    static String jLongitude = "lng";
    static String jRating = "rating";
    public static String jId = "id";
    public static String jPlaceId = "place_id";
    static String jPhoto = "photos";
    static String jPhotoReference = "photo_reference";
    static String jAddress = "vicinity";

    /* JSON tags for place details call */
    static String jFormattedAddress = "formatted_address";
    static String jFormattedPhoneNumber = "formatted_phone_number";
    static String jPhotos = "photos";
    static String jResult = "result";
    static String jReviews = "reviews";
    static String jAuthor = "author_name";
    static String jRelativeTime = "relative_time_description";
    static String jText = "text";

    /* Call types */
    static String LIST = "nearbysearch";
    static String DETAILS = "details";

    static void setRequestQueue(RequestQueue r) {
        requestQueue = r;
    }
    public static void initImageLoader() {
        imageCache = new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCashe = new LruCache<String, Bitmap>(10);
            @Override
            public Bitmap getBitmap(String url) {
                return mCashe.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCashe.put(url, bitmap);
            }
        };
        imageLoader = new ImageLoader(requestQueue, imageCache);
    }

    public static String getGetImageUrl(String photoReference, int maxWidth) {


        String url = String.format(Locale.US, "%s?maxwidth=%d&photoreference=%s&key=%s", image_url, maxWidth, photoReference, places_key );
        return url;
    }

    public static void call ( String type, final Map<String, String> headers, JSONObject body, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, int method) {
        JsonObjectRequest request;

        String url = cdn + type + "/json";
        if (headers != null) {
            String query = "?";
            Iterator it = headers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                query += pair.getKey();
                query += "=";
                query += pair.getValue();
                query += "&";
                it.remove(); // avoids a ConcurrentModificationException
            }
            url += query;
            Log.d("API", url);
        }

        request = new JsonObjectRequest(method, url, body, listener, errorListener);

        requestQueue.add(request);
    }

}
