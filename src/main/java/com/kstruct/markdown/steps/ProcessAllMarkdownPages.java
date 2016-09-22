package com.kstruct.markdown.steps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import com.kstruct.markdown.model.MarkdownPage;
import com.kstruct.markdown.templating.MarkdownProcessor;
import com.kstruct.markdown.templating.TemplateProcessor;
import com.kstruct.markdown.utils.BrokenLinkRecorder;
import com.kstruct.markdown.utils.MarkdownUtils;

public class ProcessAllMarkdownPages {

    public void queueConversionAndWritingOperations(Path inputRoot, Path outputRoot, MarkdownProcessor markdownRenderer, TemplateProcessor templateProcessor, BrokenLinkRecorder brokenLinkRecorder, ExecutorService pool) {
        queueConversionAndWritingOperationsInternal(inputRoot, inputRoot, outputRoot, markdownRenderer, templateProcessor, brokenLinkRecorder, pool);
    }

    private void queueConversionAndWritingOperationsInternal(Path path, Path inputRoot, Path outputRoot, MarkdownProcessor markdownRenderer, TemplateProcessor templateProcessor, BrokenLinkRecorder brokenLinkRecorder, ExecutorService pool) {
        if (Files.isDirectory(path)) {
            // Recurse down the input tree
            try {
                Files.list(path).forEach(subPath -> {
                    queueConversionAndWritingOperationsInternal(subPath, inputRoot, outputRoot, markdownRenderer, templateProcessor, brokenLinkRecorder, pool);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (MarkdownUtils.isMarkdownPage(path)) {
            pool.execute(new ProcessSingleMarkdownPage(path, inputRoot, outputRoot, markdownRenderer, templateProcessor, brokenLinkRecorder));
        }
    }

}
