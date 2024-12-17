package listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Is executed for each available test (not only for those that will be executed in current run)
 * Not used for now
 */
public class AnnotationTransformer implements IAnnotationTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationTransformer.class);

    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {

        LOGGER.info(annotation.getTestName());
    }
}
