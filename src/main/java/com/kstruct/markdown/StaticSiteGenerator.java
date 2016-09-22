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
import com.kstruct.markdown.templating.MarkdownProcessor;
import com.kstruct.markdown.templating.TemplateProcessor;
import com.kstruct.markdown.utils.BrokenLinkRecorder;

public class StaticSiteGenerator {

    private Path inputDirectory;
    private Path outputDirectory;
    private Path template;
    private String siteName;
    private Map<String, Object> extraConfig;
    private Boolean strictLinkChecking;

    public StaticSiteGenerator(Path inputDirectory, Path outputDirectory, Path template, String siteName,
        Boolean strictLinkChecking, Map<String, Object> extraConfig) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.template = template;
        this.siteName = siteName;
        this.strictLinkChecking = strictLinkChecking;
        this.extraConfig = extraConfig;
    }

    public void run() throws InterruptedException {
        NavigationNode navigationRoot = new BuildNavigationStructure(inputDirectory).build();

        MarkdownProcessor markdownRenderer = new MarkdownProcessor();
        TemplateProcessor templateProcessor = new TemplateProcessor(template, navigationRoot, siteName, extraConfig);
        BrokenLinkRecorder brokenLinkRecorder = new BrokenLinkRecorder(inputDirectory);

        ExecutorService pool = Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors() * 2);
                
        new CopySimpleFiles().queueCopyOperations(inputDirectory, outputDirectory, pool);
        new ProcessAllMarkdownPages().queueConversionAndWritingOperations(inputDirectory, outputDirectory, markdownRenderer, templateProcessor, brokenLinkRecorder, pool);
        
        pool.shutdown();
        boolean terminated = pool.awaitTermination(60, TimeUnit.SECONDS);

        if (!terminated) {
            // TODO - Handle sensibly somehow
            throw new RuntimeException("Didn't terminate correctly.");
        }
        
        if (!brokenLinkRecorder.getAllBrokenLinks().isEmpty()) {
            for (Path key : brokenLinkRecorder.getAllBrokenLinks().keySet()) {
             // TODO - Logging sensibly
                System.err.println("Broken link(s) in " + key + ":\n  - " + String.join("\n  - ", brokenLinkRecorder.getAllBrokenLinks().get(key)));
            }
            System.err.flush();
            if (strictLinkChecking) {
                throw new Error("Some broken links detected - Throwing an error because strict link checking is enabled.");
            }
        }
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
