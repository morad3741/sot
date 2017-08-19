package multiserverclient.core;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by LD on 18/08/2017.
 */
public class Hierarchy {

    public static HashMap<String, Device> knownDevicesInMyNetwork = new HashMap<>();
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
