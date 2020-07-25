import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ParkingLogger {
    public Logger logger = null;
    private static ParkingLogger instance = null;
    public static ParkingLogger getInstance(){
        if(instance==null){
            instance = new ParkingLogger();
        }
        return instance;
    }
    private ParkingLogger(){
        logger = Logger.getLogger("Logger");
        initLog();
    }

    private void initLog(){
        FileHandler fh;

        try {
            fh = new FileHandler("../MyLogFile.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
