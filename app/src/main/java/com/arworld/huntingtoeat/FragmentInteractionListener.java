package com.arworld.huntingtoeat;

public interface FragmentInteractionListener {
    void try_api(Double latitude, Double longitude, Double radius);
    void openDetailsActivity(String placeId);
}

