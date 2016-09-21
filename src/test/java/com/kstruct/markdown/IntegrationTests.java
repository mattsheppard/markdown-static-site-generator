package com.kstruct.markdown;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class IntegrationTests {

    @Test
    public void simpleEndToEndTest() throws IOException, InterruptedException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path config = fs.getPath("/root/config");
        Files.createDirectories(config);

        Path template = config.resolve("template.ftl");
        Files.write(template, "${siteName} ${content}".getBytes(StandardCharsets.UTF_8));

        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);

        Path output = fs.getPath("/root/output");
        Files.createDirectories(output);

        Path exampleCss = input.resolve("example.css");
        Files.write(exampleCss, "css".getBytes(StandardCharsets.UTF_8));

        Path exampleImage = input.resolve("subdir1/image1.jpg");
        Files.createDirectories(exampleImage.getParent());
        Files.write(exampleImage, "jpg".getBytes(StandardCharsets.UTF_8));
        
        Path exampleMarkdown1 = input.resolve("example1.md");
        Files.write(exampleMarkdown1, "# example 1".getBytes(StandardCharsets.UTF_8));

        Path exampleMarkdown2 = input.resolve("subdir2/example2.md");
        Files.createDirectories(exampleMarkdown2.getParent());
        Files.write(exampleMarkdown2, "# example 2".getBytes(StandardCharsets.UTF_8));

        Map<String, Object> extraConfig = new HashMap<>();
        extraConfig.put("example", "exampleValue");
        
        StaticSiteGenerator ssg = new StaticSiteGenerator(input, output, template, "Site Name", true, extraConfig);
        ssg.run();
        
        Path outputCss = output.resolve("example.css");
        Assert.assertEquals("css", new String(Files.readAllBytes(outputCss), StandardCharsets.UTF_8));

        Path outputJpg = output.resolve("subdir1/image1.jpg");
        Assert.assertEquals("jpg", new String(Files.readAllBytes(outputJpg), StandardCharsets.UTF_8));

        Path outputMarkdown1 = output.resolve("example1.html");
        Assert.assertEquals("Site Name <h1>example 1</h1>\n", new String(Files.readAllBytes(outputMarkdown1), StandardCharsets.UTF_8));

        Path outputMarkdown2 = output.resolve("subdir2/example2.html");
        Assert.assertEquals("Site Name <h1>example 2</h1>\n", new String(Files.readAllBytes(outputMarkdown2), StandardCharsets.UTF_8));
    }
    
}
