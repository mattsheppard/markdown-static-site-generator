package com.kstruct.markdown.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.kstruct.markdown.model.TocTree;
import com.kstruct.markdown.templating.MarkdownProcessor;
import com.kstruct.markdown.templating.MarkdownProcessorResult;
import com.kstruct.markdown.templating.TemplateProcessor;
import com.kstruct.markdown.utils.BrokenLinkRecorder;

public class ProcessSingleMarkdownPageTest {
    @Test
    public void testMarkdownFile() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);

        Path output = fs.getPath("/root/output");
        Files.createDirectories(output);

        Path inputMd = input.resolve("example.md");
        Files.write(inputMd, "Rendered markdown".getBytes(StandardCharsets.UTF_8));

        MarkdownProcessor markdownRenderer = mock(MarkdownProcessor.class);
        when(markdownRenderer.process(any(), any(), any())).thenReturn(new MarkdownProcessorResult("Rendered markdown", new TocTree(null, null)));
        
        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
        when(templateProcessor.template(eq("Rendered markdown"), eq("Example"), any(), eq("example.html"), eq(""))).thenReturn("Templated output");

        BrokenLinkRecorder brokenLinkRecorder = mock(BrokenLinkRecorder.class);

        ProcessSingleMarkdownPage processTask = new ProcessSingleMarkdownPage(inputMd, input, output, markdownRenderer, templateProcessor, brokenLinkRecorder);
        
        processTask.run();
        verify(templateProcessor).template(eq("Rendered markdown"), eq("Example"), any(), eq("example.html"), eq(""));
        
        Path outputPath = fs.getPath("/root/output/example.html");
        String outputContent = new String(Files.readAllBytes(outputPath), StandardCharsets.UTF_8);

        Assert.assertEquals("Templated output", outputContent);
    }
    
    @Test
    public void testDeepMarkdownFile() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);

        Path output = fs.getPath("/root/output");
        Files.createDirectories(output);

        Path deepInputMd = input.resolve("foo/bar/goo/gar/deep-example.md");
        Files.createDirectories(deepInputMd.getParent());
        Files.write(deepInputMd, "Rendered markdown".getBytes(StandardCharsets.UTF_8));

        MarkdownProcessor markdownRenderer = mock(MarkdownProcessor.class);
        when(markdownRenderer.process(any(), any(), any())).thenReturn(new MarkdownProcessorResult("Rendered markdown", new TocTree(null, null)));
        
        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
        when(templateProcessor.template(eq("Rendered markdown"), eq("Deep Example"), any(), eq("foo/bar/goo/gar/deep-example.html"), eq("../../../../"))).thenReturn("Templated output");
        
        BrokenLinkRecorder brokenLinkRecorder = mock(BrokenLinkRecorder.class);
        
        ProcessSingleMarkdownPage processTask = new ProcessSingleMarkdownPage(deepInputMd, input, output, markdownRenderer, templateProcessor, brokenLinkRecorder);
        
        processTask.run();
        verify(templateProcessor).template(eq("Rendered markdown"), eq("Deep Example"), any(), eq("foo/bar/goo/gar/deep-example.html"), eq("../../../../"));
        
        Path deepOutputPath = fs.getPath("/root/output/foo/bar/goo/gar/deep-example.html");
        String deepOutputContent = new String(Files.readAllBytes(deepOutputPath), StandardCharsets.UTF_8);

        Assert.assertEquals("Templated output", deepOutputContent);
    }
    
    // TODO - Ought to have a test confirming the population of the arrays passed into markdownRenderer
}
