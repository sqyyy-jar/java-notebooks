package com.github.sqyyy.jnb;

import java.lang.invoke.MethodHandle;

public record EntrypointHandle(boolean args, MethodHandle handle) {
}
