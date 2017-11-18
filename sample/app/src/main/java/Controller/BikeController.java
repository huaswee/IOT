package Controller;

import java.util.ArrayList;
import java.util.HashMap;

import DAO.BeaconDAO;
import DAO.BikeDAO;
import Entity.Lot;
import Entity.Bike;

/**
 * Created by Tay Hua Swee on 01/11/2017.
 */

public class BikeController {
    private static HashMap<String, Lot> lotMap;
    private static HashMap<String, Bike> bikeMap;


    public void getBikes() {
    bikeMap = BikeDAO.getAllBikeData();
    }

}
