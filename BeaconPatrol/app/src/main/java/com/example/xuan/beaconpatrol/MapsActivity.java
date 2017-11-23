package com.example.xuan.beaconpatrol;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Controller.*;
import DAO.*;
import Entity.Lot;
import Entity.User;

import static Controller.BeaconController.getBeacon;
import static com.example.xuan.beaconpatrol.R.id.button_0;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, BeaconConsumer {

    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    static final int MY_PERMISSIONS_REQUEST_Location = 1;
    static ArrayList<Region> regions = new ArrayList<Region>();

    static {
        regions.add(new Region("iot26", Identifier.parse("fda50693-a4e2-4fb1-afcf-c6eb07647825"), null, null));
        regions.add(new Region("iot09", Identifier.parse("0x02696f74736d757367303907"), null, null));
        regions.add(new Region("iot37", Identifier.parse("fda50693-a4e2-4fb1-afcf-c6eb07647869"), null, null));
        //Identifier.parse("fda50693-a4e2-4fb1-afcf-c6eb07647825"), null, null));

    }

    User user = new User();
    String name = user.getName();
    int points = user.getPoints();
    boolean hasBike = user.getHasBike();

    boolean isNearBeacon = false;
    Lot currentBeaconDetected;

    BeaconController beaconController = new BeaconController();
    BeaconDAO beaconDAO = new BeaconDAO();

    GoogleMap googleMap;
    ImageButton button;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Log.wtf("Entering MapAsync...", "Entering Map");
        mapFragment.getMapAsync(this);
        Log.wtf("Entering MapAsync...", "Out of Map");
        final User user = new User();
        Log.i(TAG, "\nEntering Get Location Permissions~~~\n");
        getLocationPermissions();

        button = (ImageButton) findViewById(button_0);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if (hasBike) {
                    lockBike();
                } else {
                    unlockBike();
                }
            }
        });
        callAsyncTask();

    }

    public void lockBike() {
        Toast.makeText(this, "Hi " + name + ", you have " + points + " points.", Toast.LENGTH_LONG).show();

        new AlertDialog.Builder(this).setTitle("Confirmation")
                .setMessage("Do you want to lock your bike here?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isNearBeacon == true) {
                            //add user points
                            points = user.getPoints() + 5;
                            //add to currentCapacity
                            beaconController.lockBike(currentBeaconDetected.getBeaconID());
                        } else {
                            //deduct from user points
                            points = user.getPoints() - 10;
                        }
                        user.setPoints(points);
                        hasBike = false;
                        button.setImageResource(R.drawable.unlock);
                        Toast.makeText(MapsActivity.this, "Hi " + name + ", after locking you have " + points + " points.", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();

    }

    public void unlockBike() {
        Toast.makeText(this, "Hi " + name + ", you have " + points + " points.", Toast.LENGTH_LONG).show();

        new AlertDialog.Builder(this).setTitle("Confirmation")
                .setMessage("Do you want to unlock a bike here?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isNearBeacon == true) {
                            //deduct from currentCapacity
                            beaconController.unlockBike(currentBeaconDetected.getBeaconID());
                        }
                        hasBike = true;
                        button.setImageResource(R.drawable.lock);

                        Toast.makeText(MapsActivity.this, "The bike has been unlocked! Ride safely and responsibly!", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();

    }

    public List<Lot> list = new ArrayList<Lot>();
    public Map<String, Marker> detectedBeacons = new HashMap<String, Marker>();

    public void refreshMarkers() {
        Log.d(TAG, "REFRESHING......");
        Log.d(TAG, "REFRESHING START");

        //Map<String, Marker> updatedBeacons = new HashMap<String, Marker>();

        //clear the markers on map
        googleMap.clear();

        // all existing markers that are left in markers list need to be cleared
        detectedBeacons.clear();

        Log.d(TAG, "CHECK IF LIST IS EMPTY: " + list.toString());

        int beaconsDetected = 0;

        for (Lot beacon : list) {
            String beaconID = beacon.getBeaconID();
            Log.d(TAG, "BEACON ID: " + beaconID);
            Double distance = beacon.getDist();
            Log.d(TAG, "BEACON DISTANCE: " + distance);
            Marker marker;


            // if beacon distance > 0, place marker, else do nothing

            if (distance.isNaN() == false) {
                Log.d(TAG, "BEACON DETECTED");
                currentBeaconDetected = beacon;
                beaconsDetected++;
            } else {
                Log.d(TAG, "BEACON NOT DETECTED");
                //currentBeaconDetected = null;
                //isNearBeacon = false;
            }

            Log.d(TAG, "PLACING MARKER ON MAP");
            LatLng latlng = new LatLng(beacon.getGpsX(), beacon.getGpsY());
            Log.d(TAG, "MARKER COORDINATES: " + latlng);
            Log.d(TAG, "MARKER DESCRIPTION: " + beacon.getDesc());
            Log.d(TAG, "MARKER CUR_CAPACITY: " + beacon.getCurCapacity());
            Log.d(TAG, "MARKER MAX_CAPACITY: " + beacon.getMaxCapacity());

            MarkerOptions markerOptions = new MarkerOptions().position(latlng).title(beacon.getDesc() + " | Capacity: "
                    + beacon.getCurCapacity() + "/" + beacon.getMaxCapacity());

            // check current capacity and change colour before placing onto the map
            if(beacon.getCurCapacity() <= 15) {
                //under capacity
                marker = googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else if(beacon.getCurCapacity() < 20) {
                //nearing capacity
                marker = googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            }else {
                //full or overcapacity
                marker = googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

            Log.d(TAG, "MARKER PLACED");
            detectedBeacons.put(beaconID, marker);
            Log.d(TAG, "detectedBeacons: " + detectedBeacons.toString());

            /*} else {
                Log.d(TAG, "ENTERING ELSE CONDITION......");
                Log.d(TAG, "BEACON NOT DETECTED");
                Log.d(TAG, "detectedBeacons: " + detectedBeacons.toString());
                LatLng latlng = new LatLng(beacon.getGpsX(), beacon.getGpsY());
                marker.setPosition(latlng);
                detectedBeacons.remove(beacon.getBeaconID());
            }*/
            //Log.d(TAG, "updatedBeacons: " + updatedBeacons.toString());
            //updatedBeacons.put(beacon.getBeaconID(), marker);
        }

        if (beaconsDetected >= 1) {
            isNearBeacon = true;
        } else {
            isNearBeacon = false;
        }


        /*for (Marker marker : detectedBeacons.values()) {
            if (marker != null) {
                marker.remove();
            }
        }*/

        //detectedBeacons = updatedBeacons;
        Log.d(TAG, "REFRESHING END");
        Log.d(TAG, "detectedBeacons: " + detectedBeacons.toString());


    }

    public void callAsyncTask() {
        Log.wtf("Entering AsyncTask", "Entering AsyncTask");
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsyncTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            // put your code here
                            Log.wtf("Entering AsyncTask", "refreshingMarkers");
                            refreshMarkers();
                        } catch (Exception e) {


                        }

                    }
                });
            }
        };

        timer.schedule(doAsyncTask, 0, 10000);

    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        // Get all the beacons from database
        HashMap<String, Lot> lotMap = beaconDAO.getLots();

        // Create an ArrayList of Marker objects
        List<Marker> markers = new ArrayList<Marker>();

        //Loop through lotMap, get LatLng for each beacon
        for (Map.Entry<String, Lot> beacon : lotMap.entrySet()) {
            list.add(beacon.getValue());

            String beaconID = beacon.getKey();
            LatLng latlng = new LatLng(beacon.getValue().getGpsX(), beacon.getValue().getGpsY());
            String beaconDescription = beacon.getValue().getDesc();
            int curCapacity = beacon.getValue().getCurCapacity();
            int maxCapacity = beacon.getValue().getMaxCapacity();
            Marker marker;

            MarkerOptions markerOptions = new MarkerOptions().position(latlng).title(beaconDescription + " | Capacity: "
                    + curCapacity + "/" + maxCapacity);

            // check current capacity and change colour before placing onto the map
            if(curCapacity <= 15) {
                //under capacity
                marker = googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else if(curCapacity < 20) {
                //nearing capacity
                marker = googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            }else {
                //full or overcapacity
                marker = googleMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }

            markers.add(marker);

        /*
        LatLng smu_sis = new LatLng(1.2973784, 103.8495219);
        markers.add(googleMap.addMarker(new MarkerOptions().position(smu_sis).title("SMU SIS Capacity: 1/5")));
        LatLng smu_soa = new LatLng(1.2956192, 103.8498277);
        markers.add(googleMap.addMarker(new MarkerOptions().position(smu_soa).title("SMU SOA Capacity: 2/5")));
        LatLng smu_sob = new LatLng(1.2952545, 103.8505429);
        markers.add(googleMap.addMarker(new MarkerOptions().position(smu_sob).title("SMU SOB Capacity: 3/5")));
        LatLng smu_soe = new LatLng(1.2979327, 103.8489191);
        markers.add(googleMap.addMarker(new MarkerOptions().position(smu_soe).title("SMU SOE Capacity: 4/5")));
        LatLng smu_sol = new LatLng(1.2948224, 103.8495096);
        markers.add(googleMap.addMarker(new MarkerOptions().position(smu_sol).title("SMU SOL Capacity: 5/5")));
        */

            // Zoom into Google Maps while fitting all markers inside
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker m : markers) {
                builder.include(m.getPosition());
            }

            LatLngBounds bounds = builder.build();

            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 500, 500, 0);
            googleMap.animateCamera(cu);

            googleMap.setOnInfoWindowClickListener(this);
            this.googleMap = googleMap;
        }
    }

    // When user clicks on marker info
    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onResume() {
        super.onResume();
        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        beaconManager.getBeaconParsers().clear();

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.i(TAG, "\nEntered OnBeaconServiceConnect!!!\n");
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG, "I detected a beacon in the region with namespace id " + region.getId1() +
                        " and instance id: " + region.getId2());
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "\nI no longer see an beacon\n");
                HashMap<String, Lot> lotmap = BeaconDAO.getLots();

                Iterator it = lotmap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Lot> pair = (Map.Entry)it.next();
                    pair.getValue().setDist(Double.NaN);
                    //pair.getValue().setCurCapacity(0);
                    //it.remove(); // avoids a ConcurrentModificationException
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "\nI have just switched from seeing/not seeing beacons: " +state + "\n");
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {
                        Log.i(TAG, "The first beacon I see is about "+beacon.getDistance()+" meters away.");
                        String beaconID = beacon.getId1().toString();
                        Log.d("SUCCESS BEACON", "ID = " + beaconID);

                        Lot lot = getBeacon(beaconID);
                        if (!Double.isNaN(beacon.getDistance()))
                            lot.setDist(beacon.getDistance());
                        lot.setCurCapacity(BeaconController.getBeaconCAP(beaconID));
                        Log.wtf("CHECK LOT", "Lot ID: " + beaconID + " Dist: " + lot.getDist() + " Cur Capacity: " + lot.getCurCapacity());
                    }

                    //Beacon beacon = beacons.iterator().next();


                    //Log.d(TAG, UrlBeaconUrlCompressor.uncompress(beacons.iterator().next().getId1().toByteArray()));
                }
            }
        });

        try {
            Log.i(TAG, "\nStarting Beacon Monitor...\n");
            for (Region region: regions) {
                beaconManager.startMonitoringBeaconsInRegion(region);
                beaconManager.startRangingBeaconsInRegion(region);
            }

        } catch (RemoteException e) {
            Log.i(TAG, "\nREMOTE EXCEPTION OCCURED.\n");
        }
    }

    public void getLocationPermissions() {
        Log.i(TAG, "\nDisplaying Get Location Permissions...!!!\n");
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_Location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i(TAG, "\nEntered Permission Results!!!\nRequest Code = " + requestCode +
                " and My Location = " + MY_PERMISSIONS_REQUEST_Location + "\n" +
                "GrantResults.length = " + grantResults.length + " ");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_Location: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Log.i(TAG, "\nInitializing BeaconManager...\n");
                    beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
                    // To detect proprietary beacons, you must add a line like below corresponding to your beacon
                    // type.  Do a web search for "setBeaconLayout" to get the proper expression.
                    beaconManager.getBeaconParsers().clear();

                    beaconManager.getBeaconParsers().add(new BeaconParser().
                            setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
                    beaconManager.getBeaconParsers().add(new BeaconParser().
                            setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
                    beaconManager.getBeaconParsers().add(new BeaconParser().
                            setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));

                    beaconManager.getBeaconParsers().add(new BeaconParser().
                            setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
                    //setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));

                    //beaconManager.setForegroundScanPeriod(10000);
                    beaconManager.bind(this);
                    Log.i(TAG, "\nCompleted Initializing....\n");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}