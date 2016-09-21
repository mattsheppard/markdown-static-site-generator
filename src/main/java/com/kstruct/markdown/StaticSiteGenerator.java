package com.kstruct.markdown;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.kstruct.markdown.model.NavigationNode;
import com.kstruct.markdown.steps.BuildNavigationStructure;
import com.kstruct.markdown.steps.CopySimpleFiles;
import com.kstruct.markdown.steps.ProcessAllMarkdownPages;
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
        NavigationNode navigationRoot = new BuildNavigationStructure(inputDirectory).build();

        MarkdownRenderer markdownRenderer = new MarkdownRenderer();
        TemplateProcessor templateProcessor = new TemplateProcessor(template, navigationRoot, siteName, extraConfig);

        ExecutorService pool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() * 2);
                
        new CopySimpleFiles().queueCopyOperations(inputDirectory, outputDirectory, pool);
        new ProcessAllMarkdownPages().queueConversionAndWritingOperations(inputDirectory, outputDirectory, markdownRenderer, templateProcessor, pool);
        
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
    
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 5) {
            System.out.println("Usage: inputDirectory outputDirectory template siteName strictLinkChecking");
            return;
        }
        System.out.println("Start: " + new Date().toString());
        Map<String, Object> extraConfig = new HashMap<>();
        extraConfig.put("version", "9.9.9");
        
        new StaticSiteGenerator(
            Paths.get(args[0]) /* inputDirectory */, 
            Paths.get(args[1]) /* outputDirectory */, 
            Paths.get(args[2]) /* template */, 
            args[3] /* siteName */, 
            Boolean.parseBoolean(args[4]) /* strictLinkChecking */, 
            extraConfig)
        .run();
        System.out.println("Finish: " + new Date().toString());
    }

}
