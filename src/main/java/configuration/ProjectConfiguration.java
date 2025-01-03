package configuration;

import reporting.ReporterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Project configuration and
 * Interaction with properties
 */
public class ProjectConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectConfiguration.class);

    static public String CONFIG_FILE = System.getProperty("config");
    static String PROPERTIES_FILE = "src/test/automation/config/" + ((CONFIG_FILE == null) ? "default" : CONFIG_FILE) + ".properties";

    static public ThreadLocal<Properties> threadProperties = new ThreadLocal<Properties>();
    static private Properties localProps = loadProperties();


    /**
     * Load main config file
     *
     * @return
     */
    public static Properties loadProperties() {
        String callingMethod = new Throwable().getStackTrace()[2].getMethodName();
        LOGGER.info("loadProperties() was called by: " + callingMethod);
        LOGGER.info("CONFIG_DATA in Project Configuration");
        LOGGER.info("Property file: {}", PROPERTIES_FILE);
        LOGGER.info("qTest integration: {}", System.getProperty("qTest"));
        LOGGER.info("Config: {}", System.getProperty("config"));
        LOGGER.info("Groups: {}", System.getProperty("groups"));
        LOGGER.info("browserstack.config: {}", System.getProperty("browserstack.config"));
//        LOGGER.info("BROWSERSTACK_BUILD_NAME ENV: {}", System.getenv("BROWSERSTACK_BUILD_NAME"));


        Properties result = new Properties();

        try {

            //open file
//            File file = new File(PROPERTIES_FILE);
            //open input stream to read file
            FileInputStream fileInput = new FileInputStream(PROPERTIES_FILE); //ProjectConfiguration.class.getResourceAsStream(PROPERTIES_FILE));
            //create Properties object
            result = new Properties();
            //load properties from file
            result.load(fileInput);
            //close file
            fileInput.close();


        } catch (FileNotFoundException e) {
            ReporterManager.Instance.fail("Config was not found");
        } catch (IOException e) {
            ReporterManager.Instance.fail("Config was not opened");
        } catch (Exception e) {
            ReporterManager.Instance.fail("Field was not found: " + PROPERTIES_FILE);
        }

        //TODO move


        return result;
    }

    /**
     * Check if Property was specified
     *
     * @param property
     * @return
     */
    public static boolean isPropertySet(String property) {

        String valueFromProperties = getConfigProperty(property);
        if (valueFromProperties != null)
            return Boolean.parseBoolean(valueFromProperties);
        return false;
    }

    /**
     * Get Configuration property (from file/command line or local thread)
     *
     * @param fieldName
     * @return
     */
    public static String getConfigProperty(String fieldName) {
//        String callingMethod = new Throwable().getStackTrace()[2].getMethodName();
//        LOGGER.info("getConfigProperty() was called by: " + callingMethod);
//        LOGGER.info("Getting property: {}", fieldName);

        String result = null;

        if (System.getProperty(fieldName) != null)
            return System.getProperty(fieldName);

        if (localProps.getProperty(fieldName) != null)
            return localProps.getProperty(fieldName);

        if (threadProperties.get() != null && threadProperties.get().getProperty(fieldName) != null)
            return threadProperties.get().getProperty(fieldName);

        return result;
    }

    /**
     * Set config property
     *
     * @param fieldName
     * @param value
     */
    public static void setConfigProperty(String fieldName, String value) {
        localProps.setProperty(fieldName, value);
    }

    /**
     * Set config property for Local thread
     *
     * @param fieldName
     * @param value
     */
    public static void setLocalThreadConfigProperty(String fieldName, String value) {
        if (threadProperties.get() == null)
            threadProperties.set(new Properties());
        threadProperties.get().setProperty(fieldName, value);
    }

    /**
     * Remove config property for Local thread
     *
     * @param fieldName
     */
    public static void removeLocalThreadConfigProperty(String fieldName) {
        if (threadProperties.get() != null)
            threadProperties.get().remove(fieldName);
    }

    /**
     * True if using IE
     *
     * @return
     */
    public static boolean isIE() {
        return ProjectConfiguration.getConfigProperty("Driver").toLowerCase().equals("ie")
                || (ProjectConfiguration.getConfigProperty("RemoteBrowser") != null && ProjectConfiguration.getConfigProperty("RemoteBrowser").toLowerCase().equals("ie"));

    }
}

