package multiserverclient.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by LD on 18/08/2017.
 */
public class Device {

    private String Name;
    private Rule rule;
    private String address;

    protected Device() {
        // For Jackson
    }

    public Device(String name) {
        Name = name;
        this.rule = null;
        try {
            this.address = InetAddress.getLocalHost().getHostAddress();
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Device{" +
                "Name='" + Name + '\'' +
                ", rule=" + rule +
                ", address='" + address + '\'' +
                '}';
    }
}
