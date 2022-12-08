package com.github.sqyyy.jnb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method as an entrypoint.
 *
 * @since v0.1.0-alpha
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Entrypoint {
}
