package listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestNGMethod;

import java.util.List;


public class TestSuiteListener implements ISuiteListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSuiteListener.class);

    /**
     * Log amount of tests that will be executed
     * @param suite
     */
    @Override
    public void onStart(ISuite suite) {
//        LOGGER.info("--- SUITE LISTENER START ---");
        List<ITestNGMethod> allMethods = suite.getAllMethods();
        LOGGER.info("AMOUNT OF TESTS TO BE EXECUTED: {}", allMethods.size());
        for (ITestNGMethod m : allMethods) {
            LOGGER.info(m.getTestClass().getName());
        }

//        try (PrintStream out = new PrintStream(new FileOutputStream("/tmp/test-list.txt"))) {
//            suite.getAllMethods().stream()
//                    .map(m -> m.getTestClass().getName() + "." + m.getMethodName())
//                    .forEach(out::println);
//        } catch (IOException e) {
//            System.err.println("FAILED TO WRITE TO FILE!");
//            throw new RuntimeException(e);
//        }
        LOGGER.info("--- SUITE LISTENER END ---");

    }
    @Override
    public void onFinish(ISuite suite) {}
}
