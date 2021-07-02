package performance.monitoring.model;

public class ApplicationRecordModel {
    private String app_record_id;
    private String app_ip;
    private String owner_id;

    public String getApp_record_id() {
        return app_record_id;
    }

    public void setApp_record_id(String app_record_id) {
        this.app_record_id = app_record_id;
    }

    public String getApp_ip() {
        return app_ip;
    }

    public void setApp_ip(String app_ip) {
        this.app_ip = app_ip;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }
}
