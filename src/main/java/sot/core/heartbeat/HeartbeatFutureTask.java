package sot.core.heartbeat;

import org.apache.log4j.Logger;
import sot.core.entities.Device;

import java.util.concurrent.Callable;

/**
 * Created by LD on 19/08/2017.
 */
public class HeartbeatFutureTask implements Callable<Boolean> {

    final static Logger logger = Logger.getLogger(HeartbeatFutureTask.class);

    private Device device;
    private int targetPort;

    public HeartbeatFutureTask(Device device, int targetPort) {
        this.device = device;
        this.targetPort = targetPort;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            Boolean result =  HeartbeatAvailabilityCheck.hostAvailabilityCheck(device.getIpAddress(),targetPort);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
