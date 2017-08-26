package sot.core;

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

    public  ConcurrentHashMap<String, Device> knownDevicesInMyNetwork = new ConcurrentHashMap<>();
    public  HashSet<String> ipsToIgnore = new HashSet<>();
    public  Thread listeningThread;
    public  Thread discoveryThread;


    @PostConstruct
    protected void init(){
        knownDevicesInMyNetwork.put(Common.getMyDevice().getAddress(), Common.getMyDevice());
        ipsToIgnore.add(Common.getMyDevice().getAddress());
        ipsToIgnore.add("127.0.0.1");
        launchListeningThread();
        launchDiscoveringThread();
    }


    private void launchListeningThread() {
        listeningThread = new Thread(new ListeningThread(knownDevicesInMyNetwork,ipsToIgnore));
        listeningThread.start();
    }

    private  void launchDiscoveringThread() {
        //in case multiple devices launch in the same time launch discoveringThread in different time
        int rndTime = Common.getRandomInt(1, 10);
        Common.sleep(rndTime);

        discoveryThread = new Thread(new DiscoveryThread(knownDevicesInMyNetwork,ipsToIgnore));
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
