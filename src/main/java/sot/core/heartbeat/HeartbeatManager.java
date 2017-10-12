package sot.core.heartbeat;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sot.common.Common;
import sot.core.IHierarchy;
import sot.core.entities.Device;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by LD on 19/08/2017.
 */
@Component
public class HeartbeatManager {

    final static Logger logger = Logger.getLogger(HeartbeatManager.class);

    @Autowired
    private IHierarchy hierarchy;

    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private int targetPort = 8889;

    @PostConstruct
    private void init() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {

            ServerSocket serverSocket = new ServerSocket(targetPort);
            while (true) {
                Socket heartbeatSocket = null;
                try {
                    heartbeatSocket = serverSocket.accept();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
                finally {
                    if (heartbeatSocket != null)
                        heartbeatSocket.close();
                }

            }
        });


    }

    @Scheduled(fixedDelay = 5000)
    protected void initScheduler() {
        Map<Device, Future<Boolean>> keepAliveResultMap = new HashMap<>();

        keepAliveResultMap.clear();
        for (String deviceAddress : hierarchy.getKnownDevicesInMyNetwork().keySet()) {
            // get(i) may not exist anymore but may still be in view of the iterator
            Device device = hierarchy.getKnownDevicesInMyNetwork().getOrDefault(deviceAddress, null);
            if (device == null || device == Common.getMyDevice())
                continue;
            else {
                //logger.debug("Initiating Heartbeat:" + device.getIpAddress());
                Future<Boolean> future = cachedThreadPool.submit(new HeartbeatFutureTask(device, targetPort));
                keepAliveResultMap.put(device, future);
            }
        }
        for (Map.Entry<Device, Future<Boolean>> mapEntry : keepAliveResultMap.entrySet()) {
            Device device = mapEntry.getKey();
            Future<Boolean> keepAliveFutureTask = mapEntry.getValue();
            Boolean isDeviceAlive;
            try {
                //logger.debug("Trying To get Heartbeat for " + device.getIpAddress());
                isDeviceAlive = keepAliveFutureTask.get(15, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                isDeviceAlive = false;
            } catch (TimeoutException e) {
                logger.info("TimeOut Exception");
                isDeviceAlive = false;
            }
            logger.debug("Heartbeat Result for Device:" + device.getIpAddress() + ":" + isDeviceAlive);
        }
    }


}


