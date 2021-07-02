package performance.monitoring.model;

public class DBProperties {

    private String user;
    private String url;
    private String password;
    private String dbDriver;

    public DBProperties() {

    }

    public DBProperties(String user, String url, String password, String dbDriver) {
        this.user = user;
        this.url = url;
        this.password = password;
        this.dbDriver = dbDriver;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }
}
