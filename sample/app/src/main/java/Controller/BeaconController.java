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

    static String beaconServer = "";
    static String beaconCAP = "getBeaconCap/?";
    static BufferedReader rd;
    static OutputStreamWriter wr;


    public static int getBeaconCAP(String beaconID) {
        return 2;
        /*
        //where connection goes to
        int result = 0;

        try {
            URL url = new URL(beaconServer + "?" + beaconCAP + "bID=" + beaconID);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.flush();

            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            while ((output = rd.readLine()) != null) {

                Log.d("OUTPUT STREAM RESULT", output);
            }

        } catch (Exception e) {
            Log.d("ERROR IN CONNECTION", e.getStackTrace().toString());
        }

        return result;
        */
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

