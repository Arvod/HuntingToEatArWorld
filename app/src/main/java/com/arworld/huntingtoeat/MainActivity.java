package com.arworld.huntingtoeat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements FragmentInteractionListener {

    TabLayout mTabLayout;
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    MapFinderFragment map_fragment;
    PlaceListFragment list_fragment;
    HashMap<LatLng, String> markerMap;

    private FusedLocationProviderClient fusedLocationProviderClient;
    public Location mCurrentLocation;
    private static String TAG = "Main Activity";
    private static String nextPageToken;

    private final int PERMISSION_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.INVISIBLE);

        /* Set up vars for Android Volley */
        API.setRequestQueue(Volley.newRequestQueue(this));
        API.initImageLoader();

        markerMap = new HashMap<>();

        /* keep using the same 2 fragments */
        map_fragment = MapFinderFragment.newInstance("test 1", ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        list_fragment = new PlaceListFragment();

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.main_pager);

        //TODO: clean up the adapters
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return map_fragment;

                    case 1:
                        return list_fragment;
                }

                return null;
            }

            @Override
            public int getCount() {
                //TODO: get rid of magic number
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                // TODO: make this cleaner
                if (position == 0) {
                    return "Карта";
                } else {
                    return "Список";
                }
            }
        };
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mTabLayout.setupWithViewPager(mViewPager);



        /* -------------------- Get location permission ------------------------ */
        if ( !getLocationAndCallAPI() ){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Need Location Permission")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map_fragment.gpsActivated();
                    getLocationAndCallAPI();
                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        API.setRequestQueue(Volley.newRequestQueue(this));
        API.initImageLoader();
    }

    public boolean getLocationAndCallAPI() {
        int permission_check = ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION );
        if (permission_check == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mCurrentLocation = location;
                                API.curLatitude = location.getLatitude();
                                API.curLongitude = location.getLongitude();

                                try_api(location.getLatitude(), location.getLongitude(), 500.0);
                                if (map_fragment.mGoogleMap != null) {
                                    map_fragment.mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(API.curLatitude, API.curLongitude) , 16) );
                                }
                            }
                        }
                    });
            return true;
        } else {
            return false;
        }
    }

    public void try_api(Double latitude, Double longitude, Double radius) {
        Log.d(TAG, "----------- trying api ------------");
        Response.Listener test_listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                /* Clear previous list of places */
                list_fragment.mRecyclerView.getAdapter().notifyItemRangeRemoved(0, PlaceContent.clear());
                //map_fragment.mGoogleMap.clear();
                // Note: clearing googlemap markers seems to be screwing things up

                Log.d( TAG, response.toString() );
                try {
                    if (response.has(API.jNextPageToken)) {
                        nextPageToken = response.getString(API.jNextPageToken);
                    } else {
                        nextPageToken = null;
                    }

                    //TODO: add some kind of listener to stop the for loop. just so if multiple responses are received
                    //      it can stop. Multiple responses can happen when the user keeps moving around the map.
                    JSONArray jsonPlaceArray = response.getJSONArray(API.jResults);
                    if (jsonPlaceArray.length() == 0) {
                        Toast.makeText(MainActivity.this, "Не найдено рестаранов", Toast.LENGTH_SHORT).show();
                    }
                    for (int i = 0; i < jsonPlaceArray.length(); i++ ){
                        JSONObject jsonPlace = jsonPlaceArray.getJSONObject(i);
                        final PlaceContent.PlaceItem placeItem = new PlaceContent.PlaceItem();

                        placeItem.id = jsonPlace.getString(API.jPlaceId);
                        placeItem.title = jsonPlace.getString(API.jTitle);

                        JSONObject jsonLocation = jsonPlace.getJSONObject(API.jGeometry).getJSONObject(API.jLocation);
                        placeItem.latitude = Double.parseDouble(jsonLocation.getString(API.jLatitude));
                        placeItem.longitude = Double.parseDouble(jsonLocation.getString(API.jLongitude));
                        placeItem.address = jsonPlace.getString(API.jAddress);

                        if (jsonPlace.has(API.jRating)) {
                            placeItem.rating = Float.parseFloat(jsonPlace.getString(API.jRating));
                        } else {
                            placeItem.rating = 0.0f;
                        }
                        if (jsonPlace.has(API.jPhoto)) {
                            placeItem.image_reference = jsonPlace.getJSONArray(API.jPhoto).getJSONObject(0).getString(API.jPhotoReference);
                        } else {
                            Log.d(TAG, "-------- null image reference");
                            placeItem.image_reference = null;
                        }

                        Log.d(TAG, "---- added " + placeItem.title );

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PlaceContent.addItem(placeItem);
                                list_fragment.mRecyclerView.getAdapter().notifyItemInserted(PlaceContent.ITEMS.size()-1);
                                String id = markerMap.get(new LatLng(placeItem.latitude, placeItem.longitude));

                                // only add new markers into map
                                if (id == null) {
                                    map_fragment.mGoogleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(placeItem.latitude, placeItem.longitude))
                                            .title(placeItem.title).icon(BitmapDescriptorFactory.fromResource(R.drawable.coffeemug)).snippet(placeItem.address));
                                    markerMap.put(new LatLng(placeItem.latitude, placeItem.longitude), placeItem.id);
                                }

                            }
                        });

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener test_error_listener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
        final Map<String, String> headers = new HashMap<>();
        headers.put("key", API.places_key);
        headers.put("location", String.format(Locale.US, "%.4f, %4f", latitude, longitude));
        headers.put("radius", String.format(Locale.US, "%4f", radius));
        headers.put("type", "restaurant");
        API.call(API.LIST, headers, null, test_listener, test_error_listener, Request.Method.GET);
    }

    public void openDetailsActivity(String placeId){
        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
        intent.putExtra("placeId", placeId);
        startActivity(intent);
    }
}
