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

import com.google.common.collect.ImmutableMap;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.kstruct.markdown.model.TocTree;
import com.kstruct.markdown.templating.ListingPageContentGenerator;
import com.kstruct.markdown.templating.ListingPageContentGenerator.ListingPageContent;
import com.kstruct.markdown.testing.utils.MockFilesystemUtils;
import com.kstruct.markdown.testing.utils.MockFilesystemUtils.FileWithContent;
import com.kstruct.markdown.templating.MarkdownProcessor;
import com.kstruct.markdown.templating.MarkdownProcessorResult;
import com.kstruct.markdown.templating.TemplateProcessor;
import com.kstruct.markdown.utils.BrokenLinkRecorder;

public class ProcessSingleMarkdownPageTest {
    @Test
    public void testMarkdownFile() throws IOException {
		FileSystem fs = MockFilesystemUtils.createMockFileSystem(new FileWithContent[]{
				new FileWithContent("example.md", "Rendered markdown".getBytes(StandardCharsets.UTF_8))
		});
		
		Path inputMd = fs.getPath("/root/input/example.md");
		String expectedTitle = "Example";
		String expectedInputContent = "Rendered markdown";
		String expectedOutputPath = "example.html";
		String expectedPathToRoot = "";
		String desiredRenderedOutput = "Templated output";

		processSinglePage(fs, inputMd, expectedTitle, expectedInputContent, expectedOutputPath,
			expectedPathToRoot, desiredRenderedOutput);
        
        Path outputPath = fs.getPath("/root/output/example.html");
        String outputContent = new String(Files.readAllBytes(outputPath), StandardCharsets.UTF_8);

        Assert.assertEquals("Templated output", outputContent);
    }
    
    @Test
    public void testDeepMarkdownFile() throws IOException {
		FileSystem fs = MockFilesystemUtils.createMockFileSystem(new FileWithContent[]{
				new FileWithContent("foo/bar/goo/gar/deep-example.md", "Rendered markdown".getBytes(StandardCharsets.UTF_8))
		});
		Path deepInputMd = fs.getPath("/root/input/foo/bar/goo/gar/deep-example.md");

		String expectedTitle = "Deep Example";
		String expectedInputContent = "Rendered markdown";
		String expectedOutputPath = "foo/bar/goo/gar/deep-example.html";
		String expectedPathToRoot = "../../../../";
		String desiredRenderedOutput = "Templated output";

		processSinglePage(fs, deepInputMd, expectedTitle, expectedInputContent, expectedOutputPath,
			expectedPathToRoot, desiredRenderedOutput);
        
        Path deepOutputPath = fs.getPath("/root/output/foo/bar/goo/gar/deep-example.html");
        String deepOutputContent = new String(Files.readAllBytes(deepOutputPath), StandardCharsets.UTF_8);

        Assert.assertEquals("Templated output", deepOutputContent);
    }

    @Test
    public void testIndexTitle() throws IOException {
    		FileSystem fs = MockFilesystemUtils.createMockFileSystem(new FileWithContent[]{
    				new FileWithContent("foo/bar/goo/gar/index.md", "Rendered markdown".getBytes(StandardCharsets.UTF_8))
    		});
    		Path directoryIndexInputMd = fs.getPath("/root/input/foo/bar/goo/gar/index.md");

    		String expectedTitle = "Gar";
    		String expectedInputContent = "Rendered markdown";
    		String expectedOutputPath = "foo/bar/goo/gar/index.html";
    		String expectedPathToRoot = "../../../../";
    		String desiredRenderedOutput = "Templated output";

		processSinglePage(fs, directoryIndexInputMd, expectedTitle, expectedInputContent, expectedOutputPath,
				expectedPathToRoot, desiredRenderedOutput);
        
        Path deepOutputPath = fs.getPath("/root/output/foo/bar/goo/gar/index.html");
        String deepOutputContent = new String(Files.readAllBytes(deepOutputPath), StandardCharsets.UTF_8);

        Assert.assertEquals("Templated output", deepOutputContent);
    }

	private void processSinglePage(FileSystem fs, Path markdownPath, String expectedTitle, String expectedInputContent,
			String expectedOutputPath, String expectedPathToRoot, String desiredRenderedOutput) {
		Path input = fs.getPath("/root/input");
		Path output = fs.getPath("/root/output");
		
		MarkdownProcessor markdownRenderer = mock(MarkdownProcessor.class);
		when(markdownRenderer.process(any(), any())).thenReturn(
				new MarkdownProcessorResult(expectedInputContent, new TocTree(null, null), ImmutableMap.of()));

		TemplateProcessor templateProcessor = mock(TemplateProcessor.class);
		when(templateProcessor.template(eq(expectedInputContent), eq(expectedTitle), any(), any(),
				eq(expectedOutputPath), eq(expectedPathToRoot))).thenReturn(desiredRenderedOutput);

		BrokenLinkRecorder brokenLinkRecorder = mock(BrokenLinkRecorder.class);

		ListingPageContentGenerator listingPageContentGenerator = mock(ListingPageContentGenerator.class);
		when(listingPageContentGenerator.getListingPageContent(any())).thenReturn(new ListingPageContent());

		ProcessSingleMarkdownPage processTask = new ProcessSingleMarkdownPage(markdownPath, input, output,
				markdownRenderer, templateProcessor, brokenLinkRecorder, listingPageContentGenerator);

		processTask.call();
		verify(templateProcessor).template(eq(expectedInputContent), eq(expectedTitle), any(), any(),
				eq(expectedOutputPath), eq(expectedPathToRoot));
	}

    // TODO - Ought to have a test confirming the population of the arrays passed into markdownRenderer
}
