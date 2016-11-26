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

    @Parameter( property = "inputDirectory", defaultValue = "${project.basedir}/src/main/resources/markdown")
    private File inputDirectory;

    @Parameter( property = "outputDirectory", defaultValue = "${project.build.directory}/generated-site")
    private File outputDirectory;

    @Parameter( property = "siteName")
    private String siteName;

    @Parameter( property = "extraConfig")
    private Map<String,Object> extraConfig;

    @Parameter( property = "strictLinkChecking", defaultValue = "true")
    private Boolean strictLinkChecking;

    @Parameter( property = "template", defaultValue = "${project.basedir}/src/main/resources/template.ftl")
    private File template;

    public void execute() throws MojoExecutionException {
        getLog().info( "Will generate static site from " + inputDirectory + " into " + outputDirectory + " based on " + template );

        StaticSiteGenerator ssg = new StaticSiteGenerator(inputDirectory.toPath(), outputDirectory.toPath(), template.toPath(), siteName, strictLinkChecking, extraConfig);
        try {
            ssg.run();
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Processing Interrupted", e);
        }
        getLog().info( "Finished generating doc" );
    }
    
}
