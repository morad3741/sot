package sot.core;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sot.common.Common;
import sot.core.entities.Device;
import sot.core.messages.Imessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by LD on 10/06/2017.
 */
@Component
@Scope("prototype")
public class ListeningThread extends Thread {
    final static Logger logger = Logger.getLogger(ListeningThread.class);

    DatagramSocket socket;

    @Autowired
    IHierarchy hierarchy;

    @Autowired
    Map<String,Imessage> messageHandlerMap;

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
                if (!hierarchy.getIpsToIgnore().contains(packet.getAddress().getHostAddress())) {
                    logger.debug("packet received from: " + packet.getAddress().getHostAddress() + " data:" + new String(packet.getData()));
                    //See if the packet holds the right command (message)
                    String message = new String(packet.getData()).trim();
                    String messageType = message.split("-")[0];
                    Imessage messageHandler = messageHandlerMap.getOrDefault(messageType,null);
                    if (messageHandler != null)
                        messageHandler.handle();
                    if (message.startsWith("NEW_DEVICE-")) {
                        String newDeviceJsonData = message.split("NEW_DEVICE-")[1];
                        Device newDevice = Common.deSerialiseObject(newDeviceJsonData, Device.class);
                        if (hierarchy.getKnownDevicesInMyNetwork().get(newDevice.getIpAddress()) == null) {
                            hierarchy.getKnownDevicesInMyNetwork().putIfAbsent(newDevice.getIpAddress(), newDevice);
                            Common.printMap(hierarchy.getKnownDevicesInMyNetwork());
                            String responseData = "DEVICE_ACCEPTED-" + Common.serialiseObject(new ArrayList(hierarchy.getKnownDevicesInMyNetwork().values()));
                            byte[] sendData = responseData.getBytes();

                            //Send a response
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                            socket.send(sendPacket);

                            logger.debug("Returned packet to: " + sendPacket.getAddress().getHostAddress() + " data:" + responseData);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
}
