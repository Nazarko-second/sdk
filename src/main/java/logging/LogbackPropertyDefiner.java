package logging;

import datasources.FileManager;
import ch.qos.logback.core.PropertyDefinerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logback property definer (required to make available logs to multiple locations)
 */
public class LogbackPropertyDefiner extends PropertyDefinerBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackPropertyDefiner.class);
    @Override
    public String getPropertyValue() {
        String callingMethod = new Throwable().getStackTrace()[2].getMethodName();
        LOGGER.info("getPropertyValue() was called by: " + callingMethod);
        return FileManager.OUTPUT_DIR;
    }
}
