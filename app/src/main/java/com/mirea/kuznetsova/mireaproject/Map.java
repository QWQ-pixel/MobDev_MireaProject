package com.mirea.kuznetsova.mireaproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class Map extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap map;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private Location lastKnownLocation;

    private Boolean locationPermissionGranted;

    private static final LatLng MAIN_BUILD = new LatLng(55.673296, 37.480067);

    private static final LatLng STAVROPOL = new LatLng(45.052213, 41.912660);

    private static final LatLng FRIAZINO = new LatLng(55.966887, 38.050533);


    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            map.addMarker(new MarkerOptions().position(MAIN_BUILD).title("РТУ МИРЭА")
                    .snippet("Год основания 1947\nМосква, Пр-т Вернадского, д. 78\nКоординаты: 55.673296, 37.480067"));

            map.addMarker(new MarkerOptions().position(FRIAZINO).title("Филиал РТУ МИРЭА в г. Фрязино")
                    .snippet("Год основания 1962\nМосковская область, г. Фрязино, ул. Вокзальная, д. 2а\n" +
                            "Корпус 61\nКоординаты: 55.966887, 38.050533"));

            map.addMarker(new MarkerOptions().position(STAVROPOL).title("Филиал РТУ МИРЭА в г. Ставрополе")
                    .snippet("Год основания 2003\nСтавропольский край\n" +
                            "г. Ставрополь,пр. Кулакова, д. 8\nКоординаты: 45.052213, 41.912660"));

            map.moveCamera(CameraUpdateFactory.newLatLng(MAIN_BUILD));

            map.getUiSettings().setIndoorLevelPickerEnabled(true);

        }

    };

    @Override
    public void onMapLongClick(LatLng latLng) {

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();

        map.animateCamera(CameraUpdateFactory .newCameraPosition(cameraPosition));

        map.addMarker(new MarkerOptions().title("Новое место").position(latLng));
    }
    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            locationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        map.setOnMyLocationButtonClickListener(this);

        map.setOnMyLocationClickListener(this);
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {

                map.setMyLocationEnabled(true);

                map.getUiSettings().setMyLocationButtonEnabled(true);

            } else {

                map.setMyLocationEnabled(false);

                map.getUiSettings().setMyLocationButtonEnabled(false);

                lastKnownLocation = null;

                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    
}