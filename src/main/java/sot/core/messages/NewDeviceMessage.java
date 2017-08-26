package sot.core.messages;//package sot.core.messages;

/**
 * Created by LD on 26/08/2017.
 */
public class NewDeviceMessage implements Imessage {
    @Override
    public void handle() {

    }

    @Override
    public String getType() {
        return "NEW_DEVICE-";
    }
}
