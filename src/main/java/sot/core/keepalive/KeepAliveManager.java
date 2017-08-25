package sot.core.keepalive;

import org.apache.log4j.Logger;
import sot.common.Common;
import sot.core.Device;
import sot.core.Hierarchy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by LD on 19/08/2017.
 */
public class KeepAliveManager {

    final static Logger logger = Logger.getLogger(KeepAliveCallable.class);
    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private KeepAliveManager() {
    }

    public static void start() {
        Map<Device, Future<Boolean>> keepAliveResultMap = new HashMap<>();

        scheduledExecutorService.scheduleWithFixedDelay(() -> {

            keepAliveResultMap.clear();
            for (String deviceAddress : Hierarchy.knownDevicesInMyNetwork.keySet()) {
                // get(i) may not exist anymore but may still be in view of the iterator
                Device device = Hierarchy.knownDevicesInMyNetwork.getOrDefault(deviceAddress, null);
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
                    logger.info("Trying To get keepAlive for " + device.getAddress());
                    isDeviceAlive = keepAliveFutureTask.get(15, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    isDeviceAlive = false;
                } catch (TimeoutException e) {
                    logger.info("TimeOut Exception");
                    isDeviceAlive = false;
                }
                logger.info("keepAlive Result for Device:" + device.getAddress() + ":" + isDeviceAlive);
            }

        }, 0, 5, TimeUnit.SECONDS);


    }


}


