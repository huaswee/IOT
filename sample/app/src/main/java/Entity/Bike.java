package Entity;

/**
 * Created by Tay Hua Swee on 01/11/2017.
 */

public class Bike {
    String bikeID;
    boolean lock;
    String cord;
    static String sampleCord = "100,204";

    public Bike(String bikeID) {
        this(bikeID, true, sampleCord);
    }

    public Bike(String bikeID, String cord) {
        this(bikeID, true, cord);
    }

    public Bike(String bikeID, boolean lock, String cord) {
        this.bikeID = bikeID;
        this.lock = lock;
        this.cord = cord;
    }

    public String getBikeID() {
        return this.bikeID;
    }

    public void setBikeID(String bikeID) {
        this.bikeID = bikeID;
    }

    public boolean getLock() {
        return this.lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getCord() {
        return this.cord;
    }

    public void setCord(String cord) {
        this.cord = cord;
    }
}
