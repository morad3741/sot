package sot.core.keepalive;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import sot.core.entities.Device;

import java.util.concurrent.Callable;

/**
 * Created by LD on 19/08/2017.
 */
public class KeepAliveCallable implements Callable<Boolean> {

    final static Logger logger = Logger.getLogger(KeepAliveCallable.class);

    @Autowired
    private HostAvailabilityCheck hostAvailabilityCheck;

    private Device device;

    public KeepAliveCallable(Device device) {
        this.device = device;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            hostAvailabilityCheck.hostAvailabilityCheck(device.getIpAddress(),1111);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
