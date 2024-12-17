package annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ndovhoshyya on 09/18/2023.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BugId {
    String[] id() default "";
    String description() default "";
    String link() default "";
}
