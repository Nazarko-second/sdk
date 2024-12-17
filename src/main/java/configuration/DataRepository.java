package configuration;

import datasources.FileManager;
import datasources.JSONConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class DataRepository {

    // main logger
    private static final Logger LOGGER = LoggerFactory.getLogger(DataRepository.class);

    private static DataRepository instance;
    public static DataRepository Instance = (instance != null) ? instance : new DataRepository();
    public static String DEFAULT_CSV_SEPARATOR = "\\|";
    public static String DATA_DIR = "src/test/automation/resources/data/" + ProjectConfiguration.getConfigProperty("DataDir");
    public static final String DEFAULT_DATA_DIR = "src/test/automation/resources/data/default/";
    public static final String QA_DATA_DIR = "src/test/automation/resources/data/default_qa/";
    public static final String STAGING_DATA_DIR = "src/test/automation/resources/data/default_staging/";
    public static final String PROD_DATA_DIR = "src/test/automation/resources/data/default_prod/";


    /**
     * Get list of lists items from file
     * @param fileName name of file in TEST_DATA_RESOURCES
     * @return List of Lists of Strings with all data from file
     * @throws Exception possible exception
     */
    public List<String[]> getTableDataFromFile(String fileName,String... separator) throws Exception {
        List<String[]> result = new LinkedList<>();
        String currentSeparator = separator.length==0 ? DEFAULT_CSV_SEPARATOR : separator[0];

        List<String> lineList = FileManager.getFileContentAsListOfLines( getDataFile(fileName));
        for(String line : lineList){
            String[] list = (line.split(currentSeparator));
            result.add(list);
        }
        return result;
    }

    /**
     * Get test file name based on dataField
     * @param dataField
     * @return file location
     */
    public File getDataFile(String dataField) {
        LOGGER.info("Get data file from field:" + dataField);
        return FileManager.getFileFromDirs(dataField, DATA_DIR, DEFAULT_DATA_DIR);//, DEFAULT_TEST_DATA_RESOURCES);
    }

    /**
     * Get data file location
     * @param dataField
     * @return
     * @throws Exception
     */
    public String getDataFileLocation(String dataField){
        return getDataFile(dataField).getAbsolutePath();
    }

    /**
     * if data file is not found in specific folder, try to find it in default folders
     * @param testName test case name
     * @return hash map with parameters
     */
    public HashMap<String, String> getDefaultParametersForTest(String testName){
        File file = null;
        String env = getEnvironment(ProjectConfiguration.getConfigProperty("DataDir"));

        switch (env) {
            case "qa":
                file = FileManager.getFileFromDir(testName, QA_DATA_DIR);
                if(file != null) break; // if fine not found for in QA_DEFAULT folder, grab file from STAGING_DEFAULT before going to DEFAULT
            case "staging":
                file = FileManager.getFileFromDir(testName, STAGING_DATA_DIR);
                break;
            case "prod":
                file = FileManager.getFileFromDir(testName, PROD_DATA_DIR);
                break;
        }

        if(file == null)
            file = FileManager.getFileFromDir(testName, DEFAULT_DATA_DIR);

        // if file is not found - fail current test instead of raising exception for whole framework
        if(file == null) Assert.fail("Property file has not been found");

        if(file.getName().contains(".properties"))
            return getParamsFromProperties(file);
        if(file.getName().contains(".json"))
            return getParamsFromJSON(file);

        return null;
    }

    /**
     * get environment based on DataDir property in configuration
     * @param dataDir data files location
     * @return environment name
     */
    private String getEnvironment(String dataDir) {
        String env = "";
        if(dataDir.contains("staging_"))
            env = "staging";
        else if(dataDir.contains("prod_"))
            env = "prod";
        else if(dataDir.contains("qa_"))
            env = "qa";
        return env;
    }



    /**
     * get HashMap from properties
     * @param testName file name
     * @return HashMap parameters from file
     */
    public HashMap<String, String> getParametersForTest(String testName) {
        LOGGER.info("Get parameters for test");
        File file = FileManager.getFileFromDir(testName, DATA_DIR);

        // if file not found, search in default folders
        if(file == null) return getDefaultParametersForTest(testName);

        if(file.getName().contains(".properties"))
            return getParamsFromProperties(file);
        if(file.getName().contains(".json"))
            return getParamsFromJSON(file);

        return null;
    }

    /**
     * get HashMap from properties/json
     * @param fileName file name
     * @return HashMap parameters from file
     */
    public HashMap<String, String> getParametersFromFile(String fileName,String folderLocation) {
        File file = FileManager.getFileFromDir(fileName, folderLocation);
        if(file.getName().contains(".properties"))
            return getParamsFromProperties(file);
        if(file.getName().contains(".json"))
            return getParamsFromJSON(file);

        return null;
    }

    /**
     * get HashMap from properties
     * @param testName file name
     * @return HashMap parameters from file
     */
    //TODO add possibility use default dir
    public HashMap<String, String> getParametersForTestDefaultDir(String testName) {
        File file = FileManager.getFileFromDir(testName, DEFAULT_DATA_DIR);
        if(file.getName().contains(".properties"))
            return getParamsFromProperties(file);
        if(file.getName().contains(".json"))
            return getParamsFromJSON(file);

        return null;
    }

    /**
     * get String from file
     * @param fileName file name
     * @return String text from file
     */
    public String getContentFromFile(String fileName){
        String filePath = DATA_DIR+"/"+fileName;
        return FileManager.getFileContent(filePath);
    }

    /**
     * get String from file
     * @param fileName file name
     * @return String text from file
     */
    public String[] getLinesFromFile(String fileName){
        String filePath = DATA_DIR+"/"+fileName;
        return FileManager.getFileContent(filePath).split("\\n");
    }


    /**
     * get HashMap parameters from file
     * @param file
     * @return HashMap parameters from file
     */
    private HashMap<String,String> getParamsFromProperties(File file){
        LOGGER.info("Load data properties file: " + file.getPath());
        HashMap<String,String> results = new HashMap<>();
        results.put("propFilePath", file.getPath());
        Properties props = new Properties();
        FileInputStream fileInput = null;
        try {
            fileInput = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LOGGER.error("File was not found " + file.getAbsolutePath(), e);
        }
        try {
            props.load(fileInput);
        } catch (IOException e) {
            LOGGER.error("Problems with properties loading " + file.getAbsolutePath(), e);
        }

        for (Map.Entry property: props.entrySet()) {
            results.put((String)property.getKey(), (String) property.getValue());
        }

        return results;
    }

    /**
     * get HashMap parameters from Json file
     * @param file
     * @return HashMap parameters from Json
     */
    private HashMap<String,String> getParamsFromJSON(File file){
        LOGGER.info("Load JSON data file: " + file.getPath());
        String jsonFromFile= FileManager.getFileContent(file);
        HashMap<String, String> result = JSONConverter.toHashMapFromJsonString(jsonFromFile);
        return result;
    }

}