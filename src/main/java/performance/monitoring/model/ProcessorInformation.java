package performance.monitoring.model;

import java.sql.Timestamp;
import java.util.UUID;

public class ProcessorInformation {

    private String id;
    private String businessUnit;
    private String serverName;
    private String serverDescription;
    private String ip;
    private String timeDate;
    private String memory;
    private String cpu;
    private String serverAvailability;
    private String application;

    public ProcessorInformation() {
        this.id = UUID.randomUUID().toString();
        this.timeDate =  new Timestamp(System.currentTimeMillis()).toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerDescription() {
        return serverDescription;
    }

    public void setServerDescription(String serverDescription) {
        this.serverDescription = serverDescription;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getServerAvailability() {
        return serverAvailability;
    }

    public void setServerAvailability(String serverAvailability) {
        this.serverAvailability = serverAvailability;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return "\n\nProcessorInformation" +
                "\nId \t\t" + id +
                "\nBusiness Unit \t\t" + businessUnit +
                "\nServer name \t\t" + serverName +
                "\nServer description \t\t" + serverDescription +
                "\nIp \t\t" + ip +
                "\nDate \t\t" + timeDate +
                "\nMemory \t\t" + memory +
                "\nCPU \t\t" + cpu +
                "\nServer availability \t\t" + serverAvailability +
                "\nApplication \t\t" + application;

    }
}
