package performance.monitoring.structures;


public enum AlertTypes {
    CPU("cpu"),
    DISK("disk"),
    MEMORY("memory") ;

    private String type;

    AlertTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
