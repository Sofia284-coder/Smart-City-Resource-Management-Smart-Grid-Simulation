package main.java.com.smartcity.models;

import main.java.com.smartcity.interfaces.Alertable;
import main.java.com.smartcity.interfaces.Reportable;

public class PowerStation extends CityResource implements Reportable, Alertable {
    private double productionRate;
    private CityZone cityZone;

    public PowerStation(String resourceID, String location, String status, double production, CityZone zone) {
        super(resourceID, location, status);
        this.productionRate = production;
        this.cityZone = zone;
    }

    public double getProductionRate() { return productionRate; }
    public void setProductionRate(double productionRate) { this.productionRate = productionRate; }
    public CityZone getCityZone() { return cityZone; }
    public void setCityZone(CityZone cityZone) { this.cityZone = cityZone; }

    @Override
    public double calculateMaintenanceCost() { return productionRate * 0.75; }

    @Override
    public String generateUsageReport() {
        return "PowerStation Report: Energy Output = " + productionRate + " kWh";
    }

    @Override
    public String sendEmergencyAlert() {
        return "PowerStation Alert: Possible outage at " + location;
    }

    public void triggerOutage() {
        this.setStatus("Outage");
        System.out.println(sendEmergencyAlert());
        if (cityZone != null) {
            cityZone.alertEmergencyServices();
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", Type: PowerStation, Energy Output: " + productionRate + " kWh";
    }
}

