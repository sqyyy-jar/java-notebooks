package com.github.sqyyy.jnb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as a notebook-page.
 *
 * @since v0.1.0-alpha
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Page {
    /**
     * The name of the page
     *
     * @return the name of the page
     * @since v0.1.0-alpha
     */
    String value();

    /**
     * The description of the page.
     * An empty string by default.
     *
     * @return the description of the page
     * @since v0.1.3-alpha
     */
    String description() default "";
}
