package performance.monitoring.functionality;

import com.sun.management.OperatingSystemMXBean;
import org.apache.commons.io.FileUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.management.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Utils {

    public Utils() {

    }

    public void moveSourceToDestinationFolder(String sourceFolder, String targetFolder) {
        try {
            File indexSource = new File(sourceFolder); //"/home/Work/Indexer1"
            File indexTarget = new File(targetFolder);
            String[] entries = indexSource.list();
            for (String s : entries) {
                File currentFile = new File(indexSource.getPath(), s);
                FileUtils.copyDirectory(indexSource, indexTarget);
                FileUtils.cleanDirectory(indexSource);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFilesOlderThanNdays(int daysBack, String dirWay) {

        File directory = new File(dirWay);
        if (directory.exists()) {

            File[] listFiles = directory.listFiles();
            long purgeTime = System.currentTimeMillis() - (daysBack * 24 * 60 * 60 * 1000);
            for (File listFile : listFiles) {
                if (listFile.lastModified() < purgeTime) {
                    if (listFile.list().length != 0) {
                        String[] list = listFile.list();
                        for (String s : list) {
                            File currentFile = new File(listFile.getPath(), s);
                            currentFile.delete();
                        }
                    }
                    listFile.delete();
                }
            }
        }
    }

    public static String getCPUMemoryLoad() throws Exception {

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
        long p = osBean.getCommittedVirtualMemorySize();

        double totalmemory = osBean.getTotalPhysicalMemorySize();
        double freemem = osBean.getFreePhysicalMemorySize();
        double usedMemory = totalmemory - freemem;
        double memUsage = usedMemory / totalmemory * 100;

        return String.valueOf((int) (memUsage));

    }

    public static String getProcessCpuLoad() throws Exception {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
        AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

        if (list.isEmpty())
            return String.valueOf(Double.NaN);

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0)
            return String.valueOf(Double.NaN);

        // returns a percentage value with 1 decimal point precision
        //   ExtentReport.testInfo("CPU Load/Usage: " + (int)((value * 1000) / 10) + "%");
        //org.apache.commons.io.FileUtils.byteCountToDisplaySize(
        return String.valueOf(((double) ((value * 1000) / 10)) + "%");
        //    return String.valueOf(((int)((value * 1000) /10)) + "%");
    }

    public Map<String, String> getDrivesandSizes(String serverName) {
        //    ExtentReport.addTest("List System Drives and Sizes.");

        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();
        Map<String, String> drives = new HashMap<>();
        String usageMessage = "";
        int discSpaceUsagePercentage;
        boolean redAlert = false;
        boolean amberAlert = false;
        /* For each filesystem root, print some info */
        for (File root : roots) {
            if (!root.getAbsolutePath().contains("A:") && !root.getAbsolutePath().contains("Z:")) {
                long totalDiscSpace = root.getTotalSpace();
                long totalFreeDiscSpace = root.getFreeSpace();
                long usedDiscSpace = totalDiscSpace - totalFreeDiscSpace;
                discSpaceUsagePercentage = (int) (((double) usedDiscSpace / (double) totalDiscSpace) * 100);
                if (discSpaceUsagePercentage >= 95) {
                    usageMessage = usageMessage + root.getAbsolutePath() + " " + discSpaceUsagePercentage + "%\n";
                    redAlert = true;
                } else if (discSpaceUsagePercentage >= 80 && discSpaceUsagePercentage < 95) {
                    usageMessage = usageMessage + root.getAbsolutePath() + " " + discSpaceUsagePercentage + "%\n";
                    amberAlert = true;
                }
               // ExtentReport.testInfo("File system root: " + root.getAbsolutePath() + " => Total space (bytes): " + org.apache.commons.io.FileUtils.byteCountToDisplaySize(root.getTotalSpace()) + ", Free space (bytes): " + org.apache.commons.io.FileUtils.byteCountToDisplaySize(root.getFreeSpace()));
                drives.put("File system root: " + root.getAbsolutePath(), " => Total space (bytes): " + org.apache.commons.io.FileUtils.byteCountToDisplaySize(root.getTotalSpace()) + ", Free space (bytes): " + org.apache.commons.io.FileUtils.byteCountToDisplaySize(root.getFreeSpace()));
            }
        }
        if (redAlert) checkDriveSizeandSendAlert(serverName, "red", usageMessage);
        else if (amberAlert)
            checkDriveSizeandSendAlert(serverName, "amber", usageMessage);

        return drives;
    }


    public void checkDriveSizeandSendAlert(String serverName, String alertType, String ussageMessage) {
        String host = "smtp.absa.co.za";    //22.149.185.132
        String from = "pule.moselane@absa.co.za";
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", host);
        //  props.setProperty("mail.smtp.port", "64693");

        String toRecipients = "pule.moselane@absa.co.za";
        String[] recipientList = toRecipients.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        Session session = Session.getDefaultInstance(props);
        try {
            MimeMessage message = new MimeMessage(session);        // email message
            message.setFrom(new InternetAddress(from));                    // setting header fields
            int x = 0;
            for (String recipients : recipientList) {
                recipientAddress[x] = new InternetAddress(recipients.trim());
                x++;
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddress);

            //   message.addRecipient(Message.RecipientType.TO, new InternetAddress([toRecipients.length]);

            if (alertType.equalsIgnoreCase("red")) {
                message.setHeader("X-Priority", "1");
                message.setSubject("RED ALERT!!! " + serverName);
            } else if (alertType.equalsIgnoreCase("amber"))
                message.setSubject("AMBER ALERT!!! " + serverName); // subject line

            message.setText("Hi,\n\nYour hard disc utilization is very high, kindly monitor the following drive(s) ASAP: \n" + ussageMessage + " \n\nPlease Note: This email was auto generated, please do not reply as it is not monitored.\n\nKind Regards.");
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public Map<String, String> getDrivesandSizes_old() {
        //ExtentReport.addTest("List System Drives and Sizes.");
        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();
        Map<String, String> drives = new HashMap<>();

        /* For each filesystem root, print some info */
        for (File root : roots) {
            if (!root.getAbsolutePath().contains("A:") && !root.getAbsolutePath().contains("Z:")) {
               // ExtentReport.testInfo("File system root: " + root.getAbsolutePath() + " => Total space: " + org.apache.commons.io.FileUtils.byteCountToDisplaySize(root.getTotalSpace()) + ", Free space:" + org.apache.commons.io.FileUtils.byteCountToDisplaySize(root.getFreeSpace()));
                drives.put(root.getAbsolutePath().substring(0, 1), "Total space:" + org.apache.commons.io.FileUtils.byteCountToDisplaySize(root.getTotalSpace()) + ", Free space:" + org.apache.commons.io.FileUtils.byteCountToDisplaySize(root.getFreeSpace()));
            }
        }
        return drives;
    }

    public Map<String, String> getAllServicesNotRunning() throws Exception {
       // ExtentReport.addTest("List All Services NOT Running.");
        Map<String, String> servicesMap = new HashMap();
        try {
            String line;
            String[] resultsArr;
            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe /v /fo csv"); //csv format
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.contains("sql")) {
                    resultsArr = line.split("\"");
                    servicesMap.put(resultsArr[1], resultsArr[11]);
                    //   System.out.println(line);
                    //  servicesMap.add(line);
                }
            }
            input.close();
            return servicesMap;
        } catch (Exception err) {
            err.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getAllAvailableProcesses() throws Exception {
        //ExtentReport.addTest("List All Services.");
        ArrayList<String> servicesMap = new ArrayList<>();

        try {
            String line;
            String[] resultsArr;
            Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe /v /fo TABLE"); //tabular format
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.contains("sql"))
                    servicesMap.add(line);
            }
            input.close();
            return servicesMap;
        } catch (Exception err) {
            err.printStackTrace();
        }
        return null;
    }

    public void printUsage() {
        java.lang.management.OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get")
                    && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = e;
                } // try
                System.out.println(method.getName() + " = " + (value));
            } // if
        } // for
    }

    public void getCPUUsage() throws IOException {
      //  ExtentReport.addTest("Get CPU Usage 2.");
        MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();

        OperatingSystemMXBean osMBean = ManagementFactory.newPlatformMXBeanProxy(
                mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

        long nanoBefore = System.nanoTime();
        long cpuBefore = osMBean.getProcessCpuTime();

// Call an expensive task, or sleep if you are monitoring a remote process
        long cpuAfter = osMBean.getProcessCpuTime();
        long nanoAfter = System.nanoTime();

        long percent;
        if (nanoAfter > nanoBefore)
            percent = ((cpuAfter - cpuBefore) * 100L) /
                    (nanoAfter - nanoBefore);
        else percent = 0;

        //ExtentReport.testInfo("CPU Usage: " + percent + "%");

    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public String pingIPServer(String ip) {
        // String ip = "127.0.0.1";
        String pingResult = "";

        String pingCmd = "ping " + ip;
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            BufferedReader in = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                //  System.out.println(inputLine);
                pingResult += inputLine;
            }
            in.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return pingResult;
    }

    public boolean checkServerIPReachable(String ip) throws IOException {
       // ExtentReport.addTest("To Test If Server: " + ip + ", is reachable(PING Server).", ip);
        InetAddress inet = InetAddress.getByName(ip);
        return inet.isReachable(5000);
    }

    /*public String createJSON(String CPUUsage, String MemoryUsage, Map drives, Map resultsMap, String hostname) {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        JSONArray array2 = new JSONArray();

        json.put("CPU Usage", CPUUsage);
        json.put("Memory Usage", MemoryUsage);
        array2.put(drives);
        json.put("system services", array);
        array.put(resultsMap);
        json.put("system drives", array2);
        json.put("Server Name", hostname);

        //  json.put("Performance Monitor", array);

        System.out.println(json.get("Performance Monitor"));
        return json.toString();

    }*/

    public boolean moveOldReportFiles(File sourceFile, File destFile) {
        if (sourceFile.isDirectory()) {
            for (File file : sourceFile.listFiles()) {
                moveOldReportFiles(file, new File(file.getPath().substring("temp".length() + 1)));
            }
        } else {
            try {
                Files.move(Paths.get(sourceFile.getPath()), Paths.get(destFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
}






