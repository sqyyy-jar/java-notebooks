package com.github.sqyyy.jnb;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public class JavaNotebooks {
    private static final Class<?> entrypointsClass;
    private static final Class<?> pagesClass;
    private static final List<Method> entrypointMethods;
    private static final List<Class<?>> pageClasses;

    static {
        try {
            entrypointsClass = Class.forName("$metadata.jnb.Entrypoints");
            pagesClass = Class.forName("$metadata.jnb.Pages");
            entrypointMethods = (List<Method>) entrypointsClass.getDeclaredField("$entrypoints").get(null);
            pageClasses = (List<Class<?>>) pagesClass.getDeclaredField("$pages").get(null);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Method> getEntrypointMethods() {
        return Collections.unmodifiableList(entrypointMethods);
    }

    public static List<Class<?>> getPageClasses() {
        return Collections.unmodifiableList(pageClasses);
    }
}
