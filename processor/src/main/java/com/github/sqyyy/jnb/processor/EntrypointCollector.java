package com.github.sqyyy.jnb.processor;

import com.github.sqyyy.jnb.Entrypoint;
import mjson.Json;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

public class EntrypointCollector {
    public static Json collect(ProcessingEnvironment procEnv, TypeElement type) {
        var entrypointMeta = Json.array();
        var methods = ElementFilter.methodsIn(type.getEnclosedElements());
        for (var method : methods) {
            var entrypointAnnotation = method.getAnnotation(Entrypoint.class);
            if (entrypointAnnotation == null) {
                continue;
            }
            var methodName = method.getSimpleName();
            if (methodName.isEmpty()) {
                procEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "Entrypoint method is anonymous");
                throw new RuntimeException("Could not collect Entrypoint annotations");
            }
            if (!method.getModifiers()
                .contains(Modifier.PUBLIC) || !method.getModifiers()
                .contains(Modifier.STATIC)) {
                procEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "Entrypoint method '" + methodName + "' must be public and static");
                throw new RuntimeException("Could not collect Entrypoint annotations");
            }
            if (method.getReturnType()
                .getKind() != TypeKind.VOID) {
                procEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "Entrypoint '" + methodName + "' must return void");
                throw new RuntimeException("Could not collect Entrypoint annotations");
            }
            var methodParameters = method.getParameters();
            if (methodParameters.size() > 1) {
                procEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR,
                        "Entrypoint method '" + methodName + "' may only contain up to one parameter (String[])");
                throw new RuntimeException("Could not collect Entrypoint annotations");
            }
            if (methodParameters.size() == 0) {
                entrypointMeta.add(Json.object()
                    .set("args", false)
                    .set("name", methodName.toString()));
                continue;
            }
            var firstMethodParameter = methodParameters.get(0)
                .asType();
            if (!procEnv.getTypeUtils()
                .isSameType(firstMethodParameter, procEnv.getTypeUtils()
                    .getArrayType(procEnv.getElementUtils()
                        .getTypeElement("java.lang.String")
                        .asType()))) {
                throw new RuntimeException(new IllegalAccessException(
                    "Entrypoint method '" + methodName + "' may only contain string-array as parameter (String[])"));
            }
            entrypointMeta.add(Json.object()
                .set("args", true)
                .set("name", methodName.toString()));
        }
        return entrypointMeta;
    }
}
