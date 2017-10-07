package sot.core.entities;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by LD on 18/08/2017.
 */
public class Device {

    private String Name;
    private Rule rule;
    private String ipAddress;

    protected Device() {
        // For Jackson
    }

    public Device(String name) {
        Name = name;
        this.rule = null;
        try {
            this.ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "Device{" +
                "Name='" + Name + '\'' +
                ", rule=" + rule +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
