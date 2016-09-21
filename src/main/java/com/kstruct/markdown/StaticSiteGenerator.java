package com.kstruct.markdown;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.kstruct.markdown.model.SiteModelNode;
import com.kstruct.markdown.steps.BuildNavigationStructure;
import com.kstruct.markdown.steps.CopySimpleFiles;
import com.kstruct.markdown.steps.WriteProcessedMarkdownFiles;
import com.kstruct.markdown.templating.MarkdownRenderer;
import com.kstruct.markdown.templating.TemplateProcessor;

public class StaticSiteGenerator {

    private Path inputDirectory;
    private Path outputDirectory;
    private Path template;
    private String siteName;
    private Map<String, Object> extraConfig;

    public StaticSiteGenerator(Path inputDirectory, Path outputDirectory, Path template, String siteName,
        Boolean strictLinkChecking, Map<String, Object> extraConfig) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.template = template;
        this.siteName = siteName;
        this.extraConfig = extraConfig;
    }

    public void run() throws InterruptedException {
        SiteModelNode navigationRoot = new BuildNavigationStructure(inputDirectory).build();

        MarkdownRenderer markdownRenderer = new MarkdownRenderer();
        TemplateProcessor templateProcessor = new TemplateProcessor(template, navigationRoot, siteName, extraConfig);

        ExecutorService pool = Executors.newCachedThreadPool();
                
        new CopySimpleFiles().queueCopyOperations(inputDirectory, outputDirectory, pool);
        new WriteProcessedMarkdownFiles().queueConversionAndWritingOperations(inputDirectory, outputDirectory, markdownRenderer, templateProcessor, pool);
        
        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);

        // Lifecycle goes like this.
        // Read all of the inputDirectory in to navigation structure
        // Run preGenerateHook?
        // For each file in input:
        //   If it's markdown, convert it
        //   Template it
        //   Fix links in result (.md -> .html)
        //   Check it's internal links
        // Else
        //   Copy it over
    }

}
