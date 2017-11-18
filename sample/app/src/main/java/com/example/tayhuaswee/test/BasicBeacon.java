package com.example.tayhuaswee.test;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Controller.BeaconController;
import DAO.BeaconDAO;
import Entity.Lot;

public class BasicBeacon extends Activity implements BeaconConsumer {

    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;
    static final int MY_PERMISSIONS_REQUEST_Location = 1;
    static ArrayList<Region> regions = new ArrayList<Region>();
    static {
    regions.add(new Region("iot26",
            Identifier.parse("fda50693-a4e2-4fb1-afcf-c6eb07647825"), null, null));
    regions.add(new Region("iot09", Identifier.parse("0x02696f74736d757367303907"), null, null));
            //Identifier.parse("fda50693-a4e2-4fb1-afcf-c6eb07647825"), null, null));

    }
    //    Region region = new Region("iot26",
    //           //identifiers)
    //          Identifier.parse("fda50693-a4e2-4fb1-afcf-c6eb07647825"), null, null);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_beacon);
        Log.i(TAG, "\nEntering Get Location Permissions~~~\n");
        getLocationPermissions();
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
                /*
                HashMap<String, Lot> lotmap = BeaconDAO.getLots();

                Iterator it = lotmap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, Lot> pair = (Map.Entry)it.next();
                    pair.getValue().setDist(Double.NaN);
                    pair.getValue().setCurCapacity(0);
                    it.remove(); // avoids a ConcurrentModificationException
                }
                */
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

                        Lot lot = BeaconController.getBeacon(beaconID);
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
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_Location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
