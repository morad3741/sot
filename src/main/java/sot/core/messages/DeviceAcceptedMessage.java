package sot.core.messages;//package sot.core.messages;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sot.common.Common;
import sot.core.IHierarchy;
import sot.core.entities.Device;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

/**
 * Created by LD on 26/08/2017.
 */
@Component("DEVICE_ACCEPTED")
public class DeviceAcceptedMessage implements Imessage {

    final static Logger logger = Logger.getLogger(DeviceAcceptedMessage.class);

    @Autowired
    IHierarchy hierarchy;

    @Override
    public void handle(String message, DatagramPacket packet, DatagramSocket datagramSocket) {
        logger.debug("DEVICE_ACCEPTED Massage Handler");

        String knownDevicesInNetwork = message.split("DEVICE_ACCEPTED-")[1];
        ArrayList<Device> knownDevices = Common.deSerialiseObjectToList(knownDevicesInNetwork, new TypeReference<ArrayList<Device>>() {
        });
        for (Device device : knownDevices) {
            hierarchy.getKnownDevicesInMyNetwork().putIfAbsent(device.getIpAddress(), device);
        }
        Common.printMap( hierarchy.getKnownDevicesInMyNetwork());

    }

}
