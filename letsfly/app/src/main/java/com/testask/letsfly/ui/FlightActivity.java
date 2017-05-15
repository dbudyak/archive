package com.testask.letsfly.ui;

import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.testask.letsfly.R;
import com.testask.letsfly.model.City;

import java.util.Collections;
import java.util.List;

import static com.testask.letsfly.util.Utils.getBitmapDescriptor;
import static com.testask.letsfly.util.Utils.getLabelBitmapDescriptor;

public class FlightActivity extends BaseActivity implements OnMapReadyCallback {

    private final static int START_ANIMATION_DELAY = 200;
    private final static int PATH_DOT_STEP = 20;
    private final static int MOVE_TO_POSTION_DELAY = 20;
    private final static int PATH_STEP_COUNT = 500;

    public static final String CITY1_KEY = "key_city1";
    public static final String CITY2_KEY = "key_city2";
    private GoogleMap map;
    private City sourceCity, destCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        sourceCity = extras.getParcelable(CITY1_KEY);
        destCity = extras.getParcelable(CITY2_KEY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng source = new LatLng(sourceCity.getLocation().getLat(), sourceCity.getLocation().getLon());
        LatLng destination = new LatLng(destCity.getLocation().getLat(), destCity.getLocation().getLon());

        addMarker(source, sourceCity.getIata().get(0));
        addMarker(destination, destCity.getIata().get(0));
        addCameraPosition(source, destination);

        startAnimation(
                buildPolyline(source, destination),
                buildAnimMarker(destination)
        );
    }

    private Marker buildAnimMarker(LatLng destination) {
        BitmapDescriptor bitmapDescriptor = getBitmapDescriptor(this, R.layout.marker_anim_view, R.drawable.aircraft);
        return map.addMarker(new MarkerOptions()
                .position(destination)
                .zIndex(3)
                .icon(bitmapDescriptor));
    }

    private void addMarker(LatLng city, String title) {
        BitmapDescriptor bitmapDescriptor = getLabelBitmapDescriptor(this, title);
        map.addMarker(new MarkerOptions()
                .position(city)
                .alpha(0.85f).zIndex(2).draggable(false)
                .icon(bitmapDescriptor));
    }

    private void addCameraPosition(LatLng source, LatLng dest) {
        LatLngBounds bounds = LatLngBounds.builder().include(source).include(dest).build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1000, 1000, 100));
    }

    private List<LatLng> buildPolyline(LatLng source, LatLng dest) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .zIndex(1)
                .width(0f).geodesic(true);

        BitmapDescriptor pathMarkerBitmapDescriptor = getBitmapDescriptor(this, R.layout.marker_dot_view, R.drawable.marker_path_dot);
        for (int i = 0; i < PATH_STEP_COUNT; i++) {
            LatLng interpolate = SphericalUtil.interpolate(source, dest, ((double) i) / ((double) PATH_STEP_COUNT));
            LatLng newLatLng = new LatLng(interpolate.latitude, interpolate.longitude);
            polylineOptions.add(newLatLng);
            if (i % PATH_DOT_STEP == 0) {
                map.addMarker(new MarkerOptions()
                        .position(newLatLng)
                        .alpha(0.7f).zIndex(1).draggable(false)
                        .icon(pathMarkerBitmapDescriptor));
            }
        }
        Polyline polyline = map.addPolyline(polylineOptions);
        return polyline.getPoints();
    }

    private void startAnimation(final List<LatLng> points, final Marker marker) {
        Collections.reverse(points);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!points.isEmpty()) {
                    LatLng poll = points.remove(points.size() - 1);
                    if (!points.isEmpty()) {
                        LatLng next = points.get(points.size() - 1);
                        double heading = SphericalUtil.computeHeading(next, poll);
                        marker.setRotation((float) heading);
                    }
                    marker.setPosition(poll);
                    handler.postDelayed(this, MOVE_TO_POSTION_DELAY);
                } else {
                    finish();
                }
            }
        }, START_ANIMATION_DELAY);
    }

}
