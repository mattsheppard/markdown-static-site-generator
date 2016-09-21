package com.kstruct.markdown.steps;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;

import com.kstruct.markdown.model.MarkdownPage;

public class CopySimpleFiles {

    public void queueCopyOperations(Path inputDirectory, Path outputDirectory, ExecutorService pool) {
        queueCopyOperationsInternal(inputDirectory, inputDirectory, outputDirectory, pool);
    }

    private void queueCopyOperationsInternal(Path path, Path inputRoot, Path outputRoot, ExecutorService pool) {
        if (Files.isDirectory(path)) {
            // Recurse down the input tree
            try {
                Files.list(path).forEach(subPath -> {
                    queueCopyOperationsInternal(subPath, inputRoot, outputRoot, pool);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (path.getFileName().toString().endsWith(MarkdownPage.MARKDOWN_FILE_EXTENSION)) {
            // Do nothing for markdown files
        } else {
            pool.execute(() -> {
                Path outputPath = outputRoot.resolve(inputRoot.relativize(path));
                
                try {
                    Files.createDirectories(outputPath.getParent());
                    Files.copy(path, outputPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    // TODO - Probably need to do something real with this case
                    throw new RuntimeException(e);
                }
            });
        }
    }

    
}
