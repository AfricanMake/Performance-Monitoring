package performance.monitoring.model;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.time.LocalDate;


public class AuditModel {

    private String serverName;
    private String ip;
    private Date date;

    public AuditModel() {
        this.date = Date.valueOf(LocalDate.now());
        try {
            InetAddress address;
            address = InetAddress.getLocalHost();
            serverName =address.getHostName();
            ip = address.getHostAddress();

        }catch (UnknownHostException e){
            e.printStackTrace();
        }
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
