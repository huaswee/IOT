package Entity;

/**
 * Created by Tay Hua Swee on 01/11/2017.
 */

public class Lot {

    String beaconID;
    String desc;
    double gpsX;
    double gpsY;
    int curCapacity;
    int maxCapacity;
    Double dist;

    public Lot(String beaconID) {
        this(beaconID, "", 0.0, 0.0, 0, 1, Double.NaN);
    }

    public Lot(String beaconID, String desc, double gpsX, double gpsY, int curCapacity, int maxCapacity) {
        this(beaconID, desc, gpsX, gpsY, curCapacity, maxCapacity, Double.NaN);
    }

    public Lot(String beaconID, String desc, double gpsX, double gpsY, int curCapacity, int maxCapacity, Double dist) {
        this.beaconID = beaconID;
        this.desc = desc;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.curCapacity = curCapacity;
        this.maxCapacity = maxCapacity;
        this.dist = dist;
    }


    public String getBeaconID() {
        return this.beaconID;
    }

    public void setBeaconID(String beaconID) {
        this.beaconID = beaconID;
    }

    public double getGpsX() {
        return gpsX;
    }

    public void setGpsX(double gpsX) {
        this.gpsX = gpsX;
    }

    public double getGpsY() {
        return gpsY;
    }

    public void setGpsY(int gpsY) {
        this.gpsY = gpsY;
    }

    public int getCurCapacity() {
        return curCapacity;
    }

    public void setCurCapacity(int curCapacity) {
        this.curCapacity = curCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public synchronized Double getDist() {
        return dist;
    }

    public synchronized void setDist(Double dist) {
        this.dist = dist;
    }
}
