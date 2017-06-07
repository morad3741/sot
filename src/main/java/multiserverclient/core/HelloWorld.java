package multiserverclient.core;

import java.net.InetAddress;

/**
 * Created by LD on 04/06/2017.
 */
public class HelloWorld {
    public static void main(String[] args) {

        System.out.println("Hello World!2");
        try {
            System.out.println("Im: " + InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
