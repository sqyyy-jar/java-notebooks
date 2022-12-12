package com.github.sqyyy.jnb.processor;

import com.github.sqyyy.jnb.Page;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.github.sqyyy.jnb.Page")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class PageProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final List<String> pageClasses = new ArrayList<>();
        final Collection<? extends Element> pageElements = roundEnv.getElementsAnnotatedWith(Page.class);
        final List<TypeElement> pageTypes = ElementFilter.typesIn(pageElements);
        for (final TypeElement type : pageTypes) {
            if (!type.getModifiers().contains(Modifier.PUBLIC)) {
                throw new RuntimeException(new IllegalAccessException("Page '" + type.getQualifiedName() + "' is not public"));
            }
            pageClasses.add(type.getQualifiedName().toString());
        }
        final StringBuilder metaFile = new StringBuilder();
        for (final String annotatedClass : pageClasses) {
            metaFile.append("$pages.add(Class.forName(\"");
            metaFile.append(annotatedClass);
            metaFile.append("\"));\n");
        }
        Processors.writeMetaClass(processingEnv.getFiler(), "Pages", """
                public static final List<Class<?>> $pages = new ArrayList<>();""", metaFile.toString(), "java.util.List",
            "java.util.ArrayList");
        return false;
    }
}
