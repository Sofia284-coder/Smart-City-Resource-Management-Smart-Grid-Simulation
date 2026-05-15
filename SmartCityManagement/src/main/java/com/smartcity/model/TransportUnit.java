package main.java.com.smartcity.models;

import main.java.com.smartcity.interfaces.Reportable;
 

public abstract class TransportUnit extends CityResource implements Reportable {
 
    protected String route;
 
    public TransportUnit(String resourceID, String location, String status, String route) {
        super(resourceID, location, status);
        this.route = route;
    }
 
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
}