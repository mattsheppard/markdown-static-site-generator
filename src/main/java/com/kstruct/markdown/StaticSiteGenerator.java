package com.kstruct.markdown;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import com.kstruct.markdown.model.SiteModelNode;
import com.kstruct.markdown.steps.BuildNavigationStructure;
import com.kstruct.markdown.steps.SetMarkdownRenderer;
import com.kstruct.markdown.steps.SetMarkdownTemplateProcessor;
import com.kstruct.markdown.templating.TemplateProcessor;

public class StaticSiteGenerator {

    private Path inputDirectory;
    private TemplateProcessor templateProcessor;
    private Path template;
    private String siteName;
    private Map<String, Object> extraConfig;

    public StaticSiteGenerator(Path inputDirectory, Path outputDirectory, Path template, String siteName,
        Boolean strictLinkChecking, Map<String, Object> extraConfig) {
        this.inputDirectory = inputDirectory;
        this.template = template;
        this.siteName = siteName;
        this.extraConfig = extraConfig;
    }

    public void run() {
        SiteModelNode root = new BuildNavigationStructure(inputDirectory).build();
        
        // We give the markdown pages some info about how they should be generated
        new SetMarkdownRenderer().process(root);
        new SetMarkdownTemplateProcessor(template, siteName, extraConfig).process(root);

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
