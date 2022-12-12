package com.github.sqyyy.jnb.processor;

import com.github.sqyyy.jnb.Entrypoint;
import com.github.sqyyy.jnb.Page;
import com.github.sqyyy.jnb.processor.error.IllegalSignatureException;
import com.github.sqyyy.jnb.processor.error.MissingAnnotationException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
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
            final Name typeName = type.getQualifiedName();
            final Name methodName = method.getSimpleName();
            if (typeName.isEmpty()) {
                throw new IllegalSignatureException("Type is an anonymous class");
            }
            if (methodName.isEmpty()) {
                throw new IllegalSignatureException("Method is an anonymous method");
            }
            final Page annotation = type.getAnnotation(Page.class);
            if (annotation == null) {
                throw new MissingAnnotationException('@' + Page.class.getName() + " is missing on '" + typeName + '\'');
            }
            if (!type.getModifiers().contains(Modifier.PUBLIC)) {
                throw new IllegalSignatureException("Entrypoint-class '" + typeName + "' must be public");
            }
            if (!method.getModifiers().contains(Modifier.PUBLIC) || !method.getModifiers().contains(Modifier.STATIC)) {
                throw new IllegalSignatureException("Entrypoint '" + methodName + "' must be public and static");
            }
            final List<? extends VariableElement> parameters = method.getParameters();
            if (parameters.size() > 1) {
                throw new IllegalSignatureException(
                    "Entrypoint '" + methodName + "' may only contain up to one parameter (String[])");
            }
            if (parameters.size() == 0) {
                entrypointMethods.add(new JnbMethod(typeName.toString(), methodName.toString(), false));
                continue;
            }
            final TypeMirror argsParameter = parameters.get(0).asType();
            if (!processingEnv.getTypeUtils().isSameType(argsParameter, processingEnv.getTypeUtils()
                .getArrayType(processingEnv.getElementUtils().getTypeElement("java.lang.String").asType()))) {
                throw new RuntimeException(new IllegalAccessException(
                    "Entrypoint '" + methodName + "' may only contain string-array as parameter (String[])"));
            }
            entrypointMethods.add(new JnbMethod(typeName.toString(), methodName.toString(), true));
        }
        final StringBuilder metaFile = new StringBuilder();
        metaFile.append("""
            final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            Class<?> var0 = null;
            MethodHandle var1 = null;
            """);
        for (final JnbMethod entrypointMethod : entrypointMethods) {
            metaFile.append("var0 = Class.forName(\"");
            metaFile.append(entrypointMethod.clazz());
            metaFile.append("\");\nvar1 = lookup.findStatic(var0, \"");
            metaFile.append(entrypointMethod.method());
            metaFile.append('"');
            if (entrypointMethod.args()) {
                metaFile.append(", MethodType.methodType(void.class, String[].class)");
            }
            metaFile.append(");\n$entrypoints.put(var0, var1);\n");
        }
        Processors.writeMetaClass(processingEnv.getFiler(), "Entrypoints",
            "public static final Map<Class<?>, MethodHandle> $entrypoints = new HashMap<>();", metaFile.toString(),
            "java.lang.invoke.MethodHandle",
            "java.lang.invoke.MethodHandles",
            "java.lang.invoke.MethodType",
            "java.util.HashMap",
            "java.util.Map");
        return false;
    }
}
