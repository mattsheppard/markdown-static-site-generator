package com.kstruct.markdown.templating;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.kstruct.markdown.model.NavigationNode;
import com.kstruct.markdown.model.TocEntry;
import com.kstruct.markdown.model.TocTree;

public class TemplateProcessorTest {
	@Test
    public void testTemplateProcessor() throws IOException {
        String template = "<html>${siteName} ${title} ${toc.details.label} ${metadata.k2?last} ${relativeUri} ${relativeRootUri} ${extraConfig.extraConfigExample} ${content?no_esc}</html>";

        String result = templateSimpleDocument(template);
		
		Assert.assertEquals("<html>Site Name title label k2&amp;v2 relativeUri relativeRootUri extraConfigExampleValue example <hr> 漏斗回</html>", result);
    }

	@Test
    public void testExampleRealTemplate() throws IOException {
        String template = com.google.common.io.Files.toString(new File("example-real-template.ftl"), StandardCharsets.UTF_8);

        String result = templateSimpleDocument(template);
		
        // Just checking that there's no exception.
        // Really just to ensure the example template remains valid.
    }

	private String templateSimpleDocument(String template) throws IOException {
		FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path root = fs.getPath("/root");
        Files.createDirectories(root);
        
        Path ftl = root.resolve("example.ftl");
        Files.write(ftl, template.getBytes(StandardCharsets.UTF_8));

		String siteName = "Site Name";
		Map<String, Object> extraConfig = new HashMap<>();
		extraConfig.put("extraConfigExample", "extraConfigExampleValue");
		extraConfig.put("version", "1.2.3");
		
		TocTree toc = new TocTree(null, new TocEntry("label", 1));

		Map<String, List<String>> metadata = ImmutableMap.of(
				"k1", ImmutableList.of("k1v1", "k1v2"),
				"k2", ImmutableList.of("k2v1", "k2&v2"));
		
		NavigationNode navRoot = mock(NavigationNode.class);
		when(navRoot.getOutputPath()).thenReturn("example.html");
		when(navRoot.getTitle()).thenReturn("title");
		
		TemplateProcessor tp = new TemplateProcessor(ftl, navRoot, siteName, extraConfig);
		String result = tp.template("example <hr> 漏斗回", "title", toc, metadata, "relativeUri", "relativeRootUri");
		return result;
	}
}
