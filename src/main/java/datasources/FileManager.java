package datasources;

import configuration.DataRepository;
import configuration.ProjectConfiguration;
import configuration.SessionManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Interaction with file system<br>
 *     This class "knows" all paths and has implementation of basic interactions with file system
 */
public class FileManager {

    // main logger
    private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    static String MAIN_RESOURCES = "src/main/resources/";
    static String TEST_SCRIPTS = "src/test/automation/scripts/";
    public static String TEST_ACTIONS = "src/test/automation/actions/";
    static String TEST_SUITES = "src/test/automation/suites/";
    public static String TEST_AUTOMATION_RESOURCES = "src/test/automation/resources/";

    //folder in .target with downloaded/created during session files)
    public static String OUTPUT_DIR = getOutputDir();

    /**
     * Create output directory for current test session
     *
     * @return test output dir
     */
    public static String getOutputDir() {
        String callingMethod = new Throwable().getStackTrace()[2].getMethodName();
        LOGGER.info("getOutputDir() was called by: " + callingMethod);
        String directory = System.getProperty("user.dir") + File.separator + "target" + File.separator + (ProjectConfiguration.isPropertySet("Jenkins") ? "report" : SessionManager.getSessionID());
        System.setProperty("OUTPUT_DIR", directory);
        //create output dir
        try {
            createDir(directory);
        } catch (IOException e) {
            LOGGER.error("Fail create DIR" + e);
            return null;
        }
        return directory;
    }

    /**
     * Get content of file as string
     *
     * @param filePath path to file
     * @return file content
     */
    public static String getFileContent(String filePath) {
        String result = null;
        try {
            result = FileUtils.readFileToString(new File(filePath).getAbsoluteFile(), StandardCharsets.UTF_8).replace("\r", "");
        } catch (IOException e) {
            LOGGER.error("Fail get content from file path " + filePath + " " + e);
        }
        return result;
    }

    /**
     * Get content of file as string
     *
     * @param file file object
     * @return file content
     */
    public static String getFileContent(File file) {
        String result = null;
        try {
            result = FileUtils.readFileToString(file, StandardCharsets.UTF_8).replace("\r", "");
        } catch (Exception e) {
            LOGGER.error("Fail get content from " + file.getAbsolutePath() + " " + e);
        }
        return result;
    }

    /**
     * create dir
     *
     * @param path filePath
     * @throws IOException exception
     */
    public static void createDir(String path) throws IOException {
        FileUtils.forceMkdir(new File(path));
    }


