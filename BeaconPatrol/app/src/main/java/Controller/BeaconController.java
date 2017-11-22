package Controller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import DAO.BeaconDAO;
import Entity.Lot;

/**
 * Created by Tay Hua Swee on 01/11/2017.
 */

public class BeaconController {

    static String beaconServer = "http://139.59.228.105/bikes/bp/";
    static String beaconCAP = "getCurrentCap/?bID=";
    static BufferedReader rd;
    static OutputStreamWriter wr;


    public static int getBeaconCAP(String beaconID) {
        //return 2;

        //where connection goes to
        int result = 2;

        try {
            URL url = new URL(beaconServer  + beaconCAP + beaconID);
            Log.d("GETBEACONCAP", "url = " + url);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.flush();
            Log.d("GETBEACONCAP", "Established");
            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            Log.wtf("GETBEACONCAP", rd.toString());
            while ((output = rd.readLine()) != null) {

                Log.e("GETBEACONCAP", output);
            }

        } catch (Exception e) {
            Log.d("ERROR IN CONNECTION", e.getStackTrace().toString());
        }

        return result;

    }

    public static Lot getBeacon(String beaconID) {
        return BeaconDAO.getLots().get(beaconID);
    }

    public static List<Double> getBeaconGPS(String beaconID) {
        Lot beacon = BeaconDAO.getLots().get(beaconID);

        ArrayList<Double> result = new ArrayList<Double>();
        result.add(beacon.getGpsX());
        result.add(beacon.getGpsY());

        return result;
    }



}

