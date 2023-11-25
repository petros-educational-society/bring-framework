package com.petros.bringframework.beans.factory.annotation;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * The <code>DestroyPlease</code> annotation is used on a method as a
 * callback notification to signal that the instance is in the
 * process of being removed by the container. The method annotated
 * with <code>DestroyPlease</code> is typically used to
 * release resources that it has been holding.
 *
 * @author "Vasiuk Maryna"
 */

@Documented
@Retention (RUNTIME)
@Target(METHOD)
public @interface DestroyPlease {
}