    /**
     * Wait for file existance
     * @param actualFileLocation path to file
     * @return was file found
     */
    public static boolean waitForFile(String actualFileLocation) {
        boolean result = false;
        boolean timeoutReached = false;
        int currentTime = 0;
        while (!timeoutReached) {
            if (Files.exists(Paths.get(actualFileLocation))) {
                return true;
            }
            currentTime++;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                //
            }
            if (currentTime >= Integer.parseInt(ProjectConfiguration.getConfigProperty("DefaultTimeoutInSeconds")))
                timeoutReached = true;
        }
        return result;
    }

    /**
     * wait for downloaded file
     *
     * @param filePath file path
     * @return was file downloaded
     */
    public static boolean waitForDownloadedFile(String filePath) {
        return waitForFile(filePath);
    }

    /**
     * Archive files to ZIP
     *
     * @param listOfResultsFile list of file locations
     * @return zip file location
     */
    public static String archiveFiles(List<String> listOfResultsFile) {
        String archiveName = SessionManager.getSessionID() + ".zip";

        try (FileOutputStream fos = new FileOutputStream(archiveName);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            for (String file : listOfResultsFile) {
                if (file != null) {
                    File fileToZip = new File(file);
                    zipFile(fileToZip, fileToZip.getName(), zipOut);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Fail archive Files");

        }

        return archiveName;
    }

    /**
     * Compress file as ZIP
     *
     * @param fileToZip File object
     * @param fileName  zip file name
     * @param zipOut    ZipOutputStream object
     * @throws IOException exception
     */
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            if (children == null)
                return;
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    /**
     * Append to file
     *
     * @param fileName file location
     * @param data     data to add
     * @throws IOException exception
     */
    public static void appendToFile(String fileName, String data) throws IOException {
        FileUtils.writeStringToFile(new File(fileName), data, true);
    }

    /**
     * Get files from dir
     *
     * @param fileName name of file
     * @param dir      dir location
     * @return list of files
     */
    public static File getFileFromDir(String fileName, String dir) {
        ArrayList<String> result = new ArrayList<>();
        IOFileFilter fn = new NameFileFilter(fileName);
        IOFileFilter dn = new NameFileFilter(fileName);
        try {

            return FileUtils.listFiles(new File(dir), new WildcardFileFilter(fileName + ".*"), new WildcardFileFilter("*")).stream().findFirst().get();

            //was
            // return FileUtils.listFiles(new File(dir), new WildcardFileFilter(fileName + "*"), new WildcardFileFilter("*")).stream().findFirst().get();
        } catch (NoSuchElementException e) {
            LOGGER.error("File was not found: " + fileName + " in " + dir);
        }
        return null;
    }

    /**
     * Check if file exists
     *
     * @param file file location
     * @return does file exist
     */
    public static boolean doesExist(String file) {
        return (new File(file)).exists();
    }

    /**
     * Delete file
     *
     * @param file file location
     * @return was file deleted
     */
    public static boolean deleteFile(String file) {
        return (new File(file)).delete();
    }

    /**
     * Replace String in specified file
     *
     * @param fileContent  whole file content
     * @param startWith    start of line
     * @param newLineValue new value of line
     * @return file content
     */
    public static String replaceStringInFileContent(String fileContent, String startWith, String newLineValue) {
        StringBuilder results = new StringBuilder();
        String[] allLines = fileContent.split("\n");
        boolean alreadyProcessed = false;
        for (String allLine : allLines) {
            if (allLine.startsWith(startWith) && !alreadyProcessed) {
                results.append(results.toString().equals("") ? "" : "\n").append(newLineValue);
                alreadyProcessed = true;
            } else
                results.append(results.toString().equals("") ? "" : "\n").append(allLine);
        }
        return results.toString();
    }

    /**
     * Get file name from string PATH
     *
     * @param filePath file location
     * @return file name with extension
     */
    public static String getFileNameFromPath(String filePath) {
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * get list of lines from file
     *
     * @param file file object
     * @return list of lines from files
     * @throws IOException exception
     */
    public static List<String> getFileContentAsListOfLines(File file) throws IOException {
        return FileUtils.readLines(file, StandardCharsets.UTF_8);
    }

    /**
     * Create new file with content
     *
     * @param fileLocation file location
     * @param content      content of file
     */
    public static void createFile(String fileLocation, String content) throws IOException {
        LOGGER.info("Create file: " + fileLocation);
        FileUtils.writeStringToFile(new File(fileLocation), content, StandardCharsets.UTF_8);
    }

    /**
     * Get files from dir
     *
     * @param fileName name of file
     * @param dir      dir location
     * @return list of files
     */
    public static File getFileFromDirs(String fileName, String... dir) {
        ArrayList<String> result = new ArrayList<>();
        IOFileFilter fn = new NameFileFilter(fileName);
        IOFileFilter dn = new NameFileFilter(fileName);

        try {
            return FileUtils.listFiles(new File(dir[0]), new WildcardFileFilter(fileName + "*"), new WildcardFileFilter("*")).stream().findFirst().get();
        } catch (NoSuchElementException e) {
            LOGGER.error("File was not found: " + fileName + " in " + dir[0]);
        }

        try {
            return FileUtils.listFiles(new File(dir[1]), new WildcardFileFilter(fileName + "*"), new WildcardFileFilter("*")).stream().findFirst().get();
        } catch (NoSuchElementException e) {
            LOGGER.error("File was not found: " + fileName + " in " + dir[1]);
        }

        try {
            return FileUtils.listFiles(new File("."), new WildcardFileFilter(fileName + "*"), null).stream().findFirst().get();
        } catch (NoSuchElementException e) {
            LOGGER.error("File was not found: " + fileName + " in current dir");
        }

        return null;
    }

}

