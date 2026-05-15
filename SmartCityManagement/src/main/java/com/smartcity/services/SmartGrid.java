import java.io.*;
import java.util.*;

import main.java.com.smartcity.models.*;



class SmartGrid implements Serializable {
    private ArrayList<PowerStation> powerStations;
    private ArrayList<Consumer> consumers;
    private double totalEnergyConsumed = 0;
    private double totalEnergyProduced = 0;

    public SmartGrid() {
        this.powerStations = new ArrayList<>();
        this.consumers = new ArrayList<>();
    }

    public void addPowerStation(PowerStation p) {
        if (!powerStations.contains(p)) {
            powerStations.add(p);
            totalEnergyProduced += p.getProductionRate();
            System.out.println("Added PowerStation: " + p.getResourceID());
        } else {
            System.out.println("PowerStation " + p.getResourceID() + " already exists in the grid.");
        }
    }

   public boolean removePowerStation(String resourceID) {
    for (PowerStation p : powerStations) {
        if (p.getResourceID().equals(resourceID)) {
            totalEnergyProduced -= p.getProductionRate(); // Subtract its production rate
            powerStations.remove(p); // Remove from the list
            System.out.println("Removed PowerStation: " + resourceID);
            return true;
        }
    }

    System.out.println("PowerStation " + resourceID + " not found.");
    return false;
}


    public void addConsumer(Consumer consumer) {
        if (!consumers.contains(consumer)) {
            consumers.add(consumer);
            totalEnergyConsumed += consumer.getConsumptionRate();
            System.out.println("Added Consumer: " + consumer.getConsumerID());
        } else {
            System.out.println("Consumer " + consumer.getConsumerID() + " already exists in the grid.");
        }
    }

   public boolean removeConsumer(String consumerID) {
    for (Consumer c : consumers) {
        if (c.getConsumerID().equals(consumerID)) {
            totalEnergyConsumed -= c.getConsumptionRate();
            consumers.remove(c); 
            System.out.println("Removed Consumer: " + consumerID);
            return true;
        }
    }

    System.out.println("Consumer " + consumerID + " not found.");
    return false;
}


    public PowerStation getPowerStation(String resourceID) {
        for (PowerStation p : powerStations) {
            if (p.getResourceID().equals(resourceID)) {
                return p;
            }
        }
        return null;
    }

    public Consumer getConsumer(String consumerID) {
        for (Consumer c : consumers) {
            if (c.getConsumerID().equals(consumerID)) {
                return c;
            }
        }
        return null;
    }

    
    public double getTotalEnergyConsumed() {
        return totalEnergyConsumed;
    }

    public double getTotalEnergyProduced() {
        return totalEnergyProduced;
    }

    public double getCurrentNetEnergy() {
        return totalEnergyProduced - totalEnergyConsumed;
    }

    public void balanceGrid() {

    double netEnergy = getCurrentNetEnergy();
    System.out.println("Balancing grid. Current Net Energy: " + netEnergy + " kWh.");

    
    recalculateTotalEnergy(); 
    netEnergy = getCurrentNetEnergy();

    if (netEnergy > 0) {
        System.out.println("Grid surplus. Considering reducing power station output or storing energy.");
        if (!powerStations.isEmpty()) {
            double reductionPerStation = netEnergy / powerStations.size();
            for (PowerStation ps : powerStations) {
                double newProductionRate = Math.max(0, ps.getProductionRate() - reductionPerStation);
                System.out.println("  - PowerStation " + ps.getResourceID() + " reduces output from " + ps.getProductionRate() + " to " + newProductionRate + " kWh.");
                ps.setProductionRate(newProductionRate); 
            }
        }
    } else if (netEnergy < 0) {
        System.out.println("Grid deficit. Requesting more production or initiating demand response.");
        if (!powerStations.isEmpty()) {
            double increasePerStation = Math.abs(netEnergy) / powerStations.size();
            for (PowerStation ps : powerStations) {
                double newProductionRate = ps.getProductionRate() + increasePerStation;
                System.out.println("  - PowerStation " + ps.getResourceID() + " increases output from " + ps.getProductionRate() + " to " + newProductionRate + " kWh.");
                ps.setProductionRate(newProductionRate); 
            }
        }
    } else {
        System.out.println("Grid is currently balanced.");
    }
    recalculateTotalEnergy(); 
}


private void recalculateTotalEnergy() {
    this.totalEnergyProduced = 0;
    for (PowerStation ps : powerStations) {
        this.totalEnergyProduced += ps.getProductionRate();
    }

    this.totalEnergyConsumed = 0;
    for (Consumer c : consumers) {
        this.totalEnergyConsumed += c.getConsumptionRate();
    }
}

    public ArrayList<PowerStation> getPowerStations() {
        return powerStations;
    }

    public ArrayList<Consumer> getConsumers() {
        return consumers;
    }
}

