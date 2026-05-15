package main.java.com.smartcity.models;

import java.io.Serializable;
 
public abstract class CityResource implements Serializable {
 
    protected String resourceID;
    protected String location;
    protected String status;
 
    public CityResource(String resourceID, String location, String status) {
        this.resourceID = resourceID;
        this.location = location;
        this.status = status;
    }
 
    public String getResourceID() { return resourceID; }
    public void setResourceID(String resourceID) { this.resourceID = resourceID; }
 
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
 
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
 
    public abstract double calculateMaintenanceCost();
 
    @Override
    public String toString() {
        return "ID: " + resourceID + ", Location: " + location + ", Status: " + status;
    }
}
