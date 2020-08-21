package exam.veracityid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
    private final static Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
