package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import Entity.Bike;

/**
 * Created by Tay Hua Swee on 01/11/2017.
 */

public class BikeDAO {

    static HashMap<String, Bike> bikeMap = new HashMap<String, Bike>();
    private static final String getAllBike = "SELECT * FROM Bike";

    public static HashMap<String, Bike> getAllBikeData() {
        try {
            Connection conn = ConnectionManager.getConnection();
            PreparedStatement smt = conn.prepareStatement(getAllBike);
            ResultSet rs = smt.executeQuery();

            while (rs.next()) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return bikeMap;
    }

    public static Bike getBike(String bikeID) {
        return bikeMap.get(bikeID);
    }

    public static void putBike(String bikeID, String cord) {
        Bike bike = new Bike(bikeID, cord);
        bikeMap.put(bikeID, bike);
    }

}
