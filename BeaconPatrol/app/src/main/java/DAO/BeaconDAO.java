package DAO;

import org.altbeacon.beacon.Beacon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import Entity.Lot;

/**
 * Created by Tay Hua Swee on 01/11/2017.
 */

public class BeaconDAO {

    private static HashMap<String, Lot> lotMap = new HashMap<String, Lot>();

    //hardcoded list of values
    static {
        lotMap.put("fda50693-a4e2-4fb1-afcf-c6eb07647825", new Lot("fda50693-a4e2-4fb1-afcf-c6eb07647825", "First Beacon", 1.2973784, 103.8495219, 0, 20));
        lotMap.put("0x02696f74736d757367303907", new Lot("0x02696f74736d757367303907", "Second Beacon", 1.2956192, 103.8498277, 0, 20));
        lotMap.put("fda50693-a4e2-4fb1-afcf-c6eb07647869", new Lot("fda50693-a4e2-4fb1-afcf-c6eb07647869", "Third Beacon", 1.2952545, 103.8505429, 0, 20));
    }

    /*


        return lotMap;
    }
*/
    public static HashMap<String, Lot> getLots() {
        return lotMap;

    }

    public static void putLot(String beaconID, String desc, double gpsX, double gpsY, int curCapacity, int maxCapacity, Double dist) {
        Lot lot = new Lot(beaconID, desc, gpsX, gpsY, curCapacity, maxCapacity, dist);
        lotMap.put(beaconID, lot);
    }




}
