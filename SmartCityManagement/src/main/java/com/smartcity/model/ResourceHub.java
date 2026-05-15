package main.java.com.smartcity.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ResourceHub implements Serializable {
    private ArrayList<TransportUnit> transportUnits = new ArrayList<>();
    private ArrayList<PowerStation> powerStations = new ArrayList<>();
    private ArrayList<EmergencyService> emergencyServices = new ArrayList<>();

    public void addTransportUnit(TransportUnit t) { transportUnits.add(t); }
    public void addPowerStation(PowerStation p) { powerStations.add(p); }
    public void addEmergencyService(EmergencyService e) { emergencyServices.add(e); }

    public ArrayList<TransportUnit> getTransportUnits() { return transportUnits; }
    public ArrayList<PowerStation> getPowerStations() { return powerStations; }
    public ArrayList<EmergencyService> getEmergencyServices() { return emergencyServices; }
}
