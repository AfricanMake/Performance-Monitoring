package performance.monitoring.model;


import java.util.UUID;

public class ServerInformation {

    private String id;
    private String application;
    private String businessUnit;
    private String machineCategory;
    private String machineIp;
    private String machineName;
    private String serverAvailability;

    public ServerInformation() {
        this.id = UUID.randomUUID().toString();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
    }

    public String getMachineCategory() {
        return machineCategory;
    }

    public void setMachineCategory(String machineCategory) {
        this.machineCategory = machineCategory;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getServerAvailability() {
        return serverAvailability;
    }

    public void setServerAvailability(String serverAvailability) {
        this.serverAvailability = serverAvailability;
    }

    @Override
    public String toString() {
        return "ServerInformation{" +
                "id='" + id + '\'' +
                "\napplication='" + application + '\'' +
                "\n businessUnit='" + businessUnit + '\'' +
                "\n machineCategory='" + machineCategory + '\'' +
                "\n machineIp='" + machineIp + '\'' +
                "\n machineName='" + machineName + '\'' +
                "\n serverAvailability='" + serverAvailability + '\'' +
                '}';
    }
}
