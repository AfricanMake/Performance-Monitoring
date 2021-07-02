package performance.monitoring.functionality;


import com.sun.management.OperatingSystemMXBean;
import performance.monitoring.model.ProcessorInformation;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public class ProcessorFunctionality {

    private ProcessorInformation processorInformation = new ProcessorInformation();
    private ServerAvailability server = new ServerAvailability();

    public ProcessorInformation getProcessorData() throws IOException {

        Utils data = new Utils();
        Runtime.getRuntime().availableProcessors();
        if (server.isReachable("localhost")) {
            System.out.println("SERVER REACHED");
            try {
                double usedCPU = 0;
                OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
                usedCPU = Math.round((osBean.getSystemCpuLoad() * 100));
                System.out.println("Used CPU: " + usedCPU);
                System.out.println("CPU LOAD " + data.getProcessCpuLoad());

                processorInformation.setCpu(Double.toString(usedCPU));
                System.out.println("Memory " + Utils.getCPUMemoryLoad());
                processorInformation.setMemory(Utils.getCPUMemoryLoad());

                double used_memory = Math.round((osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize()) / 1073741824.0);

                // processor.setMemory_used((used_memory));
                //processorRepository.save(processorModel); -->> persisting to the DB

                DBConnectionManager db = new DBConnectionManager();
                //long insertDrive = db.insertProcess(processor);
                //System.out.println(insertDrive);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            throw new IOException("Server Is Down");
        }

        return  processorInformation;
    }
}
