package com.kstruct.markdown;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate-static-site")
public class StaticSiteGeneratorMojo extends AbstractMojo {

    @Parameter( property = "inputDirectory")
    private Path inputDirectory;

    @Parameter( property = "outputDirectory")
    private Path outputDirectory;

    @Parameter( property = "siteName")
    private String siteName;

    @Parameter( property = "extraConfig")
    private Map<String,Object> extraConfig;

    @Parameter( property = "strictLinkChecking")
    private Boolean strictLinkChecking;

    @Parameter( property = "template")
    private Path template;

    public void execute() throws MojoExecutionException {
        getLog().info( "Will generate static site from " + inputDirectory + " into " + outputDirectory + " based on " + template );

        StaticSiteGenerator ssg = new StaticSiteGenerator(inputDirectory, outputDirectory, template, siteName, strictLinkChecking, extraConfig);
        try {
            ssg.run();
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Processing Interrupted", e);
        }
        getLog().info( "Finished generating doc" );
    }
    
}
