package com.kstruct.markdown.steps;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.kstruct.markdown.model.MarkdownPage;
import com.kstruct.markdown.templating.MarkdownRenderer;
import com.kstruct.markdown.templating.TemplateProcessor;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessAndWriteSingleMarkdownPage implements Runnable {

    private final Path path;
    private final Path inputRoot;
    private final Path outputRoot;
    private final MarkdownRenderer markdownRenderer;
    private final TemplateProcessor templateProcessor;
    
    @Override
    public void run() {
        Path outputPath = outputRoot.resolve(inputRoot.relativize(path));
        // TODO - Eliminate redundancy with MarkdownPage here
        outputPath = outputPath.getParent().resolve(outputPath.getFileName().toString().replaceAll(Pattern.quote(MarkdownPage.MARKDOWN_FILE_EXTENSION) + "$", MarkdownPage.HTML_OUTPUT_FILE_EXTENSION));
        
        String markdownContent;
        try {
            markdownContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // TODO need to do something with this
            throw new RuntimeException(e);
        }
        String htmlContent = markdownRenderer.render(markdownContent);
        
        String finalHtmlContent = templateProcessor.template(htmlContent);
        
        try {
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, finalHtmlContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // TODO need to do something with this
            throw new RuntimeException(e);
        }
    }

}
