package performance.monitoring.functionality;



import performance.monitoring.model.DrivesInformation;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DriveFunctionality {

    private ServerAvailability server = new ServerAvailability();

    public ArrayList<DrivesInformation>  getLinuxDrives() throws IOException {
        System.out.println("Calculating Drive Usage : LINUX.");
        ArrayList<DrivesInformation> drivesInformationList = new ArrayList<>();
        File root = new File("/");
        if (server.isReachable("localhost")) {
            try{
                double totalDisk = 0;
                double usedDisk = 0;
                System.out.println("Drive: " + "/");

                DrivesInformation drivesInformation = new DrivesInformation();
                System.out.println("Display name: " + root.getName());
                drivesInformation.setDisplayName(root.getName());
                totalDisk = Math.round(root.getTotalSpace() / 1073741824.0);
                usedDisk = Math.round(root.getFreeSpace() / 1073741824.0);
                System.out.println("Free space: " + usedDisk+" GB");
                String driveUsedSpace = String.valueOf(100 - Math.round((usedDisk / totalDisk) * 100));
                drivesInformation.setDriveUsage(driveUsedSpace);
                drivesInformationList.add(drivesInformation);

            }catch (Exception  e){
                e.printStackTrace();
            }

        }else{
            throw new IOException("Server Is Down");
        }
        return drivesInformationList;
    }


    public ArrayList<DrivesInformation>  getDrives() throws IOException {

        System.out.println("File system roots returned by FileSystemView.getFileSystemView():");
        FileSystemView fsv = FileSystemView.getFileSystemView();
        ArrayList<DrivesInformation> drivesInformationList = new ArrayList<>();
        File[] roots = fsv.getRoots();

        if (server.isReachable("localhost")) {
            System.out.println("SERVER REACHED");
            try {

                for (int i = 0; i < roots.length; i++) {
                    System.out.println("Root: " + roots[i]);
                }

                System.out.println("Home directory: " + fsv.getHomeDirectory());

                System.out.println("File system roots returned by File.listRoots():");
                File[] drives = File.listRoots();
                double totalDisk = 0;
                double usedDisk = 0;
                for (int i = 0; i < drives.length; i++) {
                    System.out.println("Drive: " + drives[i]);
                    //drives.setDrive(f[i].toString());
                    DrivesInformation drivesInformation = new DrivesInformation();
                    System.out.println("Display name: " + fsv.getSystemDisplayName(drives[i]));
                    drivesInformation.setDisplayName(fsv.getSystemDisplayName(drives[i]));
                    totalDisk = Math.round(drives[i].getTotalSpace() / 1073741824.0);
                    usedDisk = Math.round(drives[i].getFreeSpace() / 1073741824.0);


                    System.out.println("Free space: " + usedDisk+" GB");

                    //drives.setUsableSpace(usedDisk);

                    //store.getTotalSpace() - store.getUnallocatedSpace()

                    String driveUsedSpace = String.valueOf(100 - Math.round((usedDisk / totalDisk) * 100));
                    //drivesInformation.setDriveUsage(String.valueOf(Math.round((usedDisk / totalDisk) * 100)) + "%");
                    drivesInformation.setDriveUsage(driveUsedSpace);

                    drivesInformationList.add(drivesInformation);



                  // long insertDrive = db.insertDrive(drives);
                   // System.out.println(insertDrive);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            throw new IOException("Server Is Down");
        }
        return drivesInformationList;
    }
}
