package main.java.com.smartcity.models;

public class Train extends TransportUnit {
    private int numberOfCoaches;

    public Train(String resourceID, String location, String status, String route, int numberOfCoaches) {
        super(resourceID, location, status, route);
        this.numberOfCoaches = numberOfCoaches;
    }

    public int getNumberOfCoaches() { return numberOfCoaches; }
    public void setNumberOfCoaches(int numberOfCoaches) { this.numberOfCoaches = numberOfCoaches; }

    @Override
    public double calculateMaintenanceCost() { return numberOfCoaches * 20.0; }

    @Override
    public String generateUsageReport() {
        return "Train Report: Coaches = " + numberOfCoaches + ", Route = " + route;
    }

    @Override
    public String toString() {
        return super.toString() + ", Type: Train, Route: " + route + ", Coaches: " + numberOfCoaches;
    }
}
