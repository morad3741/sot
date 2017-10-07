package sot.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sot.common.Common;
import sot.core.entities.Device;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LD on 18/08/2017.
 */
@Component
public class Hierarchy implements IHierarchy {

    @Autowired
    ListeningThread listeningThread;

    @Autowired
    DiscoveryThread discoveryThread;

    private  ConcurrentHashMap<String, Device> knownDevicesInMyNetwork = new ConcurrentHashMap<>();
    private  HashSet<String> ipsToIgnore = new HashSet<>();

    @PostConstruct
    protected void init(){
        knownDevicesInMyNetwork.put(Common.getMyDevice().getIpAddress(), Common.getMyDevice());
        ipsToIgnore.add(Common.getMyDevice().getIpAddress());
        ipsToIgnore.add("127.0.0.1");
        launchListeningThread();
        launchDiscoveringThread();
    }


    private void launchListeningThread() {
        listeningThread.start();
    }

    private  void launchDiscoveringThread() {
        //in case multiple devices launch in the same time launch discoveringThread in different time
        int rndTime = Common.getRandomInt(1, 10);
        Common.sleep(rndTime);
        discoveryThread.start();
    }

    @Override
    public ConcurrentHashMap<String, Device> getKnownDevicesInMyNetwork() {
        return knownDevicesInMyNetwork;
    }
    @Override
    public HashSet<String> getIpsToIgnore() {
        return ipsToIgnore;
    }

}
