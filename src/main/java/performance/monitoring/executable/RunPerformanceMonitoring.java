package performance.monitoring.executable;


import org.apache.commons.lang3.SystemUtils;
import performance.monitoring.functionality.DBConnectionManager;
import performance.monitoring.functionality.DriveFunctionality;
import performance.monitoring.functionality.ProcessorFunctionality;
import performance.monitoring.model.*;
//import performance.monitoring.structures.AlertTypes;

//import javax.swing.*;
//import java.awt.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
//import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class RunPerformanceMonitoring {

    //static GraphicsConfiguration graphicsConfiguration;
    //static JTextArea jTextArea;
    //private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);



    public ServerInformation setServerInformation(String id, String application, String businessUnit, String machineCategory, String machineIp, String machineName, String serverAvailability) {
        ServerInformation serverInformation = new ServerInformation();
        serverInformation.setId(id);
        serverInformation.setApplication(application);
        serverInformation.setBusinessUnit(businessUnit);
        serverInformation.setMachineCategory(machineCategory);
        serverInformation.setMachineIp(machineIp);
        serverInformation.setMachineName(machineName);
        serverInformation.setServerAvailability(serverAvailability);
        return serverInformation;
    }

    public void registerServer(DBConnectionManager dbManager) {
        try {
            String operatingSystem = System.getProperty("os.name");
            InetAddress metaData;

            if(SystemUtils.IS_OS_LINUX){
                System.out.println("----- IS_OS_LINUX -----");
                metaData = getFirstNonLoopbackAddress(true, false);





            }else {
                System.out.println("----- IS_OS_windows -----");
                metaData = InetAddress.getLocalHost();
            }

            ServerInformation serverInformation = new RunPerformanceMonitoring().setServerInformation(
                    UUID.randomUUID().toString(), "App_Pending" + UUID.randomUUID().toString(), "BU_pending"+UUID.randomUUID().toString(), "Category_Pending" + UUID.randomUUID().toString(),
                    metaData.getHostAddress(),
                    metaData.getHostName(),
                    "Available");

            ApplicationRecordModel applicationRecordModel = new ApplicationRecordModel();
            applicationRecordModel.setApp_record_id(UUID.randomUUID().toString());
            applicationRecordModel.setApp_ip(serverInformation.getApplication());
            applicationRecordModel.setOwner_id("");

            AlertConfigModel alertConfig = new AlertConfigModel();
            alertConfig.setThreshold("70");
            alertConfig.setApplication(serverInformation.getApplication());

            long register = dbManager.insertServerInformation(serverInformation, alertConfig, DBConnectionManager.getConnection(), applicationRecordModel);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static InetAddress getFirstNonLoopbackAddress(boolean preferIpv4, boolean preferIPv6) throws SocketException {
        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (!addr.isLoopbackAddress()) {
                    if (addr instanceof Inet4Address) {
                        if (preferIPv6) {
                            continue;
                        }
                        return addr;
                    }
                    if (addr instanceof Inet6Address) {
                        if (preferIpv4) {
                            continue;
                        }
                        return addr;
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {

        String cmd = "start";
        if (args.length > 0) {
            cmd = args[0];
        }

        if ("stop".equals(cmd)) {
            System.exit(0);
        }


        DBConnectionManager dbManager = new DBConnectionManager();


        new RunPerformanceMonitoring().registerServer(dbManager);
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            public void run() {
                Connection connection = DBConnectionManager.getConnection();

                ProcessorFunctionality processorFunctionality = new ProcessorFunctionality();

                DriveFunctionality driveFunctionality = new DriveFunctionality();
                String ip;
                String serverName = "";
                String application = "";
                String businessUnit = "";
                String machineCategory = "";
                String machineIp = "";
                String machineName = "";
                String serverAvailability = "";

                try {
                    final String id = processorFunctionality.getProcessorData().getId();
                    InetAddress address;

                    address = InetAddress.getLocalHost();
                    serverName = address.getHostName();
                    ip = address.getHostAddress();
                    String sql = "SELECT * FROM server_information WHERE machine_ip = '" + ip + "' OR machine_name ='" + serverName + "'";
                    ResultSet resultSet = dbManager.queryInformation(connection, sql);

                    while (resultSet.next()) {
                        //Retrieve by column name
                        serverAvailability = resultSet.getString("server_availability");
                        application = resultSet.getString("application");
                        businessUnit = resultSet.getString("business_unit");
                        machineCategory = resultSet.getString("machine_category");
                        machineIp = resultSet.getString("machine_ip");
                        machineName = resultSet.getString("machine_name");
                    }

                    ProcessorInformation processorInformation = processorFunctionality.getProcessorData();
                    //DrivesInformation drivesInformation =driveFunctionality.getDrives();

                    ArrayList<DrivesInformation> drivesList;
                    if(SystemUtils.IS_OS_LINUX){
                        System.out.println("----- IS_OS_LINUX -----");
                        drivesList = driveFunctionality.getLinuxDrives();
                    }else {
                        System.out.println("----- IS_OS_windows -----");
                        drivesList = driveFunctionality.getDrives();
                    }




                    ArrayList<DrivesInformation> drives = new ArrayList<>();
                    processorInformation.setServerAvailability(serverAvailability);
                    processorInformation.setApplication(application);
                    processorInformation.setServerName(serverName);
                    processorInformation.setServerDescription(machineCategory);
                    processorInformation.setBusinessUnit(businessUnit);
                    processorInformation.setIp(machineIp);
                    long insertProcessor = dbManager.insertProcessorInformation(processorInformation, connection);

                    for (int i = 0; i<drivesList.size(); i++) {
                        drivesList.get(i).setApplication(application);
                        drivesList.get(i).setBusinessUnit(businessUnit);
                        drivesList.get(i).setServerName(serverName);
                        drivesList.get(i).setIp(machineIp);
                        drives.add(drivesList.get(i));

                    }
                    long insertDrive = dbManager.insertDrive(drives, connection);
                    System.out.println("\nDrive inserted :" + insertDrive);
//                    dbManager.updateRecord(processorInformation, connection);
//                    String sqlDeleteProcessor = "DELETE FROM processor_information WHERE server_name = '" + processorInformation.getServerName() + "';";
                  //  String sqlDelete = "DELETE FROM drive_information WHERE server_name = '" + processorInformation.getServerName() + "';";
//
                    //long deleteRecord = dbManager.deleteRecord(sqlDelete, connection);
//
//                    long deleteRecordProcessor = dbManager.deleteRecord(sqlDeleteProcessor, connection);
//
//                    //checking if the above SQL statements have been executed successfully
//                    System.out.println("Processor inserted :" + insertProcessor);
//
                 //   System.out.println("Rows deleted :" + deleteRecord);

                 /*   for (DrivesInformation drivesInformation : drivesList) {

                        drivesInformation.setApplication(application);
                        drivesInformation.setBusinessUnit(businessUnit);
                        drivesInformation.setServerName(serverName);
                        drivesInformation.setIp(machineIp);
                        long insertDrive = dbManager.insertDrive(drivesInformation, connection);
                        System.out.println("\nDrive inserted :" + insertDrive);
                    }*/

                    //jTextArea.setText(processorInformation.toString() + "\n" + drivesList);

                    connection.close();
                    connection = null;

                } catch (Exception e) {
                    //jTextArea.setText(e.getLocalizedMessage());
                    e.printStackTrace();
                    System.out.println("HELLO EXCEPTION.");
                    try {
                        connection.close();
                        connection = null;
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }

            }

        };

        timer.scheduleAtFixedRate(task, 1000, 5000);
    }

    public static void windowsService(String args[]) throws Exception {
        String cmd = "start";
        if (args.length > 0) {
            cmd = args[0];
        }

        if ("start".equals(cmd)) {
            RunPerformanceMonitoring.main(new String[]{});
        } else {
           // executor.shutdownNow();
            System.exit(0);
        }
    }

}
