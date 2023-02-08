package com.github.sqyyy.jnb.processor;

import com.github.sqyyy.jnb.Page;
import mjson.Json;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.Set;

@SupportedAnnotationTypes({"com.github.sqyyy.jnb.Page"})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class NotebookProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var pagesMeta = Json.array();
        final Collection<? extends Element> pageElements = roundEnv.getElementsAnnotatedWith(Page.class);
        var pageTypes = ElementFilter.typesIn(pageElements);
        if (pageTypes.size() == 0) {
            return false;
        }
        for (var type : pageTypes) {
            if (!type.getModifiers()
                .contains(Modifier.PUBLIC)) {
                processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "Page type '" + type.getQualifiedName() + "' is not public");
                throw new RuntimeException("Could not collect Page annotations");
            }
            pagesMeta = pagesMeta.add(Json.object()
                .set("name", type.getQualifiedName()
                    .toString())
                .set("entrypoints", EntrypointCollector.collect(processingEnv, type)));
        }
        Processors.writeJson(processingEnv, Json.object()
            .set("pages", pagesMeta));
        return false;
    }
}
