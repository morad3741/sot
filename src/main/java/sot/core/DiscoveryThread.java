package sot.core;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sot.common.Common;
import sot.core.messages.Imessage;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by LD on 18/08/2017.
 */
@Component
@Scope("prototype")
public class DiscoveryThread extends Thread {

    final static Logger logger = Logger.getLogger(DiscoveryThread.class);

    @Autowired
    IHierarchy hierarchy;

    @Autowired
    Map<String,Imessage> messageHandlerMap;

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
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    hierarchy.getIpsToIgnore().add(interfaceAddress.getAddress().getHostAddress());

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("172.17.0.2"), 8888);
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
                logger.debug("Discovery timeout reached");
                c.close();
                return;
            }


            //We have a response
            if (!hierarchy.getIpsToIgnore().contains(receivePacket.getAddress().getHostAddress())) {
                logger.debug("Received response from: " + receivePacket.getAddress().getHostAddress());

                String message = new String(receivePacket.getData()).trim();
                String messageType = message.split("-")[0];
                Imessage messageHandler = messageHandlerMap.getOrDefault(messageType,null);
                if (messageHandler != null)
                    messageHandler.handle(message,receivePacket,null);
                else
                    logger.error("Cannot find Message Handler For: " + messageType);
            }
            logger.debug("Discovery Thread Finished.");
        } catch (IOException ex) {
            logger.error("ERROR - " + ex.getMessage());

        } finally {
            //Close the port!
            if (c != null)
                c.close();
        }
    }

}
