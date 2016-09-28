package com.kstruct.markdown.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.kstruct.markdown.model.TocTree;
import com.kstruct.markdown.templating.MarkdownProcessor;
import com.kstruct.markdown.templating.MarkdownProcessorResult;
import com.kstruct.markdown.templating.TemplateProcessor;
import com.kstruct.markdown.utils.BrokenLinkRecorder;

public class ProcessAllMarkdownPagesTest {
    @Test
    public void testCopyingFiles() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path input = fs.getPath("/root/input");
        Files.createDirectories(input);

        Path output = fs.getPath("/root/output");
        Files.createDirectories(output);

        Path exampleMarkdown1 = input.resolve("example1.md");
        Files.write(exampleMarkdown1, "# example 1".getBytes(StandardCharsets.UTF_8));

        Path exampleMarkdown2 = input.resolve("subdir/example2.md");
        Files.createDirectories(exampleMarkdown2.getParent());
        Files.write(exampleMarkdown2, "# example 2".getBytes(StandardCharsets.UTF_8));
        
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ExecutorService pool = mock(ExecutorService.class);
        // This is not ideal - We depend on the internal detail of calling execute on the pool
        // where as there are many other possible pool methods which could be used instead.
        Mockito.doNothing().when(pool).execute(runnableCaptor.capture());
        
        MarkdownProcessor markdownRenderer = mock(MarkdownProcessor.class);
        when(markdownRenderer.process(any())).thenReturn(new MarkdownProcessorResult("Rendered markdown", new TocTree(null, null)));
        
        TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
        when(templateProcessor.template(any(), any(), any(), any(), any())).thenReturn("Templated output");

        BrokenLinkRecorder brokenLinkRecorder = mock(BrokenLinkRecorder.class);

        ProcessAllMarkdownPages wpmf = new ProcessAllMarkdownPages();
        wpmf.queueConversionAndWritingOperations(input, output, markdownRenderer, templateProcessor, brokenLinkRecorder, pool);

        // Should not exist yet - Should be created by the Runnable, not during the queue call
        Assert.assertFalse(Files.exists(output.resolve("example1.html")));
        Assert.assertFalse(Files.exists(output.resolve("subdir/example2.html")));
        
        List<Runnable> runnables = runnableCaptor.getAllValues();
        Assert.assertEquals(2, runnables.size());
        runnables.forEach(r -> r.run());
        
        Assert.assertTrue(Files.exists(output.resolve("example1.html")));
        Assert.assertTrue(Files.exists(output.resolve("subdir/example2.html")));

        Assert.assertEquals("Templated output", new String(Files.readAllBytes(output.resolve("example1.html")), StandardCharsets.UTF_8));
        Assert.assertEquals("Templated output", new String(Files.readAllBytes(output.resolve("subdir/example2.html")), StandardCharsets.UTF_8));
    }
}
