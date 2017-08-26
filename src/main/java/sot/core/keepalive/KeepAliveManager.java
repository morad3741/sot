package sot.core.keepalive;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sot.common.Common;
import sot.core.IHierarchy;
import sot.core.entities.Device;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by LD on 19/08/2017.
 */
@Component
public class KeepAliveManager {

    final static Logger logger = Logger.getLogger(KeepAliveCallable.class);
    @Autowired
    private IHierarchy hierarchy;
    private  ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    protected KeepAliveManager() {
    }

    @Scheduled(fixedDelay=5000)
    protected void initScheduler() {
        Map<Device, Future<Boolean>> keepAliveResultMap = new HashMap<>();

            keepAliveResultMap.clear();
            for (String deviceAddress : hierarchy.getKnownDevicesInMyNetwork().keySet()) {
                // get(i) may not exist anymore but may still be in view of the iterator
                Device device = hierarchy.getKnownDevicesInMyNetwork().getOrDefault(deviceAddress, null);
                if (device == null || device == Common.getMyDevice())
                    continue;
                else {
                    logger.debug("Initiating KeepAlive:" + device.getAddress());
                    Future<Boolean> future = cachedThreadPool.submit(new KeepAliveCallable(device));
                    keepAliveResultMap.put(device, future);
                }
            }
            for (Map.Entry<Device, Future<Boolean>> mapEntry : keepAliveResultMap.entrySet()) {
                Device device = mapEntry.getKey();
                Future<Boolean> keepAliveFutureTask = mapEntry.getValue();
                Boolean isDeviceAlive;
                try {
                    logger.debug("Trying To get keepAlive for " + device.getAddress());
                    isDeviceAlive = keepAliveFutureTask.get(15, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    isDeviceAlive = false;
                } catch (TimeoutException e) {
                    logger.info("TimeOut Exception");
                    isDeviceAlive = false;
                }
                logger.debug("keepAlive Result for Device:" + device.getAddress() + ":" + isDeviceAlive);
            }
    }


}


