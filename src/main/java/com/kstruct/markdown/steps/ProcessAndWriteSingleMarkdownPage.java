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
        String title = path.getFileName().toString().replaceAll(Pattern.quote(MarkdownPage.MARKDOWN_FILE_EXTENSION) + "$", "").replace("-", " ");
        title = toTitleCase(title);
        String relativeUri = outputRoot.relativize(outputPath).toString();
        String relativeUriToRoot = outputPath.getParent().relativize(outputRoot).toString();
        if (!relativeUriToRoot.isEmpty()) {
            relativeUriToRoot += "/";
            // We need a trailing slash for building URLs from the doc's root
            // but Path doesn't give one (because it's pointing to the directory)
        }
        
        String finalHtmlContent = templateProcessor.template(htmlContent, title, relativeUri, relativeUriToRoot);
        
        try {
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, finalHtmlContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // TODO need to do something with this
            throw new RuntimeException(e);
        }
    }
    
    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

}
