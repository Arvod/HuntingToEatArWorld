package com.arworld.huntingtoeat;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;

import org.w3c.dom.Text;


public class MapFinderFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_TITLE = "title";

    FragmentInteractionListener mListener;

    private String mTitle;
    public GoogleMap mGoogleMap;
    private MapView mMapView;
    private static int permissionStatus;

    public MapFinderFragment() {
        // Required empty public constructor
    }

    public static MapFinderFragment newInstance(String title, int permissionStatus) {
        MapFinderFragment fragment = new MapFinderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        MapFinderFragment.permissionStatus = permissionStatus;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_finder, container, false);

        final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        switch( googleAPI.isGooglePlayServicesAvailable(getActivity()) ) {
            case ConnectionResult.SUCCESS:
                mMapView = (MapView) v.findViewById(R.id.map_view);
                mMapView.onCreate(savedInstanceState);
                mMapView.onResume();
                if( mMapView != null ) {
                    mMapView.getMapAsync(this);
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "Google Play Service Missing", Toast.LENGTH_LONG).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "Google Play Update Required", Toast.LENGTH_LONG).show();
                break;
        }
        return v;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        Location cur_location = ((MainActivity)mListener).mCurrentLocation;

        if (cur_location == null) {
            // TODO: create a new runnable that waits for location permissions to be filled.
        } else {
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(cur_location.getLatitude(), cur_location.getLongitude()) , 15) );
        }

        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d("MapFinder", mGoogleMap.getCameraPosition().target.toString() );
                VisibleRegion visibleRegion = mGoogleMap.getProjection().getVisibleRegion();
                Double radius = distanceBetween(visibleRegion.farLeft, visibleRegion.farRight);
                LatLng center = mGoogleMap.getCameraPosition().target;
                Log.d("MAPFINDER", "-------- radius: " + String.format("%.4f", radius) );
                mListener.try_api(center.latitude, center.longitude, radius);
            }
        });

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String placeId = ((MainActivity) mListener).markerMap.get(marker.getPosition());
                if ( placeId != null ) {
                    mListener.openDetailsActivity(placeId);
                }
            }
        });

    }

    public double distanceBetween(LatLng pt1, LatLng pt2)
    {
        Location location1 = new Location("");
        Location location2 = new Location("");

        location1.setLatitude(pt1.latitude);
        location1.setLongitude(pt1.longitude);

        location2.setLatitude(pt2.latitude);
        location2.setLongitude(pt2.longitude);



            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
            }
        return location1.distanceTo(location2);
    }


    public void gpsActivated(){
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            mListener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
