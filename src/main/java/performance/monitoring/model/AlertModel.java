package performance.monitoring.model;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AlertModel {
    //We want to define the alert for drive data.
    //should contain all the drive info, and who it will be alerting to
    private String alert_id;
    private String date;
    private String application;
    private String server_name;
    private String issue;
    private String status;
    private String expiryDate;


    public AlertModel() {
        this.alert_id = UUID.randomUUID().toString();
        this.date = LocalDateTime.now().toString();
    }

    public String getExpiryDate() {
        return expiryDate;
    }



    public void setExpiryDate(int hours){

       this.expiryDate= LocalDateTime.now().plus(hours, ChronoUnit.HOURS).toString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAlert_id() {
        return alert_id;
    }

    public void setAlert_id(String alert_id) {
        this.alert_id = alert_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getServer_name() {
        return server_name;
    }

    public void setServer_name(String server_name) {
        this.server_name = server_name;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }




    public void timeTricks() throws InterruptedException {
        String currentDate = "2019-03-31T19:43:17.335273400";
        String[]time = currentDate.split("T");
        LocalTime time1 = LocalTime.parse(time[1]);
        LocalTime registeredTime = LocalTime.parse("10:50:11.676235900");
        LocalTime expiryTime = LocalTime.parse("11:50:11.676235900");


        Duration duration =Duration.between(registeredTime,expiryTime);
        long period = duration.getSeconds()/60;
        while (registeredTime.isBefore(expiryTime)) {
            System.out.println("Current Time: " + registeredTime + " | " + "Expiry Time: " + expiryTime);
            registeredTime = LocalTime.now();
            Thread.sleep(5000);
        }
        System.out.println("Duration: "+period);

    }


}
