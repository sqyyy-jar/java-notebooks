package com.github.sqyyy.jnb;

import java.util.List;

public record PageType(Class<?> clazz, Page page, List<EntrypointHandle> entrypoints) {
}
