package com.github.sqyyy.jnb;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Java Notebooks access-point.
 *
 * @since v0.1.0-alpha
 */
@SuppressWarnings("unchecked")
public class JavaNotebooks {
    private static final Class<?> entrypointsClass;
    private static final Class<?> pagesClass;
    private static final Map<Class<?>, MethodHandle> entrypointMethods;
    private static final List<Class<?>> pageClasses;

    static {
        try {
            entrypointsClass = Class.forName("$metadata.jnb.Entrypoints");
            pagesClass = Class.forName("$metadata.jnb.Pages");
            entrypointMethods = (Map<Class<?>, MethodHandle>) entrypointsClass.getDeclaredField("$entrypoints").get(null);
            pageClasses = (List<Class<?>>) pagesClass.getDeclaredField("$pages").get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the list of methods annotated with {@link Entrypoint}.
     *
     * @return the list of methods annotated with {@link Entrypoint}
     * @since v0.1.0-alpha
     * @deprecated this method will always return an empty list
     */
    @Deprecated(forRemoval = true)
    public static List<Method> getEntrypointMethods() {
        return List.of();
    }

    /**
     * Gets the list of methods annotated with {@link Entrypoint}.
     *
     * @return the list of methods annotated with {@link Entrypoint}
     * @since v0.2.0-alpha
     */
    public static List<MethodHandle> getEntrypointHandles() {
        return List.copyOf(entrypointMethods.values());
    }

    /**
     * Gets the list of methods annotated with {@link Entrypoint} mapped by their defining class.
     *
     * @return the map of methods annotated with {@link Entrypoint} mapped by their defining class
     * @since v0.2.0-alpha
     */
    public static Map<Class<?>, MethodHandle> getEntrypoints() {
        return Collections.unmodifiableMap(entrypointMethods);
    }

    /**
     * Gets the list of methods annotated with {@link Entrypoint} in a given class.
     *
     * @param clazz the declaring class of the entrypoints
     * @return the list of methods annotated with {@link Entrypoint} in a given class
     * @since v0.1.4-alpha
     * @deprecated this method will always return an empty list
     */
    @Deprecated(forRemoval = true)
    public static List<Method> getEntrypointMethodsByClass(Class<?> clazz) {
        return List.of();
    }

    /**
     * Gets the method annotated with {@link Entrypoint} in a given class.
     * Returns {@code null} if no entrypoint was found.
     *
     * @param clazz the declaring class of the entrypoint
     * @return the {@link MethodHandle} of the method annotated with {@link Entrypoint} in a given class
     * @since v0.2.0-alpha
     */
    public static MethodHandle getEntrypointByClass(Class<?> clazz) {
        return entrypointMethods.get(clazz);
    }

    /**
     * Gets a list of the classes annotated with {@link Page}.
     *
     * @return an unmodifiable list of the classes annotated with {@link Page}
     * @since v0.1.0-alpha
     */
    public static List<Class<?>> getPageClasses() {
        return Collections.unmodifiableList(pageClasses);
    }

    /**
     * Gets a list of the classes annotated with {@link Page} with a given page-name.
     *
     * @param name the name of the pages
     * @return an unmodifiable list of the classes annotated with {@link Page} with a given page-name
     * @since v0.1.4-alpha
     */
    public static List<Class<?>> getPageClassesByName(String name) {
        return pageClasses.stream().filter(aClass -> aClass.getAnnotation(Page.class).value().equals(name)).toList();
    }

    /**
     * Gets a list of the classes annotated with {@link Page} with a given page-name.
     * Capitalization will be ignored.
     *
     * @param name the name of the pages
     * @return an unmodifiable list of the classes annotated with {@link Page} with a given page-name
     * @since v0.1.4-alpha
     */
    public static List<Class<?>> getPageClassesByNameIgnoreCase(String name) {
        return pageClasses.stream().filter(aClass -> aClass.getAnnotation(Page.class).value().equalsIgnoreCase(name)).toList();
    }
}
