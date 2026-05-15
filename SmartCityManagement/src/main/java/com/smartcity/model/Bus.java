package main.java.com.smartcity.models;

public class Bus extends TransportUnit {
    private int numberOfPassengers;

    public Bus(String resourceID, String location, String status, String route, int numberOfPassengers) {
        super(resourceID, location, status, route);
        this.numberOfPassengers = numberOfPassengers;
    }

    public int getNumberOfPassengers() { return numberOfPassengers; }
    public void setNumberOfPassengers(int numberOfPassengers) { this.numberOfPassengers = numberOfPassengers; }

    @Override
    public double calculateMaintenanceCost() { return numberOfPassengers * 5.0; }

    @Override
    public String generateUsageReport() {
        return "Bus Report: Passengers = " + numberOfPassengers + ", Route = " + route;
    }

    @Override
    public String toString() {
        return super.toString() + ", Type: Bus, Route: " + route + ", Passengers: " + numberOfPassengers;
    }
}
