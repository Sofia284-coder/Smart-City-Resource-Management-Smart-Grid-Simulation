package main.java.com.smartcity.models;

class FireDepartment extends EmergencyService {
    private int numberOfFireTrucks;

    public FireDepartment(String resourceID, String location, String status, int numberOfFireTrucks) {
        super(resourceID, location, status);
        this.numberOfFireTrucks = numberOfFireTrucks;
    }

    public void setNumberOfFireTrucks(int numberOfFireTrucks) {
        this.numberOfFireTrucks = numberOfFireTrucks;
    }

    public int getNumberOfFireTrucks() {
        return numberOfFireTrucks;
    }

   
    public double calculateMaintenanceCost() {
        return numberOfFireTrucks * 150.0;
    }

    
    public String generateUsageReport() {
        return "Fire Dept Report: Trucks = " + numberOfFireTrucks + ", Status = " + status;
    }

  
    public String sendEmergencyAlert() {
        return "Fire Alert: Emergency at " + location;
    }

   
    public String toString() {
        return super.toString() + ", Type: Fire Department, Fire Trucks: " + numberOfFireTrucks;
    }
}