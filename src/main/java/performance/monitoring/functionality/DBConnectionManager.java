package performance.monitoring.functionality;

import performance.monitoring.model.*;
import performance.monitoring.structures.AlertTypes;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBConnectionManager {

    private static Connection connection;

    public static Connection getConnection() {

        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        DBProperties dbProperties = dataSourceFactory.getDataSource();

        try {
            Class.forName(dbProperties.getDbDriver());
            try {
                connection = DriverManager.getConnection(dbProperties.getUrl(), dbProperties.getUser(), dbProperties.getPassword());
            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection.");
                ex.printStackTrace();
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found.");
            ex.printStackTrace();
        }
        return connection;
    }

    public long insertDrive(List<DrivesInformation> drivesInformation, Connection connection) throws SQLException {
        long inserted =0;
        for(int drive = 0; drive<drivesInformation.size(); drive++) {

            checkDriveHealth(drivesInformation.get(drive), connection);
            PreparedStatement statement = null;

            if (hasDriveRecord(drivesInformation.get(drive), connection)) {
                 updateDriveRecord(drivesInformation.get(drive), connection);
            } else {
                String sqlInsert = "INSERT INTO drive_information(id,application,business_unit," +
                        "display_name,drive_usage,server_name,time_date,ip\n" +
                        ") VALUES(?,?,?,?,?,?,?,?)";
                statement = connection.prepareStatement(sqlInsert);

                statement.setString(1, drivesInformation.get(drive).getId());
                statement.setString(2, drivesInformation.get(drive).getApplication());
                statement.setString(3, drivesInformation.get(drive).getBusinessUnit());
                statement.setString(4, drivesInformation.get(drive).getDisplayName());
                statement.setString(5, drivesInformation.get(drive).getDriveUsage());
                statement.setString(6, drivesInformation.get(drive).getServerName());
                statement.setString(7, drivesInformation.get(drive).getTime_date());
                statement.setString(8, drivesInformation.get(drive).getIp());

                //PreparedStatement st = connection.prepareStatement("DELETE FROM Table WHERE name = " + name + ";");

                inserted   = statement.executeUpdate();


            }

        }
        return inserted;
    }


    public long insertProcessorInformation(ProcessorInformation processorInformation, Connection connection) throws SQLException {

        checkCpuHealth(processorInformation, connection);
        checkMemoryHealth(processorInformation, connection);
        if (hasProcessorRecord(processorInformation, connection)) {
            return updateRecord(processorInformation, connection);
        } else {
            String sql = "INSERT INTO processor_information(id,business_unit,cpu,ip,memory" +
                    ",server_availability,server_description,server_name,time_date\n" +
                    ",application) VALUES(?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, processorInformation.getId());
            statement.setString(2, processorInformation.getBusinessUnit());
            statement.setString(3, processorInformation.getCpu());
            statement.setString(4, processorInformation.getIp());
            statement.setString(5, processorInformation.getMemory());
            statement.setString(6, processorInformation.getServerAvailability());
            statement.setString(7, processorInformation.getServerDescription());
            statement.setString(8, processorInformation.getServerName());
            statement.setString(9, processorInformation.getTimeDate());
            statement.setString(10, processorInformation.getApplication());

            long inserted = statement.executeUpdate();

            return inserted;
        }
    }

    public long updateAlert(AlertModel alertModel, Connection connection) throws SQLException {
        String sql = "UPDATE public.alerts\n" +
                "\tSET   application=?, server_name=?, issue=?, status=?, expiry_date=?\n" +
                "\tWHERE issue = '" + alertModel.getIssue() + "' AND server_name = '" + alertModel.getServer_name() + "'";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, alertModel.getApplication());
        statement.setString(2, alertModel.getServer_name());
        statement.setString(3, alertModel.getIssue());
        statement.setString(4, alertModel.getStatus());
        statement.setString(5, alertModel.getExpiryDate().toString());

        return statement.executeUpdate();

    }

    public long updateRecord(ProcessorInformation processorInformation, Connection connection) throws SQLException {
        String sql = "UPDATE processor_information\n" +
                "\tSET   business_unit=?, cpu=?, ip=?, memory=?, server_availability=?, server_description=?, server_name=?, time_date=?, application=?\n" +
                "\tWHERE server_name = '" + processorInformation.getServerName() + "'";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, processorInformation.getBusinessUnit());
        statement.setString(2, processorInformation.getCpu());
        statement.setString(3, processorInformation.getIp());
        statement.setString(4, processorInformation.getMemory());
        statement.setString(5, processorInformation.getServerAvailability());
        statement.setString(6, processorInformation.getServerDescription());
        statement.setString(7, processorInformation.getServerName());
        statement.setString(8, processorInformation.getTimeDate());
        statement.setString(9, processorInformation.getApplication());
        return statement.executeUpdate();
    }

    public long updateDriveRecord(DrivesInformation drivesInformation, Connection connection) throws SQLException {
        String sql = "UPDATE public.drive_information\n" +
                "\tSET application=?, business_unit=?, display_name=?, drive_usage=?, ip=?, server_name=?, time_date=?\n" +
                "\tWHERE server_name = '"+drivesInformation.getServerName()+"' AND display_name = '"+drivesInformation.getDisplayName()+"'" ;
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, drivesInformation.getApplication());
        statement.setString(2, drivesInformation.getBusinessUnit());
        statement.setString(3, drivesInformation.getDisplayName());
        statement.setString(4, drivesInformation.getDriveUsage());
        statement.setString(5, drivesInformation.getIp());
        statement.setString(6, drivesInformation.getServerName());
        statement.setString(7, drivesInformation.getTime_date());
        return statement.executeUpdate();
    }

    //Slight change to register a default alert configuration when a server is registered
    public long insertServerInformation(ServerInformation serverInformation, AlertConfigModel alertConfigModel, Connection connection, ApplicationRecordModel applicationRecordModel) throws Exception {

        Statement statement1 = connection.createStatement();
        ResultSet resultSet = statement1.executeQuery("Select * From server_information where machine_ip = " + '\'' + serverInformation.getMachineIp() + '\'');

        if (!resultSet.next()) {
            String sql = "INSERT INTO server_information(id,application,business_unit,machine_category,machine_ip" +
                    ",machine_name,server_availability) VALUES(?,?,?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, serverInformation.getId());
            statement.setString(2, serverInformation.getApplication());
            statement.setString(3, serverInformation.getBusinessUnit());
            statement.setString(4, serverInformation.getMachineCategory());
            statement.setString(5, serverInformation.getMachineIp());
            statement.setString(6, serverInformation.getMachineName());
            statement.setString(7, serverInformation.getServerAvailability());


            long inserted = statement.executeUpdate();

            insertAlertConfig(alertConfigModel, connection);
            insertApplicationMailRecord(applicationRecordModel, connection);
            return inserted;
        } else {
            System.out.println("IP/ Server: " + serverInformation.getMachineIp() + " has already been registered ");
            return 0;
        }
    }

    public long insertApplicationMailRecord(ApplicationRecordModel applicationRecord, Connection connection) throws Exception {

        Statement statement1 = connection.createStatement();


        String sql = "INSERT INTO public.application_record(\n" +
                "\tapp_record_id, app_id, owner_id)\n" +
                "\tVALUES (?, ?, ?);";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, applicationRecord.getApp_record_id());
        statement.setString(2, applicationRecord.getApp_ip());
        statement.setString(3, applicationRecord.getOwner_id());


        long inserted = statement.executeUpdate();

        System.out.println("Application Record for mail sending inserted");
        return inserted;
    }

    public long insertAlertConfig(AlertConfigModel alertConfigModel, Connection connection) throws SQLException {
        ArrayList<String> types = new ArrayList<>();
        types.add(AlertTypes.CPU.getType());
        types.add(AlertTypes.DISK.getType());
        types.add(AlertTypes.MEMORY.getType());
        long inserted = 0;
        Statement statement1 = connection.createStatement();


        String sql = "INSERT INTO public.alert_config(\n" +
                "\tid, alert_type, application, threshold)\n" +
                "\tVALUES (?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < types.size(); i++) {

            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, types.get(i));
            statement.setString(3, alertConfigModel.getApplication());
            statement.setString(4, alertConfigModel.getThreshold());


            inserted = statement.executeUpdate();
        }
        System.out.println("Config Inserted");
        return inserted;

    }

    public long deleteRecord(String sqlDelete, Connection connection) throws SQLException {

        Statement statement = connection.createStatement();

        return statement.executeUpdate(sqlDelete);
    }

    public ResultSet queryInformation(Connection connection, String sql) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    public boolean hasProcessorRecord(ProcessorInformation information, Connection connection) throws SQLException {

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM processor_information WHERE server_name = '" + information.getServerName() + "'");
        return resultSet.next();

    }

    public boolean hasDriveRecord(DrivesInformation information, Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM drive_information WHERE server_name = '" + information.getServerName() + "' AND display_name = '"+information.getDisplayName()+"'");
        return resultSet.next();
    }


    public void checkDriveHealth(DrivesInformation information, Connection connection) throws SQLException {
        AlertModel alerts = new AlertModel();
        String server_name = information.getServerName();
        ResultSet serverInfoResultSet = connection.createStatement().executeQuery("SELECT * FROM server_information WHERE machine_name = '" + server_name + "'");

        while (serverInfoResultSet.next()) {
            String applicationID = serverInfoResultSet.getString("application");
            ResultSet alertConfigResultSet = connection.createStatement().executeQuery("SELECT * FROM alert_config WHERE application = '" + applicationID + "' AND " +
                    "alert_type= 'disk' ");
            while (alertConfigResultSet.next()) {

                String threshold = alertConfigResultSet.getString("threshold");

                double application_threshold = Double.valueOf(threshold);
                double application_server_drive_usage = Double.valueOf(information.getDriveUsage());

                ResultSet alertsRS = connection.createStatement().executeQuery("SELECT * FROM alerts WHERE issue like '%space%' AND server_name = '" + information.getServerName() + "'");

                if (application_server_drive_usage >= application_threshold) {
                    alerts.setApplication(information.getApplication());
                    alerts.setIssue("Hard disk: " + information.getDisplayName() + " on server: " + information.getServerName() + ": IP: " + information.getIp() + " is running dangerously low on space");
                    alerts.setServer_name(information.getServerName());
                    alerts.setStatus("Pending");
                    //Expiry to be set to an hour
                    alerts.setExpiryDate(1);

//                    if (alertsRS.next())
//                        updateAlert(alerts, connection);
//                    else

                    insertAlert(alerts, connection);
                }

            }
        }

    }

    public void checkCpuHealth(ProcessorInformation information, Connection connection) throws SQLException {
        AlertModel alerts = new AlertModel();
        String server_name = information.getServerName();
        ResultSet serverInfoResultSet = connection.createStatement().executeQuery("SELECT * FROM server_information WHERE machine_name = '" + server_name + "'");
        while (serverInfoResultSet.next()) {
            String applicationID = serverInfoResultSet.getString("application");
            ResultSet alertConfigResultSet = connection.createStatement().executeQuery("SELECT * FROM alert_config WHERE application = '" + applicationID + "' AND " +
                    "alert_type= 'cpu' ");
            while (alertConfigResultSet.next()) {
                String threshold = alertConfigResultSet.getString("threshold");

                double application_threshold = Double.valueOf(threshold);
                double application_server_usage = Double.valueOf(information.getCpu());

                ResultSet alertsRS = connection.createStatement().executeQuery("SELECT * FROM alerts WHERE issue like '%CPU%' AND server_name = '" + information.getServerName() + "'");
                if (application_server_usage >= application_threshold) {
                    alerts.setApplication(information.getApplication());
                    alerts.setIssue("CPU Usage on: " + information.getServerName() + ", IP: " + information.getIp() + " is dangerously high");
                    alerts.setServer_name(information.getServerName());
                    alerts.setStatus("Pending");
                    alerts.setExpiryDate(1);
//                    if (alertsRS.next())
//                        updateAlert(alerts, connection);
//                    else
                    insertAlert(alerts, connection);

                }

            }
        }
    }

    public void checkMemoryHealth(ProcessorInformation information, Connection connection) throws SQLException {
        AlertModel alerts = new AlertModel();
        String server_name = information.getServerName();
        ResultSet serverInfoResultSet = connection.createStatement().executeQuery("SELECT * FROM server_information WHERE machine_name = '" + server_name + "'");

        while (serverInfoResultSet.next()) {
            String applicationID = serverInfoResultSet.getString("application");
            ResultSet alertConfigResultSet = connection.createStatement().executeQuery("SELECT * FROM alert_config WHERE application = '" + applicationID + "' AND " +
                    "alert_type= 'memory' ");
            while (alertConfigResultSet.next()) {
                String threshold = alertConfigResultSet.getString("threshold");

                double application_threshold = Double.valueOf(threshold);
                double application_server_drive_usage = Double.valueOf(information.getMemory());

                ResultSet alertsRS = connection.createStatement().executeQuery("SELECT * FROM alerts WHERE issue like '%Memory%' AND server_name = '" + information.getServerName() + "'");

                if (application_server_drive_usage >= application_threshold) {
                    alerts.setApplication(information.getApplication());
                    alerts.setIssue("Memory Usage on: " + information.getServerName() + ", IP: " + information.getIp() + " is dangerously high");
                    alerts.setServer_name(information.getServerName());
                    alerts.setStatus("Pending");
                    alerts.setExpiryDate(1);
//                    if (alertsRS.next())
//                        updateAlert(alerts, connection);
//                    else
                    insertAlert(alerts, connection);
                }

            }
        }

    }

    public PreparedStatement helperInsertAlert(AlertModel alert, Connection connection) throws SQLException {
        String sql = "INSERT INTO public.alerts(\n" +
                "\talert_id, application, date, expiry_date, issue, server_name, status)\n" +
                "\tVALUES (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, alert.getAlert_id());
        statement.setString(2, alert.getApplication());
        statement.setString(3, alert.getDate());
        statement.setString(4, alert.getExpiryDate());
        statement.setString(5, alert.getIssue());
        statement.setString(6, alert.getServer_name());
        statement.setString(7, alert.getStatus());

        System.out.println("New Alert Inserted");
        return statement;
    }

    public long insertAlert(AlertModel alert, Connection connection) throws SQLException {
        ResultSet alertsRS = connection.createStatement().executeQuery("SELECT * FROM alerts WHERE issue = '" + alert.getIssue() + "' AND application  = '" + alert.getApplication() + "'");
        //If theres no alert, put an alert, if there is an alert, check if it has expired.
        if (alertsRS.next()) {
            //if there is an alert, check if it has expired
            if (hasAlertExpired(alert, connection)) {
                //if alert has expired, insert new alert
                return helperInsertAlert(alert, connection).executeUpdate();
            }
        } else {
            //This should only ever excecute once
            System.out.println("First Alert Insert ");
            return helperInsertAlert(alert, connection).executeUpdate();

        }
        return 0;
    }

    public boolean hasAlertExpired(AlertModel alert, Connection connection) throws SQLException {
        ResultSet alertsRS = connection.createStatement().executeQuery("SELECT * FROM alerts WHERE application  = '" + alert.getApplication() + "'");
        while (alertsRS.next()) {
            String expiry_date = alertsRS.getString("expiry_date");
            String[] time = expiry_date.split("T");
            if (LocalTime.now().isBefore(LocalTime.parse(time[1]))) {
                return false;
            }
        }
        return true;
    }


}