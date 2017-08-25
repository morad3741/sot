package sot.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import sot.core.Device;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * Created by LD on 18/08/2017.
 */
public class Common {
    final static Logger logger = Logger.getLogger(Common.class);

    private static int counter;
    private static Random rnd;
    private static ObjectMapper objectMapper;
    private static Device myDevice;

    static {
        counter = 1;
        myDevice = new Device("Device" + counter++);
        objectMapper = new ObjectMapper();
        rnd = new Random();
    }

    private Common() {
    }

    public static String serialiseObject(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <E> E deSerialiseObject(String jsonInString, Class<E> targetClass) {
        try {
            E obj = objectMapper.readValue(jsonInString, targetClass);
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T deSerialiseObjectToList(final String jsonPacket, final TypeReference<T> type) {
        T data = null;

        try {
            data = new ObjectMapper().readValue(jsonPacket, type);
        } catch (Exception e) {
            // Handle the problem
        }
        return data;
    }

    public static Device getMyDevice() {
        return myDevice;
    }

    public static int getRandomInt(int low, int high) {
        return rnd.nextInt(high - low) + low;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <K, V> void printMap(Map<K, V> map) {
        if (map.size() == 0)
            logger.debug("Map Content: Empty");
        else {
            logger.debug("Map Content:");
            for (Map.Entry<K, V> entry : map.entrySet()) {
                logger.debug("\t" + entry.getKey() + "->" + entry.getValue());
            }
        }
    }


}
