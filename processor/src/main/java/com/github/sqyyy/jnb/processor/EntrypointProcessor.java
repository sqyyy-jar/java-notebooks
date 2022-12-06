package com.github.sqyyy.jnb.processor;

import com.github.sqyyy.jnb.Entrypoint;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.github.sqyyy.jnb.Entrypoint")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class EntrypointProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final List<JnbMethod> entrypointMethods = new ArrayList<>();
        final Collection<? extends Element> entrypointElements = roundEnv.getElementsAnnotatedWith(Entrypoint.class);
        final List<ExecutableElement> entrypointExecutables = ElementFilter.methodsIn(entrypointElements);
        for (final ExecutableElement method : entrypointExecutables) {
            final TypeElement type = (TypeElement) method.getEnclosingElement();
            if (!type.getModifiers().contains(Modifier.PUBLIC)) {
                throw new RuntimeException(
                    new IllegalAccessException("Entrypoint-class '" + type.getQualifiedName() + "' must be public"));
            }
            if (!method.getModifiers().contains(Modifier.PUBLIC) || !method.getModifiers().contains(Modifier.STATIC)) {
                throw new RuntimeException(
                    new IllegalAccessException("Entrypoint '" + method.getSimpleName() + "' must be public and static"));
            }
            final List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.size() > 1) {
                throw new RuntimeException(new IllegalAccessException(
                    "Entrypoint '" + method.getSimpleName() + "' may only contain up to one parameter (args)"));
            }
            if (parameters.size() == 1) {
                final TypeMirror argsParameter = parameters.get(0).asType();
                processingEnv.getTypeUtils().isSameType(argsParameter, processingEnv.getTypeUtils()
                    .getArrayType(processingEnv.getElementUtils().getTypeElement("java.lang.String").asType()));
                if (argsParameter.getKind() != TypeKind.ARRAY) {
                    throw new RuntimeException(new IllegalAccessException(
                        "Entrypoint '" + method.getSimpleName() + "' may only contain string-array as parameter (args)"));
                }
                entrypointMethods.add(new JnbMethod(type.getQualifiedName().toString(), method.getSimpleName().toString(), true));
                continue;
            }
            entrypointMethods.add(new JnbMethod(type.getQualifiedName().toString(), method.getSimpleName().toString(), false));
        }
        final StringBuilder metaFile = new StringBuilder();
        metaFile.append("Class<?> var0 = null;\n");
        for (final JnbMethod entrypointMethod : entrypointMethods) {
            metaFile.append("var0 = Class.forName(\"");
            metaFile.append(entrypointMethod.clazz());
            metaFile.append("\");\n");
            metaFile.append("$entrypoints.add(var0.getDeclaredMethod(\"");
            metaFile.append(entrypointMethod.method());
            metaFile.append('"');
            if (entrypointMethod.args()) {
                metaFile.append(", String[].class");
            }
            metaFile.append("));\n");
        }
        Processors.writeMetaClass(processingEnv.getFiler(), "Entrypoints", """
                public static List<Method> $entrypoints = new ArrayList<>();""", metaFile.toString(), "java.util.List",
            "java.util.ArrayList", "java.lang.reflect.Method");
        return false;
    }
}
