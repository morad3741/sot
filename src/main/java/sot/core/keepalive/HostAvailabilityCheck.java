package sot.core.keepalive;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by LD on 07/10/2017.
 */
@Component
public class HostAvailabilityCheck {

    private final static Logger logger = Logger.getLogger(HostAvailabilityCheck.class);

    public static boolean hostAvailabilityCheck(String hostIpAddress,int HostPort) {
        try (Socket s = new Socket(InetAddress.getByName(hostIpAddress), HostPort)) {
            return true;
        } catch (IOException ex) {
            logger.error(ex);
            ex.printStackTrace();
        }
        return false;
    }

}
