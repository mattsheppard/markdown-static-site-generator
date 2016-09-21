package com.kstruct.markdown.steps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import com.kstruct.markdown.model.MarkdownPage;
import com.kstruct.markdown.templating.MarkdownRenderer;
import com.kstruct.markdown.templating.TemplateProcessor;
import com.kstruct.markdown.utils.Markdown;

public class WriteProcessedMarkdownFiles {

    public void queueConversionAndWritingOperations(Path inputRoot, Path outputRoot, MarkdownRenderer markdownRenderer, TemplateProcessor templateProcessor, ExecutorService pool) {
        queueConversionAndWritingOperationsInternal(inputRoot, inputRoot, outputRoot, markdownRenderer, templateProcessor, pool);
    }

    private void queueConversionAndWritingOperationsInternal(Path path, Path inputRoot, Path outputRoot, MarkdownRenderer markdownRenderer, TemplateProcessor templateProcessor, ExecutorService pool) {
        if (Files.isDirectory(path)) {
            // Recurse down the input tree
            try {
                Files.list(path).forEach(subPath -> {
                    queueConversionAndWritingOperationsInternal(subPath, inputRoot, outputRoot, markdownRenderer, templateProcessor, pool);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (Markdown.isMarkdownPage(path)) {
            pool.execute(new ProcessAndWriteSingleMarkdownPage(path, inputRoot, outputRoot, markdownRenderer, templateProcessor));
        }
    }

}
