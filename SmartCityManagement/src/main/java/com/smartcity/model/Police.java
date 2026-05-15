package main.java.com.smartcity.models;


    class Police extends EmergencyService {
    private int numberOfOfficers;

    public Police(String resourceID, String location, String status, int numberOfOfficers) {
        super(resourceID, location, status);
        this.numberOfOfficers = numberOfOfficers;
    }

    public void setNumberOfOfficers(int numberOfOfficers) {
        this.numberOfOfficers = numberOfOfficers;
    }

    public int getNumberOfOfficers() {
        return numberOfOfficers;
    }

   
    public double calculateMaintenanceCost() {
        return numberOfOfficers * 100.0;
    }

    public String generateUsageReport() {
        return "Police Report: Officers = " + numberOfOfficers + ", Status = " + status;
    }

    
    public String sendEmergencyAlert() {
        return "Police Alert: Emergency at " + location;
    }
    
    public String toString() {
        return super.toString() + ", Type: Police, Officers: " + numberOfOfficers;
    }
}