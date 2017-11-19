package Entity;

public class User {
    private String name;
    private double amount;
    private int points;
    private boolean hasBike;

    public User() {
        this.name = "User";
        this.amount = 100.00;
        this.points = 50;
        this.hasBike = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean getHasBike() {
        return hasBike;
    }

    public void setHasBike(boolean hasBike) {
        this.hasBike = hasBike;
    }
}
