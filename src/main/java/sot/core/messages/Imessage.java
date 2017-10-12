package sot.core.messages;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by LD on 26/08/2017.
 */
public interface Imessage {
    void handle(String message, DatagramPacket packet, DatagramSocket datagramSocket);
}
