package multiserverclient.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by LD on 10/06/2017.
 */
public class ListeningThread implements Runnable {

    DatagramSocket socket;

    public static ListeningThread getInstance() {
        return DiscoveryThreadHolder.INSTANCE;
    }

    @Override
    public void run() {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                //Packet received
                if (!packet.getAddress().getHostAddress().equals(Common.getMyDevice().getAddress()) && (!packet.getAddress().getHostAddress().equals("127.0.0.1"))) {
                    System.out.println(getClass().getSimpleName() + ">>>packet received from: " + packet.getAddress().getHostAddress() + " data:" + new String(packet.getData()));

                    //See if the packet holds the right command (message)
                    String message = new String(packet.getData()).trim();
                    if (message.startsWith("NEW_DEVICE-")) {
                        String newDeviceJsonData = message.split("NEW_DEVICE-")[1];
                        Device newDevice = Common.deSerialiseObject(newDeviceJsonData, Device.class);
                        if (Hierarchy.knownDevicesInMyNetwork.get(newDevice.getAddress()) == null && !newDevice.getAddress().equals(Common.getMyDevice().getAddress())) {
                            Hierarchy.knownDevicesInMyNetwork.put(newDevice.getAddress(), newDevice);
                            System.out.println(getClass().getSimpleName() + ">>> Map Content:");
                            Hierarchy.knownDevicesInMyNetwork.entrySet().forEach(entry -> System.out.println("\tKey:" + entry.getKey() + " Value:" + entry.getValue()));


                            ArrayList<Device> responseArrayList = new ArrayList(Hierarchy.knownDevicesInMyNetwork.values());
                            responseArrayList.remove(newDevice);

                            String responseData = "DEVICE_ACCEPTED-" + Common.serialiseObject(responseArrayList);
                            byte[] sendData = responseData.getBytes();

                            //Send a response
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                            socket.send(sendPacket);

                            System.out.println(getClass().getSimpleName() + ">>>Returned packet to: " + sendPacket.getAddress().getHostAddress() + " data:" + responseData);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("ListeningThreadError:" + ex.getMessage());
            Logger.getLogger(ListeningThread.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class DiscoveryThreadHolder {

        private static final ListeningThread INSTANCE = new ListeningThread();
    }

}
