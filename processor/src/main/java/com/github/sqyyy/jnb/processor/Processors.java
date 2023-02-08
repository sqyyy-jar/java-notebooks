package com.github.sqyyy.jnb.processor;

import mjson.Json;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.UncheckedIOException;

public class Processors {
    private Processors() {
    }

    private static FileObject createResource(ProcessingEnvironment env) {
        var filer = env.getFiler();
        try {
            return filer.createResource(StandardLocation.CLASS_OUTPUT, "", "metadata.jnb.json");
        } catch (IOException e) {
            env.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, "An exception occurred whilst creating metadata file");
            throw new UncheckedIOException(e);
        }
    }

    public static void writeJson(ProcessingEnvironment env, Json json) {
        var metaFile = createResource(env);
        try (var writer = metaFile.openWriter()) {
            writer.write(json.toString());
            writer.flush();
        } catch (IOException e) {
            env.getMessager()
                .printMessage(Diagnostic.Kind.ERROR, "An exception occurred whilst writing metadata file");
            throw new UncheckedIOException(e);
        }
    }
}
