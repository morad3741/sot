package multiserverclient.core;

/**
 * Created by LD on 04/06/2017.
 */
public class MainApp {
    public static void main(String[] args) {
        try {
            System.out.println("System Started, Device=" + Common.serialiseObject(Common.getMyDevice()));
            Hierarchy.knownDevicesInMyNetwork.put(Common.getMyDevice().getAddress(), Common.getMyDevice());
            //System.out.println("launching ListeningThread");
            Thread listeningThread = new Thread(new ListeningThread());
            listeningThread.start();

            int rndTime = Common.getRandomInt(1, 10);
            Common.sleep(rndTime);

            // System.out.println("launching DiscoveryThread");
            Thread discoveryThread = new Thread(new DiscoveryThread());
            discoveryThread.start();

            synchronized (listeningThread) {
                listeningThread.wait(); // just to prevent system close
            }
            System.out.println("END.");
        } catch (Exception e) {
            System.out.println("MainAppError: " + e.getMessage());
        }


    }


}
