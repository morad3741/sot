package sot.core.heartbeat;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by LD on 07/10/2017.
 */

public class HeartbeatAvailabilityCheck {

    private final static Logger logger = Logger.getLogger(HeartbeatAvailabilityCheck.class);

    public static boolean hostAvailabilityCheck(String targetIpAddress, int targetPort) {
        try (Socket s = new Socket(InetAddress.getByName(targetIpAddress), targetPort)) {
            return true;
        } catch (IOException ex) {
        }
        return false;
    }

}
