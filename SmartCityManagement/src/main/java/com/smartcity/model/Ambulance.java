package main.java.com.smartcity.models;

class Ambulance extends EmergencyService {
    private int numberOfMedics;

    public Ambulance(String resourceID, String location, String status, int numberOfMedics) {
        super(resourceID, location, status);
        this.numberOfMedics = numberOfMedics;
    }

    public void setNumberOfMedics(int numberOfMedics) {
        this.numberOfMedics = numberOfMedics;
    }

    public int getNumberOfMedics() {
        return numberOfMedics;
    }

    
    public double calculateMaintenanceCost() {
        return numberOfMedics * 120.0;
    }

 
    public String generateUsageReport() {
        return "Ambulance Report: Medics = " + numberOfMedics + ", Status = " + status;
    }

   
    public String sendEmergencyAlert() {
        return "Ambulance Alert: Emergency at " + location;
    }

    
    public String toString() {
        return super.toString() + ", Type: Ambulance, Medics: " + numberOfMedics;
    }
}
