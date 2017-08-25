package sot.core;

import org.apache.log4j.Logger;
import sot.common.Common;
import sot.core.keepalive.KeepAliveManager;

/**
 * Created by LD on 04/06/2017.
 */
public class MainApp {

    private final static Logger logger = Logger.getLogger(MainApp.class);

    public static void main(String[] args) {
        try {
            logger.debug("System Started, Device=" + Common.serialiseObject(Common.getMyDevice()));


            Hierarchy.launchListeningThread();

            Hierarchy.launchDiscoveringThread();
            KeepAliveManager.start();

            synchronized (Hierarchy.listeningThread) {
                try {
                    Hierarchy.listeningThread.wait(); // just to prevent system close
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }


            logger.debug("END.");
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }


    }


}
