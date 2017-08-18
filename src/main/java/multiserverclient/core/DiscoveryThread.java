package multiserverclient.core;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by LD on 18/08/2017.
 */
public class DiscoveryThread implements Runnable {

    @Override
    public void run() {
        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setSoTimeout(0);
            c.setBroadcast(true);

            byte[] sendData = ("NEW_DEVICE-" + Common.serialiseObject(Common.getMyDevice())).getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                c.send(sendPacket);
                //System.out.println(getClass().getSimpleName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            }

            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }

                    //System.out.println(getClass().getSimpleName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            // System.out.println(getClass().getSimpleName() +  ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            if (!receivePacket.getAddress().getHostAddress().equals(Common.getMyDevice().getAddress())) {
                System.out.println(getClass().getSimpleName() + ">>> Received response from: " + receivePacket.getAddress().getHostAddress());

                //Check if the message is correct
                String message = new String(receivePacket.getData()).trim();
                if (message.startsWith("DEVICE_ACCEPTED-")) {
                    String knownDevicesInNetwork = message.split("DEVICE_ACCEPTED-")[1];
                    ArrayList<Device> knownDevices = new ArrayList<>();
                    knownDevices = Common.deSerialiseObjectToList(knownDevicesInNetwork, new TypeReference<ArrayList<Device>>() {
                    });
                    for (Device device : knownDevices) {
                        if (!device.getAddress().equals(Common.getMyDevice().getAddress()))
                            Hierarchy.knownDevicesInMyNetwork.putIfAbsent(device.getAddress(), device);
                    }
                    if (Hierarchy.knownDevicesInMyNetwork.size() == 0)
                        System.out.println(getClass().getSimpleName() + ">>>Summery: Empty");
                    else {
                        System.out.println(getClass().getSimpleName() + ">>> Map Content:");
                        Hierarchy.knownDevicesInMyNetwork.entrySet().forEach(entry -> System.out.println("\tKey:" + entry.getKey() + " Value:" + entry.getValue()));
                    }
                }
            }
            //Close the port!
            c.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            // Logger.getLogger(LoginWindow.class.getSimpleName()).log(Level.SEVERE, null, ex);
        }
    }

}
