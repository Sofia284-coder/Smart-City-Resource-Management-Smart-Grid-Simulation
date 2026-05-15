package main.java.com.smartcity.models;

class HouseholdConsumer extends Consumer {
    private int familySize;

    public HouseholdConsumer(String consumerID, double consumptionRate, int familySize) {
        super(consumerID, "Household", consumptionRate);
        this.familySize = familySize;
    }

    public void setFamilySize(int familySize) {
        this.familySize = familySize;
    }

    public int getFamilySize() {
        return familySize;
    }

    public String toString() {
        return "Household ID: " + consumerID + ", Consumption: " + consumptionRate + " kWh, Family Size: " + familySize;
    }
}
