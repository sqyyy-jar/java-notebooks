package com.github.sqyyy.jnb;

import mjson.Json;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Java Notebooks access-point.
 *
 * @since v0.1.0-alpha
 */
@SuppressWarnings("unchecked")
public class JavaNotebooks {
    private static final List<PageType> pages;

    static {
        pages = new ArrayList<>();
        load();
    }

    /**
     * Gets the list of methods annotated with {@link Entrypoint}.
     *
     * @return the list of methods annotated with {@link Entrypoint}
     * @since v0.2.0-alpha
     */
    public static List<EntrypointHandle> getEntrypointHandles() {
        return pages.stream()
            .map(PageType::entrypoints)
            .flatMap(List::stream)
            .toList();
    }

    /**
     * Gets a list of the classes annotated with {@link Page}.
     *
     * @return an unmodifiable list of the classes annotated with {@link Page}
     * @since v0.1.0-alpha
     */
    @SuppressWarnings("rawtypes")
    public static List<Class<?>> getPageClasses() {
        return (List) pages.stream()
            .map(PageType::clazz)
            .toList();
    }

    /**
     * Gets a list of the classes annotated with {@link Page} with a given page-name.
     *
     * @param name the name of the pages
     * @return an unmodifiable list of the classes annotated with {@link Page} with a given page-name
     * @since v0.1.4-alpha
     */
    @SuppressWarnings("rawtypes")
    public static List<Class<?>> getPageClassesByName(String name) {
        return (List) pages.stream()
            .filter(it -> it.page()
                .value()
                .equals(name))
            .map(PageType::clazz)
            .toList();
    }

    /**
     * Gets a list of the classes annotated with {@link Page} with a given page-name. Capitalization will be ignored.
     *
     * @param name the name of the pages
     * @return an unmodifiable list of the classes annotated with {@link Page} with a given page-name
     * @since v0.1.4-alpha
     */
    @SuppressWarnings("rawtypes")
    public static List<Class<?>> getPageClassesByNameIgnoreCase(String name) {
        return (List) pages.stream()
            .filter(it -> it.page()
                .value()
                .equalsIgnoreCase(name))
            .map(PageType::clazz)
            .toList();
    }

    /**
     * Gets a list of all pages annotated with {@link Page}.
     *
     * @return an unmodifiable list of the pages annotated with {@link Page}
     * @since v1.0.0
     */
    public static List<PageType> getPages() {
        return Collections.unmodifiableList(pages);
    }

    /**
     * Gets a list of all pages annotated with {@link Page} with a given page-name.
     *
     * @param name the name of the pages
     * @return an unmodifiable list of the pages annotated with {@link Page} with a given page-name
     * @since v1.0.0
     */
    public static List<PageType> getPagesByName(String name) {
        return pages.stream()
            .filter(it -> it.page()
                .value()
                .equals(name))
            .toList();
    }

    /**
     * Gets a list of all pages annotated with {@link Page} with a given page-name.
     *
     * @param name the name of the pages
     * @return an unmodifiable list of the pages annotated with {@link Page} with a given page-name
     * @since v1.0.0
     */
    public static List<PageType> getPagesByNameIgnoreCase(String name) {
        return pages.stream()
            .filter(it -> it.page()
                .value()
                .equalsIgnoreCase(name))
            .toList();
    }

    private static void load() {
        var lookup = MethodHandles.publicLookup();
        try (var metaFile = JavaNotebooks.class.getClassLoader()
            .getResourceAsStream("metadata.jnb.json")) {
            if (metaFile == null) {
                System.out.println("No metafile found");
                return;
            }
            var metadata = Json.read(new String(metaFile.readAllBytes(), StandardCharsets.UTF_8));
            var pagesMeta = metadata.at("pages", Json.array());
            if (!pagesMeta.isArray()) {
                throw new RuntimeException("Invalid JavaNotebooks metadata");
            }
            for (var pageMeta : pagesMeta.asJsonList()) {
                var name = pageMeta.at("name");
                if (name == null || !name.isString()) {
                    throw new RuntimeException("Invalid JavaNotebooks metadata");
                }
                var entrypoints = pageMeta.at("entrypoints");
                if (entrypoints == null || !entrypoints.isArray()) {
                    throw new RuntimeException("Invalid JavaNotebooks metadata");
                }
                var clazz = Class.forName(name.asString(), false, JavaNotebooks.class.getClassLoader());
                var pageAnnotation = clazz.getAnnotation(Page.class);
                if (pageAnnotation == null) {
                    throw new RuntimeException("Invalid JavaNotebooks metadata");
                }
                var entrypointHandles = new ArrayList<EntrypointHandle>();
                for (var entrypoint : entrypoints.asJsonList()) {
                    if (!entrypoint.isObject()) {
                        throw new RuntimeException("Invalid JavaNotebooks metadata");
                    }
                    var entrypointArgs = entrypoint.at("args");
                    if (entrypointArgs == null || !entrypointArgs.isBoolean()) {
                        throw new RuntimeException("Invalid JavaNotebooks metadata");
                    }
                    var entrypointName = entrypoint.at("name");
                    if (entrypointName == null || !entrypointName.isString()) {
                        throw new RuntimeException("Invalid JavaNotebooks metadata");
                    }
                    if (entrypointArgs.asBoolean()) {
                        var method =
                            lookup.findStatic(clazz, entrypointName.asString(), MethodType.methodType(void.class, String[].class))
                                .asFixedArity();
                        entrypointHandles.add(new EntrypointHandle(true, method));
                    } else {
                        var method = lookup.findStatic(clazz, entrypointName.asString(), MethodType.methodType(void.class))
                            .asFixedArity();
                        entrypointHandles.add(new EntrypointHandle(false, method));
                    }
                }
                pages.add(new PageType(clazz, pageAnnotation, entrypointHandles));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid class", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Invalid method", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Illegal access", e);
        }
    }
}
