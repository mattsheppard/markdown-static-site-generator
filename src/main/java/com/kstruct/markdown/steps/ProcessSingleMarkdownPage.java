package com.kstruct.markdown.steps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.kstruct.markdown.model.MarkdownPage;
import com.kstruct.markdown.templating.MarkdownProcessor;
import com.kstruct.markdown.templating.MarkdownProcessorResult;
import com.kstruct.markdown.templating.TemplateProcessor;
import com.kstruct.markdown.utils.Markdown;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessSingleMarkdownPage implements Runnable {

    private final Path path;
    private final Path inputRoot;
    private final Path outputRoot;
    private final MarkdownProcessor markdownProcessor;
    private final TemplateProcessor templateProcessor;
    
    @Override
    public void run() {
		Path relativeOutputPath = outputRoot.resolve(inputRoot.relativize(path));

        Path outputPath = Markdown.renamePathForMarkdownPage(relativeOutputPath);
        
        String markdownContent;
        try {
            markdownContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // TODO need to do something with this
            throw new RuntimeException(e);
        }
        MarkdownProcessorResult processedMarkdown = markdownProcessor.process(markdownContent);
        String htmlContent = processedMarkdown.getRenderedContent();
        String title = Markdown.titleForMarkdownFile(path);
        String relativeUri = outputRoot.relativize(outputPath).toString();
        String relativeUriToRoot = outputPath.getParent().relativize(outputRoot).toString();
        if (!relativeUriToRoot.isEmpty()) {
            relativeUriToRoot += "/";
            // We need a trailing slash for building URLs from the doc's root
            // but Path doesn't give one (because it's pointing to the directory)
        }
        
        // TODO - use processedMarkdown.getLinkTargets() to check links
        
        String finalHtmlContent = templateProcessor.template(htmlContent, title, relativeUri, relativeUriToRoot);
        
        try {
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, finalHtmlContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // TODO need to do something with this
            throw new RuntimeException(e);
        }
    }
}
