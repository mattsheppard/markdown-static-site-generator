package com.kstruct.markdown.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.kstruct.markdown.templating.MarkdownRenderer;
import com.kstruct.markdown.templating.TemplateProcessor;

public class ProcessAndWriteSingleMarkdownPageTest {
    @Test
    public void testMarkdownFiles() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);

        Path output = fs.getPath("/root/output");
        Files.createDirectories(output);

        Path inputMd = input.resolve("example.md");
        Files.write(inputMd, "# Example heading 1".getBytes(StandardCharsets.UTF_8));

        MarkdownRenderer markdownRenderer = mock(MarkdownRenderer.class);
        when(markdownRenderer.render(any())).thenReturn("Rendered markdown");
        
        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
        when(templateProcessor.template(any())).thenReturn("Templated output");
        
        ProcessAndWriteSingleMarkdownPage processTask = new ProcessAndWriteSingleMarkdownPage(inputMd, input, output, markdownRenderer, templateProcessor);
        
        processTask.run();
        
        Path outputPath = fs.getPath("/root/output/example.html");
        String outputContent = new String(Files.readAllBytes(outputPath), StandardCharsets.UTF_8);

        Assert.assertEquals("Templated output", outputContent);
    }

}
