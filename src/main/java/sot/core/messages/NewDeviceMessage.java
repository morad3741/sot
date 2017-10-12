package sot.core.messages;//package sot.core.messages;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sot.common.Common;
import sot.core.IHierarchy;
import sot.core.entities.Device;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

/**
 * Created by LD on 26/08/2017.
 */
@Component("NEW_DEVICE")
public class NewDeviceMessage implements Imessage {

    final static Logger logger = Logger.getLogger(NewDeviceMessage.class);

    @Autowired
    IHierarchy hierarchy;

    @Override
    public void handle(String message, DatagramPacket packet, DatagramSocket datagramSocket) {
        logger.debug("NEW_DEVICE Massage Handler");

        String newDeviceJsonData = message.split("NEW_DEVICE-")[1];
        Device newDevice = Common.deSerialiseObject(newDeviceJsonData, Device.class);
        if (hierarchy.getKnownDevicesInMyNetwork().get(newDevice.getIpAddress()) == null) {
            hierarchy.getKnownDevicesInMyNetwork().putIfAbsent(newDevice.getIpAddress(), newDevice);
            Common.printMap(hierarchy.getKnownDevicesInMyNetwork());
            String responseData = "DEVICE_ACCEPTED-" + Common.serialiseObject(new ArrayList(hierarchy.getKnownDevicesInMyNetwork().values()));
            byte[] sendData = responseData.getBytes();

            //Send a response
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());

            try {
                datagramSocket.send(sendPacket);
            } catch (IOException e) {
                logger.error("Error sending packet" + e.getMessage());
                e.printStackTrace();
            }

            logger.debug("Returned packet to: " + sendPacket.getAddress().getHostAddress() + " data:" + responseData);
        }

    }

}
