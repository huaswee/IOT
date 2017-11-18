package Controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;

import com.example.tayhuaswee.test.BasicBeacon;

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

/**
 * Created by Tay Hua Swee on 18/11/2017.
 */

public class GPSController implements BeaconConsumer {

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

    public void startScan() {
        Log.i(TAG, "\nEntering Get Location Permissions~~~\n");

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
                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                    Beacon beacon = beacons.iterator().next();
                    Log.d("SUCCESS BEACON", "ID1 = " + beacon.getId1().toString());
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

    @Override
    public Context getApplicationContext() {
        return null;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {

    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }
}
