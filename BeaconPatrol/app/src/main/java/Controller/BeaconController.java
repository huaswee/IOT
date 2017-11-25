package Controller;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
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

    static String beaconServer = "http://139.59.228.105:80/bikes/bp/";
    static String beaconCAP = "getCurrentCap/?bID=";
    static String lockBike = "parkBike/?bID=";
    static String unlockBike = "unlockBike/?bID=";
    static BufferedReader rd;


    public static int getBeaconCAP(String beaconID) {
        //return 2;

        //where connection goes to
        int result = 7;

        try {
            URL url = new URL(beaconServer  + beaconCAP + beaconID);
            //Log.d("GETBEACONCAP", "url = " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            conn.setRequestProperty("Accept", "*/*");
            //wr = new OutputStreamWriter(conn.getOutputStream());

            int status = conn.getResponseCode();
            //Log.d("GETBEACONCAP", Integer.toString(status));
            //Log.d("GETBEACONCAP", "Established");
            // Get the response
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output = null;
            while ((output = rd.readLine()) != null) {
                //Log.e("GETBEACONCAP", output);
				JSONObject jsonObj = new JSONObject(output);
                result = jsonObj.getInt("CurrentCapacity");
                //Log.w("GETBEACONCAP", "Result = " + result);
            }
            conn.disconnect();
        } catch (Exception e) {
            //Log.d("GETBEACONCAP", e.getStackTrace().toString());
        }

        //Log.d("GETBEACONCAP", "result = " + result);

        return result;

    }

    public static void lockBike(String beaconID) {
        try {
            URL url = new URL(beaconServer  + lockBike + beaconID);
            //Log.d("LOCKBIKE", "url = " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            conn.setRequestProperty("Accept", "*/*");
            conn.setInstanceFollowRedirects(false);
            //wr = new OutputStreamWriter(conn.getOutputStream());
            conn.connect();
            conn.getRequestMethod();
            int status = conn.getResponseCode();
            //Log.d("LOCKBIKE", Integer.toString(status));
            //Log.d("LOCKBIKE", "Bike Sent");
            // Get the response
//            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String output = null;
//            while ((output = rd.readLine()) != null) {
//                Log.e("GETBEACONCAP", output);
//                JSONObject jsonObj = new JSONObject(output);
//                result = jsonObj.getInt("CurrentCapacity");
//                Log.w("GETBEACONCAP", "Result = " + result);            }
            conn.disconnect();
            Lot lot = getBeacon(beaconID);
            lot.setCurCapacity(getBeaconCAP(beaconID));
        } catch (Exception e) {
            //Log.d("LOCKBIKE", e.getStackTrace().toString());
        }



    }

    public static void unlockBike(String beaconID) {

        try {
            URL url = new URL(beaconServer  + unlockBike + beaconID);
            //Log.d("UNLOCKBIKE", "url = " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            conn.setRequestProperty("Accept", "*/*");
            //wr = new OutputStreamWriter(conn.getOutputStream());
            conn.connect();
            int status = conn.getResponseCode();
            //Log.d("UNLOCKBIKE", Integer.toString(status));
            //Log.d("UNLOCKBIKE", "Bike Sent");
            // Get the response
//            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String output = null;
//            while ((output = rd.readLine()) != null) {
//                Log.e("GETBEACONCAP", output);
//                JSONObject jsonObj = new JSONObject(output);
//                result = jsonObj.getInt("CurrentCapacity");
//                Log.w("GETBEACONCAP", "Result = " + result);            }
            conn.disconnect();
            Lot lot = getBeacon(beaconID);
            lot.setCurCapacity(getBeaconCAP(beaconID));
        } catch (Exception e) {
            //Log.d("UNLOCKBIKE", e.getStackTrace().toString());
        }

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

