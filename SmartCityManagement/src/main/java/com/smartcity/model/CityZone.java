package main.java.com.smartcity.models;

import java.io.Serializable;

public class CityZone implements Serializable {
    private String zoneName;
    private ResourceHub hub;

    public CityZone(String zoneName) {
        this.zoneName = zoneName;
        this.hub = new ResourceHub();
    }

    public String getZoneName() { return zoneName; }
    public ResourceHub getResourceHub() { return hub; }

    public void alertEmergencyServices() {
        System.out.println("[!] Triggering emergency alerts in zone: " + zoneName);
        for (EmergencyService es : hub.getEmergencyServices()) {
            System.out.println("  - " + es.sendEmergencyAlert());
        }
    }
}