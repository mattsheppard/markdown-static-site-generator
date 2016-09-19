package com.kstruct.markdown;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import com.kstruct.markdown.navigation.NavigationNode;
import com.kstruct.markdown.steps.BuildNavigationStructure;

public class StaticSiteGenerator {

    private Path inputDirectory;

    public StaticSiteGenerator(Path inputDirectory, Path outputDirectory, Path template, String siteName, String siteVersion,
        Boolean strictLinkChecking, Map<String, Object> extraConfig) {
        this.inputDirectory = inputDirectory;
    }

    public void run() {
        NavigationNode navigationRoot = new BuildNavigationStructure(inputDirectory).build();
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
