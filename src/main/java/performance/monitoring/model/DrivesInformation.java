package performance.monitoring.model;

import java.sql.Timestamp;
import java.util.UUID;

public class DrivesInformation {

    private String id;
    private String businessUnit;
    private String time_date;
    private String serverName;
    private String displayName;
    private String driveUsage;
    private String application;
    private String ip;

    public DrivesInformation() {
        this.id = UUID.randomUUID().toString();
        this.time_date = new Timestamp(System.currentTimeMillis()).toString();
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

    public String getTime_date() {
        return time_date;
    }

    public void setTime_date(String time_date) {
        this.time_date = time_date;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDriveUsage() {
        return driveUsage;
    }

    public void setDriveUsage(String driveUsage) {
        this.driveUsage = driveUsage;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    @Override
    public String toString() {
        return "\n\nDrivesInformation" +
                "\nId \t\t" + id +
                " \nBusiness Unit \t\t" + businessUnit +
                " \nTime \t\t" + time_date +
                " \nServer name \t\t" + serverName +
                " \nDrive Name \t\t" + displayName +
                " \nDrive Usage \t\t" + driveUsage +
                " \nApplication \t\t" + application +
                " \nIp \t\t" + ip;
    }
}
