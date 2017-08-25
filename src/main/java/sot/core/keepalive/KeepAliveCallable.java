package sot.core.keepalive;

import org.apache.log4j.Logger;
import sot.core.Device;

import java.util.concurrent.Callable;

/**
 * Created by LD on 19/08/2017.
 */
public class KeepAliveCallable implements Callable<Boolean> {

    final static Logger logger = Logger.getLogger(KeepAliveCallable.class);

    private Device device;

    public KeepAliveCallable(Device device) {
        this.device = device;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            Thread.sleep(3 * 1000);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

}
