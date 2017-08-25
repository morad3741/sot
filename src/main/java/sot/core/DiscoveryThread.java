package sot.core;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.log4j.Logger;
import sot.common.Common;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by LD on 18/08/2017.
 */
public class DiscoveryThread implements Runnable {

    final static Logger logger = Logger.getLogger(DiscoveryThread.class);

    @Override
    public void run() {
        // Find the server using UDP broadcast
        DatagramSocket c = null;
        try {
            //Open a random port to send the package
            c = new DatagramSocket();
            c.setSoTimeout(10 * 1000);
            c.setBroadcast(true);

            byte[] sendData = ("NEW_DEVICE-" + Common.serialiseObject(Common.getMyDevice())).getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                c.send(sendPacket);
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }

            // Broadcast the message over all the network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    Hierarchy.ipsToIgnore.add(interfaceAddress.getAddress().getHostAddress());
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        c.send(sendPacket);
                        logger.debug("Discovery packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            try {
                c.receive(receivePacket);
            } catch (SocketTimeoutException e) {
                logger.debug(" Discovery timeout reached");
                c.close();
                return;
            }


            //We have a response
            if (!Hierarchy.ipsToIgnore.contains(receivePacket.getAddress().getHostAddress())) {
                logger.debug("Received response from: " + receivePacket.getAddress().getHostAddress());

                //Check if the message is correct
                String message = new String(receivePacket.getData()).trim();
                if (message.startsWith("DEVICE_ACCEPTED-")) {
                    String knownDevicesInNetwork = message.split("DEVICE_ACCEPTED-")[1];
                    ArrayList<Device> knownDevices = Common.deSerialiseObjectToList(knownDevicesInNetwork, new TypeReference<ArrayList<Device>>() {
                    });
                    for (Device device : knownDevices) {
                        Hierarchy.knownDevicesInMyNetwork.putIfAbsent(device.getAddress(), device);
                    }
                    Common.printMap(Hierarchy.knownDevicesInMyNetwork);
                }
            }

            logger.debug("Finished.");
        } catch (IOException ex) {
            logger.error("ERROR - " + ex.getMessage());

        } finally {
            //Close the port!
            if (c != null)
                c.close();
        }
    }

}
