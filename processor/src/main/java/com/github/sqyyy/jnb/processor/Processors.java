package com.github.sqyyy.jnb.processor;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

public class Processors {
    private Processors() {
    }

    public static void writeMetaClass(Filer filer, String name, String staticFields, String clinit, String... imports) {
        final StringBuilder importsBuilder = new StringBuilder();
        if (imports.length > 0) {
            importsBuilder.append('\n');
        }
        for (String anImport : imports) {
            importsBuilder.append("import ").append(anImport).append(";\n");
        }
        final StringBuilder builder = new StringBuilder();
        builder.append("""
            package $metadata.jnb;
            %s
            public class %s {
            %s
                static {
                    try {
            %s
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            """.formatted(importsBuilder.toString(), name, staticFields.indent(4), clinit.indent(12)));
        try {
            final JavaFileObject sourceFile = filer.createSourceFile("$metadata.jnb." + name);
            final Writer writer = sourceFile.openWriter();
            writer.write(builder.toString());
            writer.close();
        } catch (FilerException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
