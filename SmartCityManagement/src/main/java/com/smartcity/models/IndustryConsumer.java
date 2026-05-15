package main.java.com.smartcity.models;

class IndustryConsumer extends Consumer {
    private String industryType;

    public IndustryConsumer(String consumerID, double consumptionRate, String industryType) {
        super(consumerID, "Industry", consumptionRate);
        this.industryType = industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getIndustryType() {
        return industryType;
    }

  
    public String toString() {
        return "Industry ID: " + consumerID + ", Type: " + industryType + ", Consumption: " + consumptionRate + " kWh";
    }
}
