package annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ndovhoshyya on 02/23/2023.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TestCaseId {
    String id() default "";
    String description() default "";
}
