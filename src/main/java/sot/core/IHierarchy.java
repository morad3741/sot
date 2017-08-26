package sot.core;

import sot.core.entities.Device;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LD on 26/08/2017.
 */
public interface IHierarchy {
    ConcurrentHashMap<String, Device> getKnownDevicesInMyNetwork();

    HashSet<String> getIpsToIgnore();
}
