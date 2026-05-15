package main.java.com.smartcity.models;
import java.io.Serializable;

abstract class Consumer implements Serializable {

    protected String consumerID;
    protected String type;
    protected double consumptionRate;

    public Consumer(String consumerID, String type, double consumptionRate) {
        this.consumerID = consumerID;
        this.type = type;
        this.consumptionRate = consumptionRate;
    }

    public double getConsumptionRate() {
        return consumptionRate;
    }

    public String getConsumerID() {
        return consumerID;
    }

    public String getType() {
        return type;
    }

    public void setConsumerID(String consumerID) {
        this.consumerID = consumerID;
    }

    public void setConsumptionRate(double consumptionRate) {
        this.consumptionRate = consumptionRate;
    }

    public void setType(String type) {
        this.type = type;
    }
}

