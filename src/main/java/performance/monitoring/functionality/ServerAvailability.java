package performance.monitoring.functionality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerAvailability {

    public void ping(String host){
        System.out.println("Pining server!........");

        String pingcmd = "ping "+host;
        Runtime runtime = Runtime.getRuntime();
        Process process;

        try{
            process = runtime.exec(pingcmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String input;
            while ((input = in.readLine()) != null){
                System.out.println(input);
            }
        }catch (Exception e){
            Logger.getLogger(ServerAvailability.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public boolean isReachable(String host) throws IOException {
        String ipAddress = host;
        InetAddress inet = InetAddress.getByName(ipAddress);
        boolean reachable = inet.isReachable(5000);

        return reachable;
    }
}
