package com.kstruct.markdown.steps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.kstruct.markdown.model.MarkdownPage;
import com.kstruct.markdown.templating.ListingPageContentGenerator;
import com.kstruct.markdown.templating.MarkdownProcessor;
import com.kstruct.markdown.templating.TemplateProcessor;
import com.kstruct.markdown.utils.BrokenLinkRecorder;
import com.kstruct.markdown.utils.MarkdownUtils;

public class ProcessAllMarkdownPages {

    public List<Future<Boolean>> queueConversionAndWritingOperations(Path inputRoot, Path outputRoot, MarkdownProcessor markdownRenderer, TemplateProcessor templateProcessor, BrokenLinkRecorder brokenLinkRecorder, ListingPageContentGenerator listingPageContentGenerator, ExecutorService pool) {
        return queueConversionAndWritingOperationsInternal(inputRoot, inputRoot, outputRoot, markdownRenderer, templateProcessor, brokenLinkRecorder, listingPageContentGenerator, pool);
    }

    private List<Future<Boolean>> queueConversionAndWritingOperationsInternal(Path path, Path inputRoot, Path outputRoot, MarkdownProcessor markdownRenderer, TemplateProcessor templateProcessor, BrokenLinkRecorder brokenLinkRecorder, ListingPageContentGenerator listingPageContentGenerator, ExecutorService pool) {
		List<Future<Boolean>> result = new ArrayList<>();
        if (Files.isDirectory(path)) {
            // Recurse down the input tree
            try {
                Files.list(path).forEach(subPath -> {
                		result.addAll(
                			queueConversionAndWritingOperationsInternal(subPath, inputRoot, outputRoot, markdownRenderer, templateProcessor, brokenLinkRecorder, listingPageContentGenerator, pool)
                		);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (MarkdownUtils.isMarkdownPage(path)) {
            result.add(pool.submit(new ProcessSingleMarkdownPage(path, inputRoot, outputRoot, markdownRenderer, templateProcessor, brokenLinkRecorder, listingPageContentGenerator)));
        }
        return result;
    }

}
