package performance.monitoring.functionality;

import performance.monitoring.model.DBProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataSourceFactory {

    public DBProperties getDataSource() {

        Properties properties = new Properties();
        DBProperties dbProperties = new DBProperties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("application.properties");

        try {
            properties.load(inputStream);
            dbProperties.setDbDriver(properties.getProperty("POSTGRES_DB_DRIVER_CLASS"));
            dbProperties.setPassword(properties.getProperty("POSTGRES_DB_PASSWORD"));
            dbProperties.setUrl(properties.getProperty("POSTGRES_DB_URL"));
            dbProperties.setUser(properties.getProperty("POSTGRES_DB_USERNAME"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dbProperties;

    }
}
