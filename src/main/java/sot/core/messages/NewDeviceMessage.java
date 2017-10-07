package sot.core.messages;//package sot.core.messages;

import org.springframework.stereotype.Component;

/**
 * Created by LD on 26/08/2017.
 */
@Component("NEW_DEVICE")
public class NewDeviceMessage implements Imessage {
    @Override
    public void handle() {
        System.out.println("ss");
    }

}
