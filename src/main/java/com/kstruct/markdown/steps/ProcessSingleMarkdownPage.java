package com.kstruct.markdown.steps;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.kstruct.markdown.model.TocEntry;
import com.kstruct.markdown.model.TocTree;
import com.kstruct.markdown.templating.ListingPageContentGenerator;
import com.kstruct.markdown.templating.ListingPageContentGenerator.ListingPageContent;
import com.kstruct.markdown.templating.MarkdownProcessor;
import com.kstruct.markdown.templating.MarkdownProcessorResult;
import com.kstruct.markdown.templating.TemplateProcessor;
import com.kstruct.markdown.utils.BrokenLinkRecorder;
import com.kstruct.markdown.utils.MarkdownUtils;
import com.kstruct.markdown.utils.PathUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessSingleMarkdownPage implements Runnable {

    private final Path path;
    private final Path inputRoot;
    private final Path outputRoot;
    private final MarkdownProcessor markdownProcessor;
    private final TemplateProcessor templateProcessor;
    private final BrokenLinkRecorder brokenLinkRecorder;
    private final ListingPageContentGenerator listingPageContentGenerator;
    
    @Override
    public void run() {
		Path relativeOutputPath = outputRoot.resolve(inputRoot.relativize(path));

        Path outputPath = MarkdownUtils.renamePathForMarkdownPage(relativeOutputPath);
        
        ListingPageContent listingPageContent = new ListingPageContent();
        
        if (path.endsWith(MarkdownUtils.DIRECTORY_INDEX_FILE_NAME + MarkdownUtils.MARKDOWN_FILE_EXTENSION)) {
            // On index pages, we need to pass the sub-pages and sub-categories through
            listingPageContent = listingPageContentGenerator.getListingPageContent(path);
        }
        
        String markdownContent;
        try {
            markdownContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            // TODO need to do something with this
            throw new RuntimeException(e);
        }
        MarkdownProcessorResult processedMarkdown = markdownProcessor.process(markdownContent, listingPageContent.getPages(), listingPageContent.getCategories());
        String htmlContent = processedMarkdown.getRenderedContent();
        TocTree toc = processedMarkdown.getToc();
        String title = PathUtils.titleForPath(path, inputRoot);
        String relativeUri = outputRoot.relativize(outputPath).toString();
        String relativeUriToRoot = outputPath.getParent().relativize(outputRoot).toString();
        if (!relativeUriToRoot.isEmpty()) {
            relativeUriToRoot += "/";
            // We need a trailing slash for building URLs from the doc's root
            // but Path doesn't give one (because it's pointing to the directory)
        }
        
        brokenLinkRecorder.recordBrokenMarkdownLinks(path, processedMarkdown.getLinkTargets());
        
        String finalHtmlContent = templateProcessor.template(htmlContent, title, toc, relativeUri, relativeUriToRoot);
        
        try {
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, finalHtmlContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // TODO need to do something with this
            throw new RuntimeException(e);
        }
    }


}
