package sot.core;

import sot.common.Common;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LD on 18/08/2017.
 */
public class Hierarchy {

    public static ConcurrentHashMap<String, Device> knownDevicesInMyNetwork = new ConcurrentHashMap<>();
    public static HashSet<String> ipsToIgnore = new HashSet<>();
    public static Thread listeningThread;
    public static Thread discoveryThread;

    static {
        knownDevicesInMyNetwork.put(Common.getMyDevice().getAddress(), Common.getMyDevice());
        ipsToIgnore.add(Common.getMyDevice().getAddress());
        ipsToIgnore.add("127.0.0.1");
    }


    public static void launchListeningThread() {
        listeningThread = new Thread(new ListeningThread());
        listeningThread.start();
    }

    public static void launchDiscoveringThread() {
        //in case multiple devices launch in the same time launch discoveringThread in different time
        int rndTime = Common.getRandomInt(1, 10);
        Common.sleep(rndTime);

        discoveryThread = new Thread(new DiscoveryThread());
        discoveryThread.start();
    }
}
